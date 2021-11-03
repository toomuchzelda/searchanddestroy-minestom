package me.toomuchzelda.sndminestom.core.ranks;

public enum Rank
{
	OPERATOR, ADMIN, PLAYER;
	
	public boolean isOp() {
		return this == ADMIN || this == OPERATOR;
	}
}
