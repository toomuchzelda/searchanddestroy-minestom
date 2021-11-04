package me.toomuchzelda.sndminestom.core;

import me.toomuchzelda.sndminestom.core.ranks.Rank;
import me.toomuchzelda.sndminestom.game.teamarena.TeamArenaTeam;
import me.toomuchzelda.sndminestom.game.teamarena.TeamColours;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomPlayer extends Player
{
	//team they're on if any
	private TeamArenaTeam team;
	private Rank rank;
	
	public CustomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection)
	{
		super(uuid, username, playerConnection);
		team = null;
		rank = Rank.PLAYER;
	}
	
	public void setGameTeam(TeamArenaTeam team) {
		this.team = team;
	}
	
	public TeamArenaTeam getTeamArenaTeam() {
		return this.team;
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public void setRank(Rank rank) {
		this.rank = rank;
	}
}
