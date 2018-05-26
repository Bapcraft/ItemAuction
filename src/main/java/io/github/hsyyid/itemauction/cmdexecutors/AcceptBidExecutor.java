package io.github.hsyyid.itemauction.cmdexecutors;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColors;

import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.util.Auction;
import io.github.hsyyid.itemauction.util.Bid;

public class AcceptBidExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;

			Optional<Auction> auction = ItemAuction.auctions.stream().filter(a -> a.getSender().getUniqueId() == player.getUniqueId()).findAny();

			if (auction.isPresent())
			{
				double highestBid = auction.get().getBids().stream().mapToDouble(i -> i.getPrice().doubleValue()).max().orElse(-15);
				Optional<Bid> bid = auction.get().getBids().stream().filter(b -> b.getPrice().doubleValue() == highestBid).findAny();

				if (bid.isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getQuantity() == auction.get().getItemStack().getQuantity() && player.getItemInHand(HandTypes.MAIN_HAND).get().getType() == auction.get().getItemStack().getType())
				{
					Player bidder = bid.get().getBidder();
					Currency currency = ItemAuction.economyService.getDefaultCurrency();
					EconomyService eco = ItemAuction.economyService;
					Cause cause = Cause.builder().append(player).build(EventContext.empty()); // FIXME Make this have more context in it.
					TransactionResult transactionResult = eco.getOrCreateAccount(bidder.getUniqueId()).get().transfer(eco.getOrCreateAccount(player.getUniqueId()).get(), currency, bid.get().getPrice(), cause);

					if (transactionResult.getResult() == ResultType.SUCCESS)
					{
						ItemAuction.auctions.remove(auction.get());
						MutableMessageChannel messageChannel = MessageChannel.TO_ALL.asMutable();

						for (UUID uuid : ItemAuction.ignorePlayers)
						{
							if (Sponge.getServer().getPlayer(uuid).isPresent())
							{
								messageChannel.removeMember(Sponge.getServer().getPlayer(uuid).get());
							}
						}

						messageChannel.send(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, player.getName() + " auction for " + auction.get().getQuantity() + " " + auction.get().getItemStack().getType().getTranslation().get() + " has ended."));
						bidder.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, "Your bid was accepted by " + player.getName() + "."));
						player.setItemInHand(HandTypes.MAIN_HAND, null);
						bidder.getInventory().offer(auction.get().getItemStack());
						src.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, "Bid accepted."));
					}
					else
					{
						src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Bidder does not have enough money!"));
					}
				}
				else if (bid.isPresent())
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are not holding the item(s) for the auction!"));
				}
				else
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Bid not found!"));
				}
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are not auctioning anything!"));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /acceptbid!"));
		}

		return CommandResult.success();
	}
}
