package io.github.hsyyid.itemauction.cmdexecutors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ItemAuctionExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		String version = Sponge.getPluginManager().getPlugin("itemauction").get().getVersion().get();
		src.sendMessage(Text.of(TextColors.GREEN, "ItemAuction: ", TextColors.GRAY, "Version: ", TextColors.GOLD, version));
		return CommandResult.success();
	}
}
