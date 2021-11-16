package me.toomuchzelda.sndminestom.core;

import me.toomuchzelda.sndminestom.Main;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//minestom already has a BlockUtils bruh
public class BlockStuff
{
	
	
	//parse coords from map config.yml
	// String format: x,y,z
	// example: 34,0,40.5
	public static double[] parseCoords(String string, double xOffset, double yOffset, double zOffset) {
		String[] split = string.split(",");
		double[] coords = new double[3];
		coords[0] = Double.parseDouble(split[0]) + xOffset;
		coords[1] = Double.parseDouble(split[1]) + yOffset;
		coords[2] = Double.parseDouble(split[2]) + zOffset;
		return coords;
	}
	
	//find the first non-air block below any coordinate
	// returns null if none
	public static Vec getFloor(Point pos, InstanceContainer instance) {
		int y = (int) pos.y();
		for(int yl = y; y >= -1; y--) {
			//did not find a block if reached here
			if(y == -1) {
				return null;
			}
			else if(instance.getBlock((int) pos.x(), yl, (int) pos.z()).isSolid()) {
				y = yl;
				break;
			}
		}
		return new Vec(pos.x(), y, pos.z());
	}
	
	//get highest coord of solid block at xz point
	//fuck async bruh
	public static Vec getHighestBlock (Point pos, InstanceContainer instance) {
		
		Vec loc = new Vec(pos.x(), 256, pos.z());
		
		CompletableFuture<Chunk> future = instance.loadOptionalChunk(pos);
		try {
			//wait for the thing to load?
			Chunk chunk = future.get();
			Main.getLogger().info(chunk.getIdentifier().toString());
			while(!chunk.getBlock(loc).isSolid()) {
				//didnt find block
				if((int) loc.y() == 0) {
					loc = loc.withY(256);
					break;
				}
				loc = loc.sub(0, 1, 0);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return loc;
	}
}
