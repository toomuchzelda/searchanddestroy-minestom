package me.toomuchzelda.sndminestom.game.teamarena;

public class TeamArenaOptions
{
	public final TeamArenaOption RESPAWNING;
	public final TeamArenaOption MID_GAME_JOINING;
	
	public TeamArenaOptions() {
		RESPAWNING = new TeamArenaOption("Respawning", "Players respawn after dying", false, false);
		MID_GAME_JOINING = new TeamArenaOption("Mid Game Joining", "Allow players to join while" +
				" game is in progress. Requires respawning to be true", false, false);
	}
}
