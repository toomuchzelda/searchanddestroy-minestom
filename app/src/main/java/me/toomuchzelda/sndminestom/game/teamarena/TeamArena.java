package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.BlockStuff;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.core.MathUtils;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.GameState;
import me.toomuchzelda.sndminestom.game.teamarena.kits.Kit;
import me.toomuchzelda.sndminestom.game.teamarena.kits.KitNone;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.packet.server.play.SoundEffectPacket;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.block.BlockUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class TeamArena extends Game
{
	protected final String mapPath = "Maps/TeamArena";
	protected TeamArenaTeam[] teams;
	
	protected ConcurrentLinkedQueue<CustomPlayer> joiningQueue = new ConcurrentLinkedQueue<>();
	protected ConcurrentLinkedQueue<CustomPlayer> leavingQueue = new ConcurrentLinkedQueue<>();
	
	private Kit[] kits;
	private ConcurrentHashMap<UUID, Kit> chosenKits = new ConcurrentHashMap<>();
	protected ItemStack kitMenuItem;
	
	//ticks of wait time before teams are decided
	protected static final int preTeamsTime = 25 * 20;
	//ticks of wait time after teams chosen, before game starting phase
	protected static final int preGameStartingTime = 30 * 20;
	//ticks of game starting time
	protected static final int gameStartingTime = 10 * 20;
	protected static final int totalWaitingTime = preTeamsTime + preGameStartingTime + gameStartingTime;
	
	protected static final int minPlayersRequired = 1;
	protected long waitingSince;
	protected boolean teamsDecided = false;
	
	protected TeamsPacketsManager teamsPacketsManager;
	protected TeamArenaOptions teamArenaOptions;
	
	protected MapBorder border;
	protected Pos spawnPos;
	
	public TeamArena(InstanceContainer instance, String name)
	{
		super(instance, name);
		
		//Select kit item
		kitMenuItem = ItemStack.builder(Material.FEATHER).amount(1)
				.displayName(Component.text("Select a kit").color(NamedTextColor.BLUE)
						.decoration(TextDecoration.ITALIC, false)).build();
	
		kits = new Kit[]{new KitNone()};
		
		this.teamsPacketsManager = new TeamsPacketsManager(this);
		teamArenaOptions = new TeamArenaOptions();
		waitingSince = 0;
	}
	
	
	@Override
	public void tick() {
		super.tick();
		
		while(!leavingQueue.isEmpty()) {
			CustomPlayer player = leavingQueue.remove();
			players.remove(player);
			//sync cleanup tasks..
		}
		
		//process players that just joined
		while(!joiningQueue.isEmpty()) {
			CustomPlayer player = joiningQueue.remove();
			//Main.getLogger().info("Processing queue " + player.getUsername());
			players.add(player);
			
			giveLobbyItems(player);
			player.refreshCommands();
			teamsPacketsManager.sendCreatePackets(player);
			if((gameState.isPreGame() && teamsDecided) ||
					(teamArenaOptions.RESPAWNING.value && teamArenaOptions.MID_GAME_JOINING.value)) {
				//put them on team with lowest players, or if already balanced, on lowest scoring teams
				int lowest = Integer.MAX_VALUE;
				TeamArenaTeam lowestTeam = teams[0];
				for(TeamArenaTeam team : teams) {
					if(team.getEntityMembers().size() < lowest) {
						lowest = team.getEntityMembers().size();
						lowestTeam = team;
					}
				}
				lowestTeam.addMembers(player);
			}
			player.setRespawnPoint(spawnPos);
			player.teleport(spawnPos);
		}
		
		//tick for each gamestate
		if(gameState.isPreGame())
		{
			//if countdown is ticking, do announcements
			if(players.size() >= minPlayersRequired) {
				//announce Game starting in:
				// and play sound
				sendCountdown(false);
				if(waitingSince + preTeamsTime == gameTick) {
					//set teams here
					setupTeams();
					teamsDecided = true;
					gameState = GameState.TEAMS_CHOSEN;
					
					for (Player p : instance.getPlayers())
					{
						CustomPlayer cp = (CustomPlayer) p;
						//SoundEffectPacket packet = SoundEffectPacket.create(Sound.Source.AMBIENT, SoundEvent.ENTITY_CREEPER_DEATH, p.getPosition(), 99999, 0);
						cp.sendMessage(Component.text("Teams have been decided!").color(NamedTextColor.RED));
						String name = cp.getTeamArenaTeam().getTeamColour().getName();
						TextColor colour = cp.getTeamArenaTeam().getTeamColour().getRGBTextColor();
						cp.sendMessage(Component.text("You are on " + name).color(colour));
						SoundEffectPacket packet = SoundEffectPacket.create(Sound.Source.AMBIENT,
								SoundEvent.BLOCK_NOTE_BLOCK_BELL, cp.getPosition(), 1f, 0.5f);
						cp.sendPacket(packet);
						Main.getLogger().info("Decided Teams");
						//p.sendPacket(packet);
					}
					
					sendCountdown(true);
				}
				//Game starting; teleport everyone to spawns and freeze them
				else if(waitingSince + preTeamsTime + preGameStartingTime == gameTick) {
					//teleport players to team spawns
					for(TeamArenaTeam team : teams) {
						int i = 0;
						Pos[] spawns = team.getSpawns();
						for(Entity e : team.getEntityMembers()) {
							e.teleport(spawns[i % spawns.length]);
							team.spawnsTaken[i] = true;
							i++;
						}
					}
					
					//EventListeners.java should stop them from moving
					gameState = GameState.GAME_STARTING;
				}
				//start game
				else if(waitingSince + totalWaitingTime == gameTick)
				{
					gameState = GameState.LIVE;
					
				}
			}
			else {
				waitingSince = gameTick;
				gameState = GameState.PREGAME;
				if(teamsDecided) {
					//remove players from all teams (and send packets)
					for(TeamArenaTeam team : teams) {
						team.removeAllMembers();
					}
				}
				teamsDecided = false;
			}
		}
	}
	
	public void setupTeams() {
		//shuffle order of teams first so certain teams don't always get the odd player(s)
		TeamArenaTeam[] teamArray = Arrays.copyOf(teams, teams.length);
		MathUtils.shuffleArray(teamArray);
		Player[] shuffledPlayers = players.toArray(new Player[0]);
		if(shuffledPlayers.length > 1)
			MathUtils.shuffleArray(shuffledPlayers);
		
		int offset = 0;
		for(TeamArenaTeam team : teamArray)
		{
			LinkedList<Player> playersOnThisTeam = new LinkedList<>();
			for(int i = offset; i < shuffledPlayers.length; i = i + teamArray.length) {
				playersOnThisTeam.add(shuffledPlayers[i]);
			}
			offset++;
			team.addMembers(playersOnThisTeam.toArray(new Player[0]));
		}
	}
	
	public void cleanUpPlayer(CustomPlayer player) {
		teamsPacketsManager.sendDestroyPackets(player);
		
		//for sync-required tasks
		leavingQueue.add(player);
	}
	
	@Override
	public String mapPath() {
		return "Maps/TeamArena";
	}
	
	public void queueJoiningPlayer(CustomPlayer player) {
		joiningQueue.add(player);
	}
	
	public void queueLeavingPlayer(CustomPlayer player) {
		leavingQueue.add(player);
	}
	
	public void giveLobbyItems(CustomPlayer player) {
		PlayerInventory inventory = player.getInventory();
		inventory.setItemStack(0, kitMenuItem);
	}
	
	public abstract boolean canSelectKitNow();
	
	public void selectKit(CustomPlayer player, String kitName) {
		boolean found = false;
		for(int i = 0; i < kits.length; i++) {
			if(kits[i].getName().equalsIgnoreCase(kitName)) {
				chosenKits.put(player.getUuid(), kits[i]);
				found = true;
				player.sendMessage(Component.text("Using kit " + kitName).color(NamedTextColor.BLUE));
				//play sound
				SoundEffectPacket packet = SoundEffectPacket.create(Sound.Source.PLAYER,
						SoundEvent.BLOCK_NOTE_BLOCK_PLING, player.getPosition(), 1f, 2f);
				player.sendPacket(packet);
				break;
			}
		}
		
		if(!found)
			player.sendMessage(Component.text("Kit " + kitName + " doesn't exist").color(NamedTextColor.RED));
	}
	
	public Kit[] getKits() {
		return kits;
	}
	
	public TeamArenaTeam[] getTeams() {
		return teams;
	}
	
	//This method assumes everything in config is set up correctly
	@Override
	public void parseConfig(String filename) {
		Yaml yaml = new Yaml();
		Main.getLogger().info("Reading config YAML: " + filename);
		try
		{
			FileInputStream fileStream = new FileInputStream(filename);
			Map<String, Object> map = yaml.load(fileStream);
			Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
			while(iter.hasNext()) {
				System.out.println(iter.next().toString());
			}
			
			//Create the teams
			//Key = team e.g RED, BLUE. value = Map:
			//		key = "Spawns" value: ArrayList<String>
			Map<String, Map<String, ArrayList<String>>> teamsMap =
					(Map<String, Map<String, ArrayList<String>>>) map.get("Teams");
			
			int numOfTeams = teamsMap.size();
			teams = new TeamArenaTeam[numOfTeams];
			int teamsArrIndex = 0;
			
			Iterator<Map.Entry<String, Map<String, ArrayList<String>>>> teamsIter = teamsMap.entrySet().iterator();
			while(teamsIter.hasNext()) {
				Map.Entry<String, Map<String, ArrayList<String>>> entry = teamsIter.next();
				String teamName = entry.getKey();
				
				Map<String, ArrayList<String>> spawnsYaml = entry.getValue();
				ArrayList<String> spawnsList = spawnsYaml.get("Spawns");
				
				TeamColours teamColour = TeamColours.valueOf(teamName);
				TeamArenaTeam teamArenaTeam = new TeamArenaTeam(teamColour, this);
				Pos[] positionArray = new Pos[spawnsList.size()];
				
				int index = 0;
				for(String loc : spawnsList) {
					double[] coords = BlockStuff.parseCoords(loc, 0.5, 0, 0.5);
					Pos pos = new Pos(coords[0], coords[1], coords[2]);
					positionArray[index] = pos;
					index++;
				}
				teamArenaTeam.setSpawns(positionArray);
				teams[teamsArrIndex] = teamArenaTeam;
				teamsArrIndex++;
			}
			
			//Map border
			// Only supports rectangular prism borders as of now
			ArrayList<String> borders = (ArrayList<String>) map.get("Border");
			double[] corner1 = BlockStuff.parseCoords(borders.get(0), 0, 0, 0);
			double[] corner2 = BlockStuff.parseCoords(borders.get(1), 0, 0, 0);
			border = new MapBorder(corner1, corner2);
			Main.getLogger().info("MapBorder: " + border.toString());
			
			//calculate spawnpoint based on map border
			Vec centre = border.getCentre();
			/*Vec spawnpoint = BlockStuff.getFloor(centre, instance);
			//if not safe to spawn just spawn them in the sky
			if(spawnpoint == null) {
				spawnpoint = new Vec(centre.x(), 255, centre.z());
			}*/
			centre = BlockStuff.getHighestBlock(centre,instance);
			spawnPos = centre.asPosition().withYaw(90f).add(0, 1, 0);
			Main.getLogger().info("spawnPos: " + spawnPos.toString());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public void sendCountdown(boolean force) {
		if((gameTick - waitingSince) % 20 == 0 || force)
		{
			long timeLeft;
			//how long until teams are chosen
			if(gameState == GameState.PREGAME) {
				timeLeft = (waitingSince + preTeamsTime) - gameTick;
			}
			else {
				timeLeft = (waitingSince + totalWaitingTime) - gameTick;
			}
			timeLeft /= 20;
			//is a multiple of 30, is 15, is between 10 and 1 inclusive , AND is not 0
			// OR is just forced
			if(((timeLeft % 30 == 0 || timeLeft == 15 || timeLeft == 10 ||
					(timeLeft <= 5 && timeLeft >= 1 && gameState == GameState.GAME_STARTING)) && timeLeft != 0) || force)
			{
				for (Player p : players)
				{
					String s;
					if(gameState == GameState.PREGAME)
						s = "Teams will be chosen in ";
					else
						s = "Game starting in ";
					SoundEffectPacket packet = SoundEffectPacket.create(Sound.Source.AMBIENT, SoundEvent.ENTITY_CREEPER_DEATH, p.getPosition(), 99999, 0);
					p.sendMessage(Component.text(s + timeLeft + 's').color(NamedTextColor.RED));
					p.sendPacket(packet);
				}
			}
		}
	}
}
