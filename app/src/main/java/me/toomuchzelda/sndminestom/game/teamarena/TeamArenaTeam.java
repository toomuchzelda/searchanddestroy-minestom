package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class TeamArenaTeam
{
	private TeamColours teamColour;
	private Pos[] spawns;
	//only add to from this object's encapsulating TeamArena object's tick() method
	private LinkedList<String> members = new LinkedList<>();
	private TeamArena teamArena;
	
	public TeamArenaTeam(TeamColours teamColour, TeamArena game)
	{
		this.teamColour = teamColour;
		spawns = null;
		this.teamArena = game;
	}
	
	public Pos[] getSpawns() {
		return spawns;
	}
	
	public void setSpawns(Pos[] array) {
		this.spawns = array;
	}
	
	public TeamColours getTeamColour() {
		return teamColour;
	}
	
	//Things like making sure one entity isnt on two teams is done in TeamArena class
	public void addMembers(Entity... entities) {
		String[] names = new String[entities.length];
		for(int i = 0; i < entities.length; i++) {
			Entity entity = entities[i];
			
			if (entity instanceof Player) {
				members.add(((Player) entity).getUsername());
				names[i] = ((Player) entity).getUsername();
			}
			else {
				members.add(entity.getUuid().toString());
				names[i] = entity.getUuid().toString();
			}
		}
		
		final TeamsPacket addPacket = new TeamsPacket();
		addPacket.teamName = getTeamColour().getName();
		addPacket.action = TeamsPacket.Action.ADD_PLAYERS_TEAM;
		addPacket.entities = names;
		
		for(Player p : teamArena.getInstance().getPlayers()) {
			p.sendPacket(addPacket);
		}
	}
	
	public void removeMembers(Entity... entities) {
		String[] names = new String[entities.length];
		for(int i = 0; i < entities.length; i++) {
			Entity entity = entities[i];
			
			if (entity instanceof Player) {
				members.remove(((Player) entity).getUsername());
				names[i] = ((Player) entity).getUsername();
			}
			else {
				members.remove(entity.getUuid().toString());
				names[i] = entity.getUuid().toString();
			}
		}
		
		final TeamsPacket removePacket = new TeamsPacket();
		removePacket.teamName = getTeamColour().getName();
		removePacket.action = TeamsPacket.Action.REMOVE_PLAYERS_TEAM;
		removePacket.entities = names;
		
		for(Player p : teamArena.getInstance().getPlayers()) {
			p.sendPacket(removePacket);
		}
	}
	
	public TeamsPacket createRemoveTeamPacket() {
		final TeamsPacket removePacket = new TeamsPacket();
		removePacket.teamName = teamColour.getName();
		removePacket.action = TeamsPacket.Action.REMOVE_TEAM;
		return removePacket;
	}
	
	public LinkedList<String> getMembers() {
		return members;
	}
	
	public String[] getTeamsPacketEntities() {
		return members.toArray(new String[0]);
	}
	
}
