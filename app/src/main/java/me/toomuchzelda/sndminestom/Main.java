/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package me.toomuchzelda.sndminestom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.core.commands.CommandStop;
import me.toomuchzelda.sndminestom.core.ranks.Rank;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.teamarena.KingOfTheHill;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.network.ConnectionManager;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;

public class Main {
 
	public static Game currentGame;
	private static final Logger logger = Logger.getLogger("SNDMinestom");
	
	private static final ConcurrentHashMap<InstanceContainer, Game> gameInstances = new ConcurrentHashMap<>();
	
	public static void main(String[] args) {
		MinecraftServer mcServer = MinecraftServer.init();
		
		//Create the first game
		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		InstanceContainer instance = instanceManager.createInstanceContainer();
		Game firstGame = new KingOfTheHill(instance, "Official Lobby");
		gameInstances.put(instance, firstGame);
		
		
		ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
		connectionManager.setPlayerProvider(CustomPlayer::new);
		
		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
		globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
			final Player player = event.getPlayer();
			event.setSpawningInstance(instance);
			player.setRespawnPoint(new Pos(0, 100, 0));
			if(player.getName().equals(Component.text("toomuchzelda"))) {
				((CustomPlayer) player).setRank(Rank.OPERATOR);
			}
		});
		
		globalEventHandler.addListener(PlayerSkinInitEvent.class, event -> {
			//PlayerSkin skin = PlayerSkin.fromUuid(event.getPlayer().getUuid().toString());
			PlayerSkin skin = PlayerSkin.fromUsername("toomuchzelda");
			event.setSkin(skin);
		});
		
		globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
			//event.getPlayer().setGameMode(GameMode.CREATIVE);
			/*event.getPlayer().sendMessage(Component.text("asdf"));
			event.getPlayer().sendMessage(Component.text("DARK_RED").color(NamedTextColor.DARK_RED));
			event.getPlayer().sendMessage(Component.text("RED").color(NamedTextColor.RED));
			event.getPlayer().sendMessage(Component.text("DARK_BLUE").color(NamedTextColor.DARK_BLUE));*/
		});
		
		//Run the game ticks on every instance tick
		globalEventHandler.addListener(InstanceTickEvent.class, instanceTickEvent -> {
			gameInstances.get(instanceTickEvent.getInstance()).tick();
		});
		
		registerCommands();
		
		mcServer.start("127.0.0.1", 25565);
	}
	
	/*private static Game createGame(GameType gameType, InstanceContainer instance) {
		if(nextGameType == null) {
			int random = MathUtils.randomRange(0, 2);
			//nextGameType = GameType.values()[random];
			nextGameType = GameType.KOTH;
		}
		
		Game nextGame;
		if(nextGameType == GameType.KOTH)
			nextGame = new KingOfTheHill(instance);
		else if(nextGameType == GameType.CTF)
			nextGame = new CaptureTheFlag(instance);
		else
			nextGame = new SearchAndDestroy(instance); //else if SND
		
		nextGameType = null;
		return nextGame;
	}*/
	
	public static Logger getLogger() {
		return logger;
	}
	
	private static void registerCommands() {
		CommandManager manager = MinecraftServer.getCommandManager();
		manager.register(new CommandStop());
	}
}
