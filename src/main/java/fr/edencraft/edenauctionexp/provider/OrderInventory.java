package fr.edencraft.edenauctionexp.provider;

import fr.edencraft.edenauctionexp.EdenAuctionEXP;
import fr.edencraft.edenauctionexp.auction.Order;
import fr.edencraft.edenauctionexp.command.EdenAuctionEXPCommand;
import fr.edencraft.edenauctionexp.provider.utils.Action;
import fr.edencraft.edenauctionexp.provider.utils.ContentsBuilder;
import fr.edencraft.edenauctionexp.utils.ColoredText;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import fr.minuskube.inv.content.SlotPos;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class OrderInventory implements InventoryProvider {

    private Order order;
    private int previousPage;
    private SmartInventory smartInventory;

    public OrderInventory(Order order, int previousPage) {
        this.order = order;
        this.previousPage = previousPage;
        smartInventory = SmartInventory.builder()
                .id("AuctionInventoryOrder" + order.getId())
                .provider(this)
                .size(6, 9)
                .manager(EdenAuctionEXP.getINSTANCE().getInventoryManager())
                .title("AuctionEXP - Ordre #" + order.getId())
                .build();
    }

    public SmartInventory getSmartInventory() {
        return smartInventory;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(4, ClickableItem.empty(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        ContentsBuilder contentsBuilder = new ContentsBuilder(contents);
        contentsBuilder.addBackItem(5, 4, AuctionInventory.INVENTORY, previousPage);
        contentsBuilder.addActionItem(
                0,
                4,
                Material.EXPERIENCE_BOTTLE,
                new ColoredText("&6Détails de l'offre:").treat(),
                Arrays.asList(
                        "",
                        new ColoredText("  &f• &eType d'ordre: &b" + order.getOrderType().name()).treat(),
                        new ColoredText("  &f• &eQuantité d'expérience: &b" + order.getAmount()).treat(),
                        new ColoredText("  &f• &ePrix de l'ordre: &b" + order.getPrice()).treat(),
                        new ColoredText("  &f• &eProposé par: &b" + Bukkit.getOfflinePlayer(order.getPlayerUUID()).getName()).treat()
                ),
                null
        );

        Action action = event -> {
            if (event.isLeftClick()) {
                if (EdenAuctionEXPCommand.onTakeOrder(player, order.getId())) event.getWhoClicked().closeInventory();
            }
        };

        contentsBuilder.addInventoryItem(
                2,
                4,
                new TakeConfirmationInventory(action, order, smartInventory).getSmartInventory(),
                Material.GREEN_CONCRETE,
                new ColoredText("&aPrendre l'offre").treat(),
                null
        );
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
