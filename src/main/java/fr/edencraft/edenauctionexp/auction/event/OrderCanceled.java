package fr.edencraft.edenauctionexp.auction.event;

import fr.edencraft.edenauctionexp.auction.Auction;
import fr.edencraft.edenauctionexp.auction.Order;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class OrderCanceled extends Event implements Cancellable {

    private static HandlerList HANDLERS = new HandlerList();
    private boolean cancel = false;

    private Auction auction;
    private Order order;

    public OrderCanceled(boolean cancel, Auction auction, Order order) {
        this.cancel = cancel;
        this.auction = auction;
        this.order = order;
    }

    public static HandlerList getHANDLERS() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
