package me.toomuchzelda.sndminestom.core;

import me.toomuchzelda.sndminestom.core.ranks.Rank;
import me.toomuchzelda.sndminestom.game.teamarena.TeamColours;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomPlayer extends Player
{
	private TeamColours teamColour;
	private Rank rank;
	
	public CustomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection)
	{
		super(uuid, username, playerConnection);
		teamColour = null;
		rank = Rank.PLAYER;
	}
	
	public void setGameTeam(TeamColours team) {
		this.teamColour = team;
	}
	
	public TeamColours getGameTeam() {
		return this.teamColour;
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public void setRank(Rank rank) {
		this.rank = rank;
	}
}
