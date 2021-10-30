package me.toomuchzelda.sndminestom.core;

import java.util.Random;

public class MathUtils
{
	public static Random random = new Random();
	
	public static double randomRange(double min, double max) {
		double rand = random.nextDouble() * (max - min);
		rand += min;
		return rand;
	}
	
	public static int randomMax(int max) {
		int rand = random.nextInt();
		return rand % max;
	}
	
	public static int randomRange(int min, int max) {
		return randomMax(max) + min;
	}
}
