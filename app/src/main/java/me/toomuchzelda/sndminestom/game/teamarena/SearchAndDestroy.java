package me.toomuchzelda.sndminestom.game.teamarena;

import net.minestom.server.instance.InstanceContainer;

public class SearchAndDestroy extends TeamArena
{
	public final String mapPath = super.mapPath + "/SND";
	
	public SearchAndDestroy(InstanceContainer instance) {
		super(instance);
	}
	
	@Override
	public void tick()
	{
	
	}
	
	@Override
	public String mapPath()
	{
		return super.mapPath() + "/SND";
	}
}
