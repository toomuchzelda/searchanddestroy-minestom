package me.toomuchzelda.sndminestom.game.teamarena;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class MapBorder
{
	//positive XYZ corner
	public Vec posCorner;
	//negative XYZ corner
	public Vec negCorner;
	//for maps with no Y borders
	public boolean hasYLimits;
	
	public MapBorder(double[] corner1, double[] corner2) {
		double posX = Math.max(corner1[0], corner2[0]);
		double posY = Math.max(corner1[1], corner2[1]);
		double posZ = Math.max(corner1[2], corner2[2]);
		
		double negX = Math.min(corner1[0], corner2[0]);
		double negY = Math.min(corner1[1], corner2[1]);
		double negZ = Math.min(corner1[2], corner2[2]);
		
		posCorner = new Vec(posX, posY, posZ);
		negCorner = new Vec(negX, negY, negZ);
		
		if(posY == 0 && negY == 0)
			hasYLimits = false;
	}
	
	//vectors are immutable
	public Vec getCentre() {
		Vec direction = negCorner.sub(posCorner);
		direction = direction.mul(0.5);
		return posCorner.add(direction);
	}
	
	public String toString() {
		return "positive=[" + posCorner.toString() + "], negative=[" + negCorner.toString() + ']';
	}
}
