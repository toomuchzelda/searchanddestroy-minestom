package me.toomuchzelda.sndminestom.core;

import me.toomuchzelda.sndminestom.core.ranks.Rank;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.teamarena.TeamArenaTeam;
import me.toomuchzelda.sndminestom.game.teamarena.kits.Kit;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomPlayer extends Player
{
	//team they're on if any
	private TeamArenaTeam team;
	//Kit, if any
	private Kit kit;
	private Rank rank;
	private Game game;
	private Hologram nametag;
	
	public CustomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection)
	{
		super(uuid, username, playerConnection);
		team = null;
		rank = Rank.PLAYER;
	}
	
	public void setTeamArenaTeam(TeamArenaTeam team) {
		this.team = team;
	}
	
	public TeamArenaTeam getTeamArenaTeam() {
		return this.team;
	}
	
	//use these instead of entity addViewer
	public void addNameViewer(CustomPlayer player) {
		nametag.addViewer(player);
		this.addViewer(player);
	}
	
	public void removeNameViewer(CustomPlayer player) {
		nametag.removeViewer(player);
		this.removeViewer(player);
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public void setRank(Rank rank) {
		this.rank = rank;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public Kit getKit() {
		return kit;
	}
	
	public void setKit(Kit kit) {
		this.kit = kit;
	}
	
	public Hologram getNametag() {
		return nametag;
	}
	
	public void setNametag(Hologram nametag) {
		this.nametag = nametag;
	}
}
