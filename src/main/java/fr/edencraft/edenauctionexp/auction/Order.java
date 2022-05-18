package fr.edencraft.edenauctionexp.auction;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Order {

    private int id;
    private UUID playerUUID;
    private double amount;
    private double price;
    private OrderType orderType;
    private long creationDate;

    public Order(int id, UUID player, double amount, double price, OrderType orderType, long creationDate) {
        this.id = id;
        this.playerUUID = player;
        this.amount = amount;
        this.price = price;
        this.orderType = orderType;
        this.creationDate = creationDate;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public int getId() {
        return id;
    }

    public OrderType getOrderType() {
        return orderType;
    }
}
