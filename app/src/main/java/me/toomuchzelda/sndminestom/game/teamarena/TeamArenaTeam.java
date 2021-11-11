package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.core.CustomPlayer;
import net.minestom.server.coordinate.Pos;

import java.util.ArrayList;
import java.util.LinkedList;

public class TeamArenaTeam
{
	private TeamColours teamColour;
	private Pos[] spawns;
	//only add to from this object's encapsulating TeamArena object's tick() method
	private LinkedList<CustomPlayer> players = new LinkedList<>();
	
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
