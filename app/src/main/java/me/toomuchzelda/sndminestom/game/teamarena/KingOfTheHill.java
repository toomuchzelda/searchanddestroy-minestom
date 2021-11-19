package me.toomuchzelda.sndminestom.game.teamarena;

import net.minestom.server.instance.InstanceContainer;

public class KingOfTheHill extends TeamArena
{
	public final String mapPath = super.mapPath + "/KOTH";
	
	public KingOfTheHill(InstanceContainer instance, String name)
	{
		super(instance, name);
		teamArenaOptions.RESPAWNING.value = true;
		teamArenaOptions.MID_GAME_JOINING.value = true;
		teamArenaOptions.BALANCE_TEAMS.value = true;
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
	
	@Override
	public boolean canSelectKitNow() {
		return !gameState.isEndGame();
	}
	
	@Override
	public void parseConfig(String filename) {
		super.parseConfig(filename);
	}
}
