package me.toomuchzelda.sndminestom.game.teamarena;

import com.extollit.collect.ArrayIterable;
import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.game.TeamGame;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public abstract class TeamArena extends TeamGame
{
	protected final String mapPath = "Maps/TeamArena";
	protected TeamArenaTeam[] teams;
	
	public TeamArena(InstanceContainer instance, String name)
	{
		super(instance, name);
	}
	
	@Override
	public void tick() {
		super.tick();
		for(TeamArenaTeam team : teams)
		{
			for(Pos spawn : team.getSpawns()) {
				ParticlePacket packet = ParticleCreator.createParticlePacket(Particle.CRIT,
						spawn.x(), spawn.y(), spawn.z(), 0, 0, 0,1);
				
				instance.sendGroupedPacket(packet);
			}
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
}
