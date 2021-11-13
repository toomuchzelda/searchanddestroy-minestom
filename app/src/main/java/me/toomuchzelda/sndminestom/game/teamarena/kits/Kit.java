package me.toomuchzelda.sndminestom.game.teamarena.kits;

import me.toomuchzelda.sndminestom.game.teamarena.abilities.Ability;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;

public abstract class Kit
{
	private String name;
	private String description;
	private Material icon;
	private ItemStack[] armour;
	private ItemStack[] items;
	private Ability[] abilities;
	
	public Kit(String name, String description, Material icon) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		
		//these are set via the setter methods
		ItemStack[] armour = new ItemStack[4];
		Arrays.fill(armour, ItemStack.AIR);
		this.armour = armour;
		
		this.items = new ItemStack[0];
		this.abilities = new Ability[0];
	}
	
	public void setArmour(ItemStack[] armour) {
		this.armour = armour;
	}
	
	public void setItems(ItemStack[] items) {
		this.items = items;
	}
	
	public void setAbilities(Ability... abilities) {
		this.abilities = abilities;
	}
	
	public Material getIcon() {
		return icon;
	}
	
	public String getName() {
		return name;
	}
}
