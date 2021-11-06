package me.toomuchzelda.sndminestom.game;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.core.MathUtils;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

import java.io.File;

public abstract class Game
{
	public long gameTick;
	protected InstanceContainer instance;
	protected AnvilLoader anvilLoader;
	protected GameState gameState;
	protected GameType nextGame = GameType.KOTH;
	private String lobbyName;
	
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
		parseConfig(chosenMapName + "\\config.yml");
		
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
}
