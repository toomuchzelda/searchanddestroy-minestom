package me.toomuchzelda.sndminestom.game.teamarena;

public class TeamArenaOptions
{
	public final TeamArenaOption<Boolean> RESPAWNING;
	public final TeamArenaOption<Boolean> MID_GAME_JOINING;
	public final TeamArenaOption<Boolean> BALANCE_TEAMS;
	
	public TeamArenaOptions() {
		RESPAWNING = new TeamArenaOption<>("Respawning", "Players respawn after dying", false, false);
		
		MID_GAME_JOINING = new TeamArenaOption<>("Mid Game Joining", "Allow players to join while"
				+ " game is in progress. Requires respawning to be true", false, false);
		
		BALANCE_TEAMS = new TeamArenaOption<>("Balance teams", "Enforce player limits on teams before game starts", true, true);
	}
}
