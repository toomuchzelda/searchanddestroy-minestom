package me.toomuchzelda.sndminestom.core.commands;

import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.core.ranks.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;

public class CommandStop extends Command
{
	private static Rank rankRequired = Rank.ADMIN;
	
	public CommandStop()
	{
		super("stop");
		
		setCondition((sender, commandString) -> {
			boolean canUse = false;
			if(sender.isConsole())
				canUse = true;
			else if(sender.isPlayer()) {
				CustomPlayer player = (CustomPlayer) sender.asPlayer();
				if(player.getRank().isOp())
					canUse = true;
			}
			
			if(commandString != null && !canUse)
				sendNotAllowedMessage(sender);
			
			return canUse;
		});
		
		setDefaultExecutor(((sender, context) -> {
				MinecraftServer.stopCleanly();
				System.exit(0);
			}
		));
	}
	
	private static void sendNotAllowedMessage(CommandSender sender) {
		sender.sendMessage(Component.text("Stop? ").color(TextColor.color(255, 0, 0)).append(
				Component.text("Why ").color(TextColor.color(255, 255, 255)).append(
						Component.text("stop? ").color(TextColor.color(255, 0, 0)).append(
								Component.text("GO! ").color(TextColor.color(0, 255, 0)).append(
										Component.text("I say ").color(TextColor.color(255, 255, 255)).append(
												Component.text("GO!!").color(TextColor.color(0, 255, 0))
										)
								)
						))
		));
	}
}
