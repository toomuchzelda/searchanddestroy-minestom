package me.toomuchzelda.sndminestom.game.teamarena;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.game.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecorationAndState;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class TeamArena extends Game
{
	protected final String mapPath = "Maps/TeamArena";
	protected TeamArenaTeam[] teams;
	protected ConcurrentLinkedQueue<CustomPlayer> joiningQueue = new ConcurrentLinkedQueue<>();
	
	protected ItemStack kitMenuItem;
	
	public TeamArena(InstanceContainer instance, String name)
	{
		super(instance, name);
		
		//Select kit item
		kitMenuItem = ItemStack.builder(Material.FEATHER).amount(1)
				.displayName(Component.text("Select a kit").color(NamedTextColor.BLUE)
						.decoration(TextDecoration.ITALIC, false)).build();
	}
	
	@Override
	public void tick() {
		super.tick();
		for(TeamArenaTeam team : teams)
		{
			//instance.sendMessage(Component.text(team.getTeamColour().getName()).color(team.getTeamColour().getTextColor()));
			for(Pos spawn : team.getSpawns()) {
				ParticlePacket packet = ParticleCreator.createParticlePacket(Particle.CRIT,
						spawn.x(), spawn.y() + 0.5, spawn.z(), 0, 0, 0,1);
				
				instance.sendGroupedPacket(packet);
			}
		}
		
		//process players that just joined
		while(!joiningQueue.isEmpty()) {
			CustomPlayer player = (CustomPlayer) joiningQueue.remove();
			Main.getLogger().info("Processing queue " + player.getUsername());
			giveLobbyItems(player);
		}
	}
	
	@Override
	public String mapPath() {
		return "Maps/TeamArena";
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
				TeamArenaTeam teamArenaTeam = new TeamArenaTeam(teamColour);
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
	
	public void queueJoiningPlayer(CustomPlayer player) {
		joiningQueue.add(player);
	}
	
	public void giveLobbyItems(CustomPlayer player) {
		PlayerInventory inventory = player.getInventory();
		inventory.setItemStack(0, kitMenuItem);
	}
}
