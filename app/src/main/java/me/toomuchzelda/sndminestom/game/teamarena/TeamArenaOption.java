package me.toomuchzelda.sndminestom.game.teamarena;

public class TeamArenaOption<V>
{
	public final String name;
	public final String desc;
	public V value;
	public final Object defaultValue;
	
	public TeamArenaOption(String name, String desc, V value, Object defaultValue) {
		this.name = name;
		this.desc = desc;
		this.value = value;
		this.defaultValue = defaultValue;
	}
}
