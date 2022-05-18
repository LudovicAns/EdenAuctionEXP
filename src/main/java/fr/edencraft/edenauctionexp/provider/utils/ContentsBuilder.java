package fr.edencraft.edenauctionexp.provider.utils;

import fr.edencraft.edenauctionexp.utils.ColoredText;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContentsBuilder {

    private InventoryContents contents;

    public ContentsBuilder(InventoryContents contents) {
        this.contents = contents;
    }

    private ItemStack createItemStack(Material material, @Nullable String displayName, @Nullable List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (displayName != null) {
            itemMeta.displayName(Component.text(displayName));
        }
        if (lore != null) {
            List<Component> components = new ArrayList<>();
            lore.forEach(s -> components.add(Component.text(s)));
            itemMeta.lore(components);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void addActionItem(int row,
                              int column,
                              Material material,
                              @Nullable String displayName,
                              @Nullable List<String> lore,
                              Action action) {
        ItemStack itemStack = createItemStack(material, displayName, lore);

        contents.set(row, column, ClickableItem.of(itemStack, event -> {
            if (action != null) action.run(event);
        }));
    }

    public void addInventoryItem(int row,
                                 int column,
                                 SmartInventory inventory,
                                 Material material,
                                 @Nullable String displayName,
                                 @Nullable List<String> lore) {

        ItemStack itemStack = createItemStack(material, displayName, lore);

        contents.set(row, column, ClickableItem.of(itemStack, event -> {
            if (event.isLeftClick()) {
                inventory.open((Player) event.getWhoClicked());
            }
        }));
    }

    public void addInventoryItem(int row,
                                 int column,
                                 SmartInventory inventory,
                                 int page,
                                 Material material,
                                 @Nullable String displayName,
                                 @Nullable List<String> lore) {
        ItemStack itemStack = createItemStack(material, displayName, lore);

        contents.set(row, column, ClickableItem.of(itemStack, event -> {
            if (event.isLeftClick()) {
                inventory.open((Player) event.getWhoClicked(), page);
            }
        }));
    }

    public void addBackItem(int row, int column, SmartInventory inventory, int page) {
        ItemStack itemStack = createItemStack(Material.BARRIER, new ColoredText("&cRetour").treat(), null);

        contents.set(row, column, ClickableItem.of(itemStack, event -> {
            if (event.isLeftClick()) {
                Player player = (Player) event.getWhoClicked();
                inventory.open(player, page);
            }
        }));
    }

    public void addBackItem(int row, int column, SmartInventory inventory) {
        ItemStack itemStack = createItemStack(Material.BARRIER, new ColoredText("&cRetour").treat(), null);

        contents.set(row, column, ClickableItem.of(itemStack, event -> {
            if (event.isLeftClick()) {
                Player player = (Player) event.getWhoClicked();
                inventory.open(player);
            }
        }));
    }

    public void addCloseItem(int row, int column) {
        ItemStack itemStack = createItemStack(Material.BARRIER, new ColoredText("&4Fermer").treat(), null);

        contents.set(row, column, ClickableItem.of(itemStack, event -> {
            if (event.isLeftClick()) event.getWhoClicked().closeInventory();
        }));
    }


}
