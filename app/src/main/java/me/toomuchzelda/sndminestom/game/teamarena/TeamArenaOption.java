package me.toomuchzelda.sndminestom.game.teamarena;

public class TeamArenaOption
{
	public final String name;
	public final String desc;
	public Object value;
	public final Object defaultValue;
	
	public TeamArenaOption(String name, String desc, Object value, Object defaultValue) {
		this.name = name;
		this.desc = desc;
		this.value = value;
		this.defaultValue = defaultValue;
	}
}
