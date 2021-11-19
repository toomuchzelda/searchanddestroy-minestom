package me.toomuchzelda.sndminestom.game.teamarena.commands;

import me.toomuchzelda.sndminestom.core.CustomPlayer;
import me.toomuchzelda.sndminestom.game.Game;
import me.toomuchzelda.sndminestom.game.teamarena.TeamArena;
import me.toomuchzelda.sndminestom.game.teamarena.TeamArenaTeam;
import me.toomuchzelda.sndminestom.game.teamarena.kits.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;

public class CommandTeam extends Command
{
	public CommandTeam() {
		super("team");
		
		setCondition((sender, commandString) -> {
			boolean canUse = false;
			if(sender.isPlayer()) {
				CustomPlayer player = (CustomPlayer)  sender.asPlayer();
				if(player.getGame() instanceof TeamArena tm) {
					if(tm.canSelectTeamNow()) {
						canUse = true;
					}
				}
			}
			
			if(commandString != null && !canUse)
				sendNotAllowedMessage(sender);
			
			return canUse;
		});
		
		setDefaultExecutor((sender, context) -> {
			sender.sendMessage(Component.text("Usage: /team <team name>").color(NamedTextColor.RED));
			Component teamsList = Component.text("Available teams: ").color(NamedTextColor.BLUE);
			//if they passed the setCondition() then all the stuff here should be valid and not need checking
			// theoretically
			Game game = ((CustomPlayer) sender.asPlayer()).getGame();
			TeamArenaTeam[] teams = ((TeamArena) game).getTeams();
			for(TeamArenaTeam team : teams) {
				teamsList = teamsList.append(Component.text(team.getTeamColour().getSimpleName())
						.color(team.getTeamColour().getRGBTextColor()).append(Component.text(", ")));
			}
			sender.sendMessage(teamsList);
		});
		
		ArgumentWord nameArg = ArgumentType.Word("team");
		
		addSyntax((sender, context) -> {
			final String team = context.get(nameArg);
			CustomPlayer player = (CustomPlayer) sender.asPlayer();
			TeamArena teamArena = (TeamArena) player.getGame();
			teamArena.selectTeam(player, team);
		}, nameArg);
	}
	
	public void sendNotAllowedMessage(CommandSender sender) {
		if(sender.isConsole())
			sender.sendMessage("lol");
		else {
			//assuming TeamArena is the only Game they could be in, since right now it's the only game
			sender.sendMessage(Component.text("It's too late to pick a team!").color(NamedTextColor.RED));
		}
	}
}
