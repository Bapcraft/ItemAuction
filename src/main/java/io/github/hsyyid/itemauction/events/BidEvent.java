package io.github.hsyyid.itemauction.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import io.github.hsyyid.itemauction.util.Auction;

public class BidEvent extends AbstractEvent implements Cancellable
{
	private boolean cancelled = false;

	private Player bidder;
	private double price;
	private Auction auction;
	private Cause cause;

	public Player getBidder()
	{
		return bidder;
	}

	public double getPrice()
	{
		return price;
	}

	public Auction getAuction()
	{
		return auction;
	}

	public boolean isCancelled()
	{
		return cancelled;
	}

	public void setCancelled(boolean cancel)
	{
		cancelled = cancel;
	}

	public BidEvent(Player bidder, double price, Auction auction, Cause parentCause)
	{
		this.bidder = bidder;
		this.price = price;
		this.auction = auction;
		this.cause = Cause.builder().from(parentCause).build(parentCause.getContext());
	}

	public Cause getCause()
	{
		return this.cause;
	}
}
