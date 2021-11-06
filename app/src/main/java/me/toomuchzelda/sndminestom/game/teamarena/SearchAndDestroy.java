package me.toomuchzelda.sndminestom.game.teamarena;

import net.minestom.server.instance.InstanceContainer;

public class SearchAndDestroy extends TeamArena
{
	public final String mapPath = super.mapPath + "/SND";
	
	public SearchAndDestroy(InstanceContainer instance, String name) {
		super(instance, name);
	}
	
	@Override
	public void tick()
	{
		super.tick();
	}
	
	@Override
	public String mapPath()
	{
		return super.mapPath() + "/SND";
	}
	
	@Override
	public boolean canSelectKitNow() {
		return gameState.isPreGame();
	}
	
	@Override
	public void parseConfig(String filename) {
		super.parseConfig(filename);
	}
}
