package fr.edencraft.edenauctionexp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.edencraft.edenauctionexp.EdenAuctionEXP;
import fr.edencraft.edenauctionexp.auction.Auction;
import fr.edencraft.edenauctionexp.auction.Order;
import fr.edencraft.edenauctionexp.auction.OrderType;
import fr.edencraft.edenauctionexp.provider.AuctionInventory;
import fr.edencraft.edenauctionexp.utils.PlayerUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("edenauctionexp|eaexp|ahexp")
public class EdenAuctionEXPCommand extends BaseCommand {

    private static final String basePermission = "edenauctionexp.command";

    private static final Auction auction = EdenAuctionEXP.getINSTANCE().getAuction();
    private static final Economy economy = EdenAuctionEXP.getINSTANCE().getEconomy();

    @Default
    @CommandPermission(basePermission)
    public static void onCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            AuctionInventory.INVENTORY.open(player);
            return;
        }
        sender.sendMessage("HELP MESSAGE HERE");
    }

    @Subcommand("sell")
    @CommandPermission(basePermission + ".sell")
    @CommandCompletion("<montant> <prix>")
    public static void onSell(Player seller, double amount, double price) {
        if (PlayerUtils.getPlayerExp(seller) < amount) {
            seller.sendMessage("Vous n'avez pas assez d'exp (" + PlayerUtils.getPlayerExp(seller) + ").");
            return;
        }

        if (auction.getOrderCount(seller.getUniqueId()) >= Auction.getMaxOrder()) {
            seller.sendMessage("Vous avez placé trop d'offre.");
            return;
        }
        if (amount > Auction.getMaxExperience()) {
            seller.sendMessage("Vous ne pouvez pas faire une offre avec autant d'experience.");
            return;
        }
        if (amount < Auction.getMinExperience()) {
            seller.sendMessage("Vous ne pouvez pas faire une offre avec aussi peu d'experience.");
            return;
        }

        double priceForOneExp = calculatePriceForOneExp(amount, price);
        if (priceForOneExp > Auction.getMaxPrice()) {
            seller.sendMessage("Votre prix est trop haut pour cette quantitée d'expérience.");
            return;
        }
        if (priceForOneExp < Auction.getMinPrice()) {
            seller.sendMessage("Votre prix est trop bas pour cette quantitée d'experience.");
            return;
        }

        boolean state = auction.addOrder(seller.getUniqueId(), amount, price, OrderType.SELLING);
        if (!state) {
            seller.sendMessage("Impossible de placer votre offre, contactez un administrateur.");
            return;
        }

        seller.sendMessage("Suppression de " + (int) (amount) + "/" + PlayerUtils.getPlayerExp(seller) + " exp.");
        PlayerUtils.changePlayerExp(seller.getPlayer(), (int) (amount * -1));
    }

    @Subcommand("remove")
    @CommandPermission(basePermission + ".remove")
    @CommandCompletion("<id_de_l'ordre>")
    public static void onRemove(Player player, int orderId) {
        List<Order> playerOrders = auction.getPlayerOrders(player);
        if (playerOrders.isEmpty()) {
            player.sendMessage("Vous n'avez pas d'offre à supprimer.");
            return;
        }
        if (playerOrders.stream().noneMatch(order -> order.getId() == orderId)) {
            player.sendMessage("L'id " + orderId + " ne fait pas partie de votre liste d'ordre.");
            return;
        }

        Order order = playerOrders.stream().filter(order1 -> order1.getId() == orderId).findFirst().orElse(null);
        if (order == null) {
            player.sendMessage("Impossible de supprimer votre offre, contactez un adminsitrateur.");
            return;
        }

        if (order.getOrderType().equals(OrderType.SELLING))
            PlayerUtils.changePlayerExp(player, (int) order.getAmount());
        else
            economy.depositPlayer(player, order.getPrice());
        boolean state = auction.removeOrder(orderId);
        if (!state) {
            player.sendMessage("Impossible de supprimer l'offre, contactez un administrateur.");
            return;
        }
        player.sendMessage("Offre supprimé.");
    }

    @Subcommand("buy")
    @CommandPermission(basePermission + ".buy")
    @CommandCompletion("<montant> <prix>")
    public static void onBuy(Player player, double amount, double price) {
        if (economy.getBalance(player) < price) {
            player.sendMessage("Vous n'avez pas assez d'argent pour placer cette ordre.");
            return;
        }

        if (auction.getOrderCount(player.getUniqueId()) >= Auction.getMaxOrder()) {
            player.sendMessage("Vous avez placé trop d'offre.");
            return;
        }
        if (amount > Auction.getMaxExperience()) {
            player.sendMessage("Vous ne pouvez pas faire une offre avec autant d'experience.");
            return;
        }
        if (amount < Auction.getMinExperience()) {
            player.sendMessage("Vous ne pouvez pas faire une offre avec aussi peu d'experience.");
            return;
        }

        double priceForOneExp = calculatePriceForOneExp(amount, price);
        if (priceForOneExp > Auction.getMaxPrice()) {
            player.sendMessage("Votre prix est trop haut pour cette quantitée d'expérience.");
            return;
        }
        if (priceForOneExp < Auction.getMinPrice()) {
            player.sendMessage("Votre prix est trop bas pour cette quantitée d'experience.");
            return;
        }

        boolean state = auction.addOrder(player.getUniqueId(), amount, price, OrderType.BUYING);
        if (!state) {
            player.sendMessage("Impossible de placer votre offre, contactez un administrateur.");
            return;
        }

        player.sendMessage("Mise en réserve de " + price + " " + economy.currencyNamePlural());
        economy.withdrawPlayer(player, price);
    }

    @Subcommand("take-order")
    @CommandPermission(basePermission + ".take-order")
    @CommandCompletion("<id_de_l'ordre>")
    public static boolean onTakeOrder(Player player, int orderId) {
        if (auction.idIsFree(orderId)) {
            player.sendMessage("L'id " + orderId + " n'existe pas.");
            return false;
        }
        if (auction.getPlayerOrders(player).stream().anyMatch(order -> order.getId() == orderId)) {
            player.sendMessage("Vous ne pouvez pas prendre votre propre ordre.");
            return false;
        }

        Order order = auction.getOrders().stream().filter(order1 -> order1.getId() == orderId).findFirst().orElse(null);
        if (order == null) {
            player.sendMessage("Impossible de prendre cette offre, contactez un administrateur. (code: 1)");
            return false;
        }
        if (order.getOrderType().equals(OrderType.BUYING)) {
            if (PlayerUtils.getPlayerExp(player) < order.getAmount()) {
                player.sendMessage("Vous n'avez pas assez d'experience pour satisfaire cette offre.");
                return false;
            }

            PlayerUtils.changePlayerExp(player, (int) (order.getAmount() * -1));
            economy.depositPlayer(player, order.getPrice());
        } else {
            if (economy.getBalance(player) < order.getPrice()) {
                player.sendMessage("Vous n'avez pas assez d'argent pour cette offre.");
                return false;
            }

            economy.withdrawPlayer(player, order.getPrice());
            PlayerUtils.changePlayerExp(player, (int) order.getAmount());
        }

        boolean state = auction.successOrder(orderId, player);
        if (!state) {
            player.sendMessage("Impossible de prendre cette offre, contactez un administrateur. (code: 2)");
            return false;
        }

        if (order.getOrderType().equals(OrderType.BUYING)) {
            player.sendMessage("Vous avez achetez " + order.getAmount() + " experiences pour " + order.getPrice() + " " +
                    economy.currencyNamePlural());
        } else {
            player.sendMessage("Vous avez vendu " + order.getAmount() + " experience pour " + order.getPrice() + " " +
                    economy.currencyNamePlural());
        }

        return true;
    }

    /**
     * @param amount of exp in the Order.
     * @param price  of the Order.
     * @return Price of one exp (see example below).
     * Example:
     * Order{amount=100, price=10}
     * Result will be (0.1) for one exp.
     */
    private static double calculatePriceForOneExp(double amount, double price) {
        return price / amount;
    }

}
