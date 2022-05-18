package fr.edencraft.edenauctionexp.provider;

import fr.edencraft.edenauctionexp.EdenAuctionEXP;
import fr.edencraft.edenauctionexp.auction.Auction;
import fr.edencraft.edenauctionexp.auction.Order;
import fr.edencraft.edenauctionexp.auction.OrderType;
import fr.edencraft.edenauctionexp.provider.utils.ContentsBuilder;
import fr.edencraft.edenauctionexp.utils.ColoredText;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AuctionInventory implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("AuctionInventory")
            .provider(new AuctionInventory())
            .size(6, 9)
            .manager(EdenAuctionEXP.getINSTANCE().getInventoryManager())
            .title("AuctionEXP")
            .build();

    private Auction auction = EdenAuctionEXP.getINSTANCE().getAuction();
    private Map<String, Object> configVars = null;

    @Override
    public void init(Player player, InventoryContents contents) {
        loadConfigVars();
        update(player, contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);

        if (state % 5 != 0) return;

        Pagination pagination = contents.pagination();
        ContentsBuilder contentsBuilder = new ContentsBuilder(contents);

        ClickableItem[] items = new ClickableItem[auction.getOrders().size()];

        for (int i=0; i < items.length; i++) {
            Order order = auction.getOrders().get(i);
            items[i] = ClickableItem.of(
                    buildItemOrder(order),
                    inventoryClickEvent -> orderItemEvent(inventoryClickEvent, contents, order)
            );
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(36);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        contents.fillRow(4, ClickableItem.empty(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        contentsBuilder.addCloseItem(5, 4);

        if (!contents.pagination().isFirst()) {
            contents.set(5, 3, ClickableItem.of(buildPaginationItem("items.previous-page", contents),
                    e -> INVENTORY.open(player, pagination.previous().getPage())));
        }
        if (!contents.pagination().isLast()) {
            contents.set(5, 5, ClickableItem.of(buildPaginationItem("items.next-page", contents),
                    e -> INVENTORY.open(player, pagination.next().getPage())));
        }

    }

    private void orderItemEvent(InventoryClickEvent inventoryClickEvent, InventoryContents contents, Order order) {
        if (inventoryClickEvent.isLeftClick()) {
            OrderInventory orderInventory = new OrderInventory(order, contents.pagination().getPage());
            orderInventory.getSmartInventory().open((Player) inventoryClickEvent.getWhoClicked());
        }
    }

    private void loadConfigVars() {
        FileConfiguration cfg = EdenAuctionEXP.getINSTANCE().getConfigurationManager().getConfigurationFile("config.yml");
        ConfigurationSection items = cfg.getConfigurationSection("items");

        Map<String, Object> configVars = new HashMap<>();

        for (String itemName : items.getKeys(false)) {
            ConfigurationSection item = items.getConfigurationSection(itemName);
            if (item.getString("displayname") != null) {
                configVars.put(item.getCurrentPath() + ".displayname", item.getString("displayname"));
            }
            if (item.isString("lore") && item.getString("lore") != null) {
                if (!item.getString("lore").equalsIgnoreCase("none")) {
                    configVars.put(item.getCurrentPath() + ".lore", Arrays.asList(item.getString("lore")));
                }
                continue;
            } else {
                List<String> lore = item.getStringList("lore");
                configVars.put(item.getCurrentPath() + ".lore", lore);
            }
        }
        this.configVars = configVars;
    }

    private String formatItem(String s, Order order) {
        s = s.replaceAll("\\{order_id}", order.getId() + "");
        s = s.replaceAll("\\{player_name}", Bukkit.getOfflinePlayer(order.getPlayerUUID()).getName());
        s = s.replaceAll("\\{exp}", order.getAmount() + "");
        s = s.replaceAll("\\{price}", order.getPrice() + "");

        return new ColoredText(s).treat();
    }

    private String formatPaginationItem(String s, InventoryContents contents) {
        s = s.replaceAll("\\{next_page_number}", (contents.pagination().getPage()+2) + "");
        s = s.replaceAll(
                "\\{previous_page_number}",
                (contents.pagination().getPage() == 0 ? "❌" : contents.pagination().getPage()) + ""
        );

        return  new ColoredText(s).treat();
    }

    private ItemStack buildItemOrder(Order order) {
        ItemStack itemOrder = null;
        if (order.getOrderType().equals(OrderType.BUYING))  {
            itemOrder = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemOrder.getItemMeta();

            String displayName = (String) configVars.get("items.buy-order.displayname");
            itemMeta.displayName(Component.text(formatItem(displayName, order)));

            List<String> lore = (List<String>) configVars.get("items.buy-order.lore");
            List<Component> loreCompo = new ArrayList<>();
            for (String s : lore) loreCompo.add(Component.text(formatItem(s, order)));

            itemMeta.lore(loreCompo);
            itemOrder.setItemMeta(itemMeta);
        } else {
            itemOrder = new ItemStack(Material.CHEST);
            ItemMeta itemMeta = itemOrder.getItemMeta();

            String displayName = (String) configVars.get("items.sell-order.displayname");
            itemMeta.displayName(Component.text(formatItem(displayName, order)));

            List<String> lore = (List<String>) configVars.get("items.sell-order.lore");
            List<Component> loreCompo = new ArrayList<>();
            for (String s : lore) loreCompo.add(Component.text(formatItem(s, order)));

            itemMeta.lore(loreCompo);
            itemOrder.setItemMeta(itemMeta);
        }

        return itemOrder;
    }

    private ItemStack buildPaginationItem(String path, InventoryContents contents) {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String displayName = (String) configVars.get(path + ".displayname");
        itemMeta.displayName(Component.text(formatPaginationItem(displayName, contents)));

        List<String> lore = (List<String>) configVars.get(path + ".lore");
        if (lore != null) {
            List<Component> loreCompo = new ArrayList<>();
            for (String s : lore) loreCompo.add(Component.text(formatPaginationItem(s, contents)));

            itemMeta.lore(loreCompo);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
