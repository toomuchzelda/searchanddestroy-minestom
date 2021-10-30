package me.toomuchzelda.sndminestom.game;

import me.toomuchzelda.sndminestom.Main;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

public abstract class Game
{
	public long gameTick = 0;
	protected InstanceContainer instance;
	protected AnvilLoader anvilLoader;
	
	public Game(InstanceContainer instance) {
		this.instance = instance;
		//Main.getLogger().info(mapPath);
		//anvilLoader = new AnvilLoader(mapPath);
		//instance.setChunkLoader(anvilLoader);
	}
	
	public abstract void tick();
	
	public abstract String mapPath();
}
