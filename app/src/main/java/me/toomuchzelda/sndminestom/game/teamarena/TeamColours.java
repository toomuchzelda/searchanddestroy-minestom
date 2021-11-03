package me.toomuchzelda.sndminestom.game.teamarena;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.color.Color;
import net.minestom.server.color.DyeColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

//uhhhh might need to be renamed TeamSettings and GameTeam become lib's thing
public enum TeamColours
{
	NONE("Players", "Players", convert(NamedTextColor.WHITE), convert(NamedTextColor.WHITE),
			ItemStack.of(Material.PLAYER_HEAD), DyeColor.WHITE),
	
	BLUE("Blue Team", "Blue", convert(NamedTextColor.BLUE), convert(NamedTextColor.DARK_BLUE),
		ItemStack.of(Material.DIAMOND_BLOCK), DyeColor.BLUE),
	
	GREEN("Green Team", "Green", convert(NamedTextColor.GREEN), convert(NamedTextColor.DARK_GREEN),
			ItemStack.of(Material.BIRCH_LEAVES), DyeColor.GREEN),
	
	PURPLE("Purple Team", "Purple", convert(NamedTextColor.DARK_PURPLE), convert(NamedTextColor.DARK_PURPLE),
			ItemStack.of(Material.OBSIDIAN), DyeColor.PURPLE),
	
	RED("Red Team", "Red", convert(NamedTextColor.RED), convert(NamedTextColor.DARK_RED), ItemStack.of(Material.REDSTONE_BLOCK),
			DyeColor.RED),
	
	YELLOW("Yellow Team", "Yellow", convert(NamedTextColor.YELLOW), convert(NamedTextColor.GOLD),
			ItemStack.of(Material.GOLD_BLOCK), DyeColor.YELLOW);
	
	//more cool colours....
	
	;

	private final String name;
	private final String simpleName;
	private final Color colour;
	private final Color darkColour;
	private final ItemStack hat;
	private final DyeColor dyeColour;
	
	private TeamColours(String name, String simpleName, Color colour, Color darkColour, ItemStack hat, DyeColor dyeColor) {
		this.name = name;
		this.simpleName = simpleName;
		this.colour = colour;
		this.darkColour = darkColour;
		this.hat = hat;
		this.dyeColour = dyeColor;
	}
	
	private static Color convert(NamedTextColor textColor) {
		return new Color(textColor.red(), textColor.green(), textColor.blue());
	}
	
	public String getName() {
		return name;
	}
	
	public String getSimpleName() {
		return simpleName;
	}
	
	public Color getColour() {
		return colour;
	}
	
	public Color getDarkColour() {
		return darkColour;
	}
	
	public ItemStack getHat() {
		return hat;
	}
	
	public DyeColor getDyeColour() {
		return dyeColour;
	}
}
