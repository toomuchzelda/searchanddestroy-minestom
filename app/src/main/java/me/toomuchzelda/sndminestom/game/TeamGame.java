package me.toomuchzelda.sndminestom.game;

import net.minestom.server.instance.InstanceContainer;

public abstract class TeamGame extends Game
{
	
	public TeamGame(InstanceContainer instance, String name)
	{
		super(instance, name);
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
}
