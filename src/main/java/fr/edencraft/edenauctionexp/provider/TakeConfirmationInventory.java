package fr.edencraft.edenauctionexp.provider;

import fr.edencraft.edenauctionexp.EdenAuctionEXP;
import fr.edencraft.edenauctionexp.auction.Order;
import fr.edencraft.edenauctionexp.provider.utils.Action;
import fr.edencraft.edenauctionexp.provider.utils.ContentsBuilder;
import fr.edencraft.edenauctionexp.utils.ColoredText;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TakeConfirmationInventory implements InventoryProvider {

    private Action action;
    private Order order;
    private SmartInventory previousInventory;

    private SmartInventory smartInventory;

    public TakeConfirmationInventory(Action action, Order order, SmartInventory previousInventory) {
        this.action = action;
        this.order = order;
        this.previousInventory = previousInventory;
        smartInventory = SmartInventory.builder()
                .id("AuctionInventoryTakeConfirmation" + order.getId())
                .provider(this)
                .size(6, 9)
                .manager(EdenAuctionEXP.getINSTANCE().getInventoryManager())
                .title("AuctionEXP - Confirmation")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(4, ClickableItem.empty(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        contents.fillColumn(4, ClickableItem.empty(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        ContentsBuilder contentsBuilder = new ContentsBuilder(contents);
        contentsBuilder.addBackItem(5, 4, previousInventory);

        SlotPos[] confirmationItemPos = {
                new SlotPos(0, 0),
                new SlotPos(0, 1),
                new SlotPos(0, 2),
                new SlotPos(0, 3),
                new SlotPos(1, 0),
                new SlotPos(1, 1),
                new SlotPos(1, 2),
                new SlotPos(1, 3),
                new SlotPos(2, 0),
                new SlotPos(2, 1),
                new SlotPos(2, 2),
                new SlotPos(2, 3),
                new SlotPos(3, 0),
                new SlotPos(3, 1),
                new SlotPos(3, 2),
                new SlotPos(3, 3),
        };

        SlotPos[] cancelItemPos = {
                new SlotPos(0, 5),
                new SlotPos(0, 6),
                new SlotPos(0, 7),
                new SlotPos(0, 8),
                new SlotPos(1, 5),
                new SlotPos(1, 6),
                new SlotPos(1, 7),
                new SlotPos(1, 8),
                new SlotPos(2, 5),
                new SlotPos(2, 6),
                new SlotPos(2, 7),
                new SlotPos(2, 8),
                new SlotPos(3, 5),
                new SlotPos(3, 6),
                new SlotPos(3, 7),
                new SlotPos(3, 8),
        };

        for (int i=0; i < confirmationItemPos.length; i++) {
            SlotPos confirmPos = confirmationItemPos[i];
            SlotPos cancelPos = cancelItemPos[i];

            contentsBuilder.addActionItem(
                    confirmPos.getRow(),
                    confirmPos.getColumn(),
                    Material.GREEN_CONCRETE,
                    new ColoredText("&aConfirmer").treat(),
                    null,
                    action
            );

            contentsBuilder.addInventoryItem(
                    cancelPos.getRow(),
                    cancelPos.getColumn(),
                    previousInventory,
                    Material.RED_CONCRETE,
                    new ColoredText("&cAnnuler").treat(),
                    null
            );
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    public SmartInventory getSmartInventory() {
        return smartInventory;
    }
}
