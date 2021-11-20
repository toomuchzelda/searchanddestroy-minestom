package me.toomuchzelda.sndminestom.listeners;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.GameState;
import me.toomuchzelda.sndminestom.game.teamarena.TeamArena;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.player.*;

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
				Game game = Main.getInstanceGame(event.getInstance());
				if(game instanceof TeamArena teamArena) {
					teamArena.cleanUpPlayer(cPlayer);
				}
			}
		});
		
		//freeze players in game starting time but allow jumping and turning around
		eventHandler.addListener(PlayerMoveEvent.class, event -> {
			CustomPlayer player = (CustomPlayer) event.getPlayer();
			if(player.getGame() instanceof TeamArena) {
				TeamArena tm = (TeamArena) player.getGame();
				if(tm.getGameState() == GameState.GAME_STARTING) {
					Pos currentPos = event.getPlayer().getPosition();
					Pos newPos = event.getNewPosition();
					if(currentPos.x() != newPos.x() || currentPos.z() != newPos.z())
						event.setCancelled(true);
				}
			}
		});
		
		eventHandler.addListener(PlayerBlockBreakEvent.class, event -> {
			event.setCancelled(true);
		});
		
		//make nametag follow them
		eventHandler.addListener(PlayerTickEvent.class, event -> {
			CustomPlayer player = (CustomPlayer) event.getPlayer();
			
			//should be resized based on pose in Main.java
			double height = player.getBoundingBox().getHeight();
			if(player.isSneaking())
				height -= 0.11; //trial and error
			
			Pos toTp = player.getPosition().add(0, height, 0);
			if(!player.getNametag().getPosition().equals(toTp))
				player.getNametag().getEntity().teleport(player.getPosition().add(0, height, 0));
		});
		
		//make their nametag shift/not shift
		eventHandler.addListener(PlayerStartSneakingEvent.class, event -> {
			//((CustomPlayer) event.getPlayer()).getNametag().getEntity().setSneaking(true);
			((CustomPlayer) event.getPlayer()).getNametag().getEntity().setSneaking(true);
		});
		
		eventHandler.addListener(PlayerStopSneakingEvent.class, event -> {
			//((CustomPlayer) event.getPlayer()).getNametag().getEntity().setSneaking(false);
			((CustomPlayer) event.getPlayer()).getNametag().getEntity().setSneaking(false);
		});
	}
}
