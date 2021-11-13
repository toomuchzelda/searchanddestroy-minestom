package me.toomuchzelda.sndminestom.game.teamarena;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.TeamsPacket;

public class TeamsPacketsManager
{
	private TeamArena game;
	
	public TeamsPacketsManager(TeamArena game) {
		this.game = game;
	}
	
	public void sendTeamUpdatePackets() {
		for(TeamArenaTeam team : game.getTeams()) {
			final TeamsPacket packet = new TeamsPacket();
			
			packet.teamName = team.getTeamColour().getName();
			packet.action = TeamsPacket.Action.UPDATE_TEAM_INFO;
			packet.teamDisplayName = Component.text(team.getTeamColour().getName())
					.color(team.getTeamColour().getRGBTextColor());
			packet.friendlyFlags = friendlyFireAndInvisBits(true, true);
			packet.nameTagVisibility = TeamsPacket.NameTagVisibility.NEVER;
			packet.collisionRule = TeamsPacket.CollisionRule.NEVER;
			packet.teamColor = NamedTextColor.nearestTo(team.getTeamColour().getRGBTextColor());
			packet.teamPrefix = Component.text(team.getTeamColour().getName()).color(team.getTeamColour().getRGBTextColor());
			
			for(Player p : game.getInstance().getPlayers()) {
				p.sendPacket(packet);
			}
		}
	}
	
	public void sendCreatePackets(Player player) {
		TeamsPacket[] packets = constructCreatePackets();
		for(TeamsPacket packet : packets) {
			player.sendPacket(packet);
		}
	}
	
	public void sendDestroyPackets(Player player) {
		for(TeamArenaTeam team : game.getTeams()) {
			player.sendPacket(team.createRemoveTeamPacket());
		}
	}
	
	private TeamsPacket[] constructCreatePackets() {
		TeamsPacket[] teams = new TeamsPacket[game.getTeams().length];
		
		TeamArenaTeam[] team = game.getTeams();
		for(int i = 0; i < team.length; i++) {
			TeamsPacket packet = team[i].createCreateTeamPacket();
			teams[i] = packet;
		}
		
		return teams;
	}
	
	//private TeamsPacket[] constructAddMemberPackets() {
	
	//}
	
	
	public static byte friendlyFireAndInvisBits(boolean friendlyFire, boolean seeInvisTeammates) {
		byte b = 0;
		
		if(friendlyFire)
			b |= 0x01;
		
		if(seeInvisTeammates)
			b |= 0x02;
		
		return b;
	}
}
