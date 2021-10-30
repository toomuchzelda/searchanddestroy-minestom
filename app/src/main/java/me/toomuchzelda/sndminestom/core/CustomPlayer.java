package me.toomuchzelda.sndminestom.core;

import me.toomuchzelda.sndminestom.game.GameTeam;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomPlayer extends Player
{
	private GameTeam gameTeam;
	
	public CustomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection)
	{
		super(uuid, username, playerConnection);
		gameTeam = null;
	}
	
	public void setGameTeam(GameTeam team) {
		this.gameTeam = team;
	}
	
	public GameTeam getGameTeam() {
		return this.gameTeam;
	}
}
