package me.toomuchzelda.sndminestom.game.teamarena;

import net.minestom.server.instance.InstanceContainer;

public class KingOfTheHill extends TeamArena
{
	public final String mapPath = super.mapPath + "/KOTH";
	
	public KingOfTheHill(InstanceContainer instance)
	{
		super(instance);
	}
	
	@Override
	public void tick()
	{
		super.tick();
	}
	
	@Override
	public String mapPath()
	{
		return super.mapPath() + "/KOTH";
	}
}
