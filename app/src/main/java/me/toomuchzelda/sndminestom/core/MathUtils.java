package me.toomuchzelda.sndminestom.core;

import java.util.Random;

public class MathUtils
{
	public static final Random random = new Random();
	
	public static double randomRange(double min, double max) {
		double rand = random.nextDouble() * (max - min);
		rand += min;
		return rand;
	}
	
	public static int randomMax(int max) {
		// + 1 to not exclude the max value itself
		return random.nextInt(max + 1);
	}
	
	public static int randomRange(int min, int max) {
		return randomMax(max) + min;
	}
	
	public static void shuffleArray(Object[] array) {
		for(int i = 0; i < array.length; i++) {
			int rand = MathUtils.randomMax(array.length - 1);
			Object temp = array[rand];
			array[rand] = array[i];
			array[i] = temp;
		}
	}
}
