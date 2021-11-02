package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.game.TeamGame;
import net.minestom.server.instance.InstanceContainer;

public abstract class TeamArena extends TeamGame
{
	protected final String mapPath = "Maps/TeamArena";
	
	public TeamArena(InstanceContainer instance)
	{
		super(instance);
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public String mapPath() {
		return "Maps/TeamArena";
	}
}
