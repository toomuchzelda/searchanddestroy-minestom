package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.core.CustomPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import net.minestom.server.event.player.PlayerStopSneakingEvent;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.utils.MathUtils;

import java.lang.reflect.Field;

public class HoloNameTracker
{
	private TeamArena game;
	
	public HoloNameTracker(TeamArena game) {
		this.game = game;
		
		GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
		
		//have the armor stand follow players around
		eventHandler.addListener(PlayerMoveEvent.class, event -> {
			CustomPlayer player = (CustomPlayer) event.getPlayer();
			double height = player.getBoundingBox().getHeight();
			player.getNametag().setPosition(player.getPosition().add(0, height, 0));
		});
		
		eventHandler.addListener(PlayerStartSneakingEvent.class, event -> {
			((CustomPlayer) event.getPlayer()).getNametag().getEntity().setSneaking(true);
		});
		
		eventHandler.addListener(PlayerStopSneakingEvent.class, event -> {
			((CustomPlayer) event.getPlayer()).getNametag().getEntity().setSneaking(false);
		});
	}
	
	public static void createName(CustomPlayer player) {
		//player must be on a TeamArenaTeam before using this on them
		Component name = Component.text(player.getUsername())
				.color(player.getTeamArenaTeam().getTeamColour().getRGBTextColor());
		
		Hologram hologram = new Hologram(player.getGame().getInstance(), player.getPosition(), name, true, true);
		player.setNametag(hologram);
		
		//only those who can view the player can view the armour stand
		hologram.getEntity().updateViewableRule(player1 -> {
			if(player.isViewer(player1))
				return true;
			else
				return false;
		});
	}
	
}
