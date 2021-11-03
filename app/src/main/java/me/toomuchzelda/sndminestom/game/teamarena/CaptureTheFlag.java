package me.toomuchzelda.sndminestom.game.teamarena;

import net.minestom.server.instance.InstanceContainer;

public class CaptureTheFlag extends TeamArena
{
	public final String mapPath = super.mapPath + "/CTF";
	
	public CaptureTheFlag(InstanceContainer instance, String name) {
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
		return super.mapPath() + "/CTF";
	}
	
	@Override
	public void parseConfig(String filename) {
		super.parseConfig(filename);
	}
}
