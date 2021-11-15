package me.toomuchzelda.sndminestom.game;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.core.MathUtils;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Game
{
	protected long gameTick;
	protected InstanceContainer instance;
	protected AnvilLoader anvilLoader;
	protected GameState gameState;
	protected GameType nextGame = GameType.KOTH;
	private String lobbyName;
	protected Set<CustomPlayer> players;
	
	public Game(InstanceContainer instance, String name) {
		this.instance = instance;
		
		//Read + load maps
		Main.getLogger().info("Reading maps from: " + this.mapPath());
		File[] maps = new File(mapPath()).listFiles();
		for(File map : maps) {
			Main.getLogger().info(map.getAbsolutePath() + " " + map.getName());
		}
		int rand = 0;
		if(maps.length > 1) {
			rand = MathUtils.randomMax(maps.length - 1);
		}
		String chosenMapName = maps[rand].getAbsolutePath();
		Main.getLogger().info("Loading Map: " + chosenMapName);
		anvilLoader = new AnvilLoader(chosenMapName);
		instance.setChunkLoader(anvilLoader);
		parseConfig(chosenMapName + "/config.yml");
		
		//maintain own set of players, to avoid problems when using instance.getPlayers().
		// a player may not have been initialized within the game yet.
		players = ConcurrentHashMap.newKeySet();
		
		gameTick = 0;
		gameState = GameState.PREGAME;
		lobbyName = name;
	}
	
	public void tick() {
		gameTick++;
		
	}
	
	public abstract String mapPath();
	
	//parse map config, unique per game
	public abstract void parseConfig(String filename);
	
	public GameState getGameState() {
		return gameState;
	}
	
	public InstanceContainer getInstance() {
		return instance;
	}
	
	public Set<CustomPlayer> getPlayers() {
		return players;
	}
}
