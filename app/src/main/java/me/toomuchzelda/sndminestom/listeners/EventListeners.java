package me.toomuchzelda.sndminestom.listeners;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.teamarena.TeamArena;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;

public class EventListeners
{
	public EventListeners() {
		GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
		
		//Player join instance event
		// REmove later when i have an actual Hub!!
		eventHandler.addListener(AddEntityToInstanceEvent.class, event -> {
			if(event.getEntity() instanceof CustomPlayer cPlayer) {
				//Main.getLogger().info("caught customPlayer joining in event");
				Game game = Main.getInstanceGame(event.getInstance());
				cPlayer.setGame(game);
				if(game instanceof TeamArena teamArena) {
					teamArena.queueJoiningPlayer(cPlayer);
				}
			}
		});
		
		eventHandler.addListener(RemoveEntityFromInstanceEvent.class, event -> {
			if(event.getEntity() instanceof CustomPlayer cPlayer) {
				//Main.getLogger().info("caught customPlayer joining in event");
				Game game = Main.getInstanceGame(event.getInstance());
				cPlayer.setGame(game);
				if(game instanceof TeamArena teamArena) {
					teamArena.cleanUpPlayer(cPlayer);
				}
			}
		});
	}
}
