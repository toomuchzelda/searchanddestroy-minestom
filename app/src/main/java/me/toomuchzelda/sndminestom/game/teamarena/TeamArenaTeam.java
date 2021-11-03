package me.toomuchzelda.sndminestom.game.teamarena;

import net.minestom.server.coordinate.Pos;

public class TeamArenaTeam
{
	private TeamColours teamColour;
	private Pos[] spawns;
	
	public TeamArenaTeam(TeamColours teamColour)
	{
		this.teamColour = teamColour;
		spawns = null;
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
}
