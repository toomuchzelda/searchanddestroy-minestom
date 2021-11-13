package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.core.MathUtils;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.GameState;
import me.toomuchzelda.sndminestom.game.teamarena.kits.Kit;
import me.toomuchzelda.sndminestom.game.teamarena.kits.KitNone;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.SoundEffectPacket;
import net.minestom.server.sound.SoundEvent;
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
	protected boolean teamsDecided = false;
	
	protected ConcurrentLinkedQueue<CustomPlayer> joiningQueue = new ConcurrentLinkedQueue<>();
	protected ConcurrentLinkedQueue<CustomPlayer> leavingQueue = new ConcurrentLinkedQueue<>();
	
	private Kit[] kits;
	private ConcurrentHashMap<UUID, Kit> chosenKits = new ConcurrentHashMap<>();
	protected ItemStack kitMenuItem;
	
	//ticks of wait time before teams are decided
	protected static final int preTeamsTime = 25 * 20;
	//ticks of wait time after teams chosen, before game starting phase
	protected static final int preGameStartingTime = 25 * 20;
	//ticks of game starting time
	protected static final int gameStartingTime = 10 * 20;
	protected static final int totalWaitingTime = preTeamsTime + preGameStartingTime + gameStartingTime;
	
	protected static final int minPlayersRequired = 1;
	protected long waitingSince;
	
	protected TeamsPacketsManager teamsPacketsManager;
	
	public TeamArena(InstanceContainer instance, String name)
	{
		super(instance, name);
		
		//Select kit item
		kitMenuItem = ItemStack.builder(Material.FEATHER).amount(1)
				.displayName(Component.text("Select a kit").color(NamedTextColor.BLUE)
						.decoration(TextDecoration.ITALIC, false)).build();
	
		kits = new Kit[]{new KitNone()};
		
		this.teamsPacketsManager = new TeamsPacketsManager(this);
		waitingSince = 0;
	}
	
	
	@Override
	public void tick() {
		super.tick();
		
		/*for(TeamArenaTeam team : teams)
		{
			//instance.sendMessage(Component.text(team.getTeamColour().getName()).color(team.getTeamColour().getTextColor()));
			for(Pos spawn : team.getSpawns()) {
				ParticlePacket packet = ParticleCreator.createParticlePacket(Particle.CRIT,
						spawn.x(), spawn.y() + 0.5, spawn.z(), 0, 0, 0,1);
				
				instance.sendGroupedPacket(packet);
			}
		}*/
		
		while(!leavingQueue.isEmpty()) {
			CustomPlayer player = leavingQueue.remove();
			//sync cleanup tasks..
		}
		
		//process players that just joined
		while(!joiningQueue.isEmpty()) {
			CustomPlayer player = joiningQueue.remove();
			Main.getLogger().info("Processing queue " + player.getUsername());
			giveLobbyItems(player);
			player.refreshCommands();
			teamsPacketsManager.sendCreatePackets(player);
			//if teams decided put them on a team
		}
		
		//tick for each gamestate
		if(gameState.isPreGame())
		{
			//if countdown is ticking, do announcements
			if(instance.getPlayers().size() >= minPlayersRequired) {
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
						//SoundEffectPacket packet = SoundEffectPacket.create(Sound.Source.AMBIENT, SoundEvent.ENTITY_CREEPER_DEATH, p.getPosition(), 99999, 0);
						p.sendMessage(Component.text("Teams have been decided!").color(NamedTextColor.RED));
						Main.getLogger().info("Decided Teams");
						//p.sendPacket(packet);
					}
					
					sendCountdown(true);
				}
			}
			else {
				waitingSince = gameTick;
				gameState = GameState.PREGAME;
				if(teamsDecided) {
					//send remove players for all teams packets
				}
				teamsDecided = false;
			}
		}
	}
	
	public void setupTeams() {
		//shuffle order of teams first so certain teams don't always get the odd player(s)
		TeamArenaTeam[] teamArray = Arrays.copyOf(teams, teams.length);
		MathUtils.shuffleArray(teamArray);
		Player[] players = instance.getPlayers().toArray(new Player[0]);
		if(players.length > 1)
			MathUtils.shuffleArray(players);
		
		int offset = 0;
		for(TeamArenaTeam team : teamArray)
		{
			LinkedList<Player> playersOnThisTeam = new LinkedList<>();
			for(int i = offset; i < players.length; i = i + teamArray.length) {
				playersOnThisTeam.add(players[i]);
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
					String[] coords = loc.split(",");
					double x = Double.parseDouble(coords[0]) + 0.5f;
					double y = Double.parseDouble(coords[1]);
					double z = Double.parseDouble(coords[2]) + 0.5f;
					Pos pos = new Pos(x, y, z);
					positionArray[index] = pos;
					index++;
				}
				teamArenaTeam.setSpawns(positionArray);
				teams[teamsArrIndex] = teamArenaTeam;
				teamsArrIndex++;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendCountdown(boolean force) {
		//time so far in ticks
		long secondsLeft = gameTick - waitingSince;
		
		if(secondsLeft % 20 == 0 || force)
		{
			secondsLeft = ((waitingSince + totalWaitingTime) / 20) - secondsLeft / 20;
			//is a multiple of 30, is 15, is between 10 and 1 inclusive, AND is not 0
			// OR is just forced
			if(((secondsLeft % 30 == 0 || secondsLeft == 15 || secondsLeft == 10 ||
					(secondsLeft <= 5 && secondsLeft >= 1)) && secondsLeft != 0) || force)
			{
				for (Player p : instance.getPlayers())
				{
					String s;
					if(gameState == GameState.PREGAME)
						s = "Teams will be chosen in ";
					else
						s = "Game starting in ";
					SoundEffectPacket packet = SoundEffectPacket.create(Sound.Source.AMBIENT, SoundEvent.ENTITY_CREEPER_DEATH, p.getPosition(), 99999, 0);
					p.sendMessage(Component.text(s + secondsLeft + 's').color(NamedTextColor.RED));
					p.sendPacket(packet);
				}
			}
		}
	}
}
