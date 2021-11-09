package me.toomuchzelda.sndminestom.game.teamarena.commands;

import me.toomuchzelda.sndminestom.Main;
import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.core.ranks.Rank;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.teamarena.TeamArena;
import me.toomuchzelda.sndminestom.game.teamarena.kits.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import org.jetbrains.annotations.NotNull;

public class CommandKit extends Command
{
	private static Rank rankRequired = Rank.PLAYER;
	
	public CommandKit()
	{
		super("kit");
		
		setCondition(((sender, commandString) -> {
			boolean canUse = false;
			if(sender.isPlayer()) {
				CustomPlayer player = (CustomPlayer) sender.asPlayer();
				Game game = player.getGame();
				if(game instanceof TeamArena teamArena) {
					if(teamArena.canSelectKitNow())
						canUse = true;
				}
			}
			
			if(commandString != null && !canUse)
				sendNotAllowedMessage(sender);
			
			return canUse;
		}));
		
		setDefaultExecutor((sender, context) -> {
			sender.sendMessage(Component.text("Usage: /kit <kit name>").color(NamedTextColor.RED));
			Component kitList = Component.text("Available kits: ").color(NamedTextColor.BLUE);
			//if they passed the setCondition() then all the stuff here should be valid and not need checking
			// theoretically
			Game game = ((CustomPlayer) sender.asPlayer()).getGame();
			Kit[] kits = ((TeamArena) game).getKits();
			for(int i = 0; i < kits.length; i++) {
				//Main.getLogger().info(kits[i].getName());
				kitList = kitList.append(Component.text(kits[i].getName() + ", ").color(NamedTextColor.BLUE));
			}
			sender.sendMessage(kitList);
		});
		
		ArgumentWord nameArg = ArgumentType.Word("kit-name");
		
		addSyntax((sender, context) -> {
			final String kit = context.get(nameArg);
			CustomPlayer player = (CustomPlayer) sender.asPlayer();
			TeamArena teamArena = (TeamArena) player.getGame();
			teamArena.selectKit(player, kit);
		}, nameArg);
		
	}
	
	public void sendNotAllowedMessage(CommandSender sender) {
		if(sender.isConsole())
			sender.sendMessage(Component.text("Console can't choose a kit smh").color(NamedTextColor.RED));
		else if(sender.isPlayer())
			sender.sendMessage(Component.text("You can't choose a kit right now").color(NamedTextColor.RED));
	}
}
