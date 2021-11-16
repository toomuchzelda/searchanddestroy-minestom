package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamArenaTeam
{
	private TeamColours teamColour;
	private Pos[] spawns;
	//only add to from this object's encapsulating TeamArena object's tick() method
	private Set<String> members = ConcurrentHashMap.newKeySet();
	private Set<Entity> entityMembers = ConcurrentHashMap.newKeySet();
	
	//in the rare case a player joins during GAME_STARTING, need to find an unused spawn position
	// to teleport to
	public int spawnsIndex;
	
	//abstract score value, game-specific
	public int score;
	
	//reference to game instance
	private TeamArena teamArena;
	
	public TeamArenaTeam(TeamColours teamColour, TeamArena game)
	{
		this.teamColour = teamColour;
		spawns = null;
		this.teamArena = game;
		score = 0;
	}
	
	public Pos[] getSpawns() {
		return spawns;
	}
	
	public void setSpawns(Pos[] array) {
		this.spawns = array;
		this.spawnsIndex = 0;
	}
	
	public TeamColours getTeamColour() {
		return teamColour;
	}
	
	public void addMembers(Entity... entities) {
		String[] names = new String[entities.length];
		for(int i = 0; i < entities.length; i++) {
			Entity entity = entities[i];
			
			if (entity instanceof CustomPlayer) {
				CustomPlayer cp = (CustomPlayer) entity;
				//if they're already on a team
				// remove them from that team and update the reference in their own class
				if(cp.getTeamArenaTeam() != null) {
					cp.getTeamArenaTeam().removeMembers(cp);
				}
				cp.setTeamArenaTeam(this);
				members.add(cp.getUsername());
				names[i] = cp.getUsername();
			}
			else {
				members.add(entity.getUuid().toString());
				names[i] = entity.getUuid().toString();
			}
			entityMembers.add(entity);
		}
		
		final TeamsPacket addPacket = new TeamsPacket();
		addPacket.teamName = getTeamColour().getName();
		addPacket.action = TeamsPacket.Action.ADD_PLAYERS_TEAM;
		addPacket.entities = names;
		
		for(Player p : teamArena.getPlayers()) {
			p.sendPacket(addPacket);
		}
	}
	
	public void removeMembers(Entity... entities) {
		String[] names = new String[entities.length];
		for(int i = 0; i < entities.length; i++) {
			Entity entity = entities[i];
			
			if (entity instanceof CustomPlayer cp) {
				members.remove(cp.getUsername());
				names[i] = cp.getUsername();
				cp.setTeamArenaTeam(null);
			}
			else {
				members.remove(entity.getUuid().toString());
				names[i] = entity.getUuid().toString();
			}
			entityMembers.remove(entity);
		}
		
		final TeamsPacket removePacket = new TeamsPacket();
		removePacket.teamName = getTeamColour().getName();
		removePacket.action = TeamsPacket.Action.REMOVE_PLAYERS_TEAM;
		removePacket.entities = names;
		
		for(Player p : teamArena.getPlayers()) {
			p.sendPacket(removePacket);
		}
	}
	
	public void removeAllMembers() {
		String[] names = members.toArray(new String[0]);
		members.clear();
		for(Entity e : entityMembers) {
			if(e instanceof CustomPlayer cp) {
				cp.setTeamArenaTeam(null);
			}
		}
		entityMembers.clear();
		
		final TeamsPacket removePacket = new TeamsPacket();
		removePacket.teamName = getTeamColour().getName();
		removePacket.action = TeamsPacket.Action.REMOVE_PLAYERS_TEAM;
		removePacket.entities = names;
		
		for(Player p : teamArena.getPlayers()) {
			p.sendPacket(removePacket);
		}
	}
	
	public TeamsPacket createCreateTeamPacket() {
		TeamsPacket packet = new TeamsPacket();
		
		packet.teamName = getTeamColour().getName();
		packet.action = TeamsPacket.Action.CREATE_TEAM;
		packet.teamDisplayName = Component.text(getTeamColour().getName())
				.color(getTeamColour().getRGBTextColor());
		packet.friendlyFlags = TeamsPacketsManager.friendlyFireAndInvisBits(true, true);
		packet.nameTagVisibility = TeamsPacket.NameTagVisibility.NEVER;
		packet.collisionRule = TeamsPacket.CollisionRule.NEVER;
		packet.teamColor = NamedTextColor.nearestTo(getTeamColour().getRGBTextColor());
		packet.teamPrefix = Component.text(getTeamColour().getName()).color(getTeamColour().getRGBTextColor());
		packet.entities = getTeamsPacketEntities();
		
		return packet;
	}
	
	public TeamsPacket createRemoveTeamPacket() {
		final TeamsPacket removePacket = new TeamsPacket();
		removePacket.teamName = teamColour.getName();
		removePacket.action = TeamsPacket.Action.REMOVE_TEAM;
		return removePacket;
	}
	
	public Set<String> getMembers() {
		return members;
	}
	
	public Set<Entity> getEntityMembers() {
		return entityMembers;
	}
	
	public String[] getTeamsPacketEntities() {
		return members.toArray(new String[0]);
	}
	
}
