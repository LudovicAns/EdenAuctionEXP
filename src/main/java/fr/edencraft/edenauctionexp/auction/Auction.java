package fr.edencraft.edenauctionexp.auction;

import com.google.gson.*;
import fr.edencraft.edenauctionexp.EdenAuctionEXP;
import fr.edencraft.edenauctionexp.auction.event.OrderCanceled;
import fr.edencraft.edenauctionexp.auction.event.OrderPlaced;
import fr.edencraft.edenauctionexp.auction.event.OrderSuccess;
import fr.edencraft.edenauctionexp.manager.ConfigurationManager;
import fr.edencraft.edenauctionexp.utils.JsonUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Auction {

    // Data that depends on Auction#loadData function.
    private List<Order> orders = null;

    // Data that depends on Auction#loadParameters function.
    private static double minPrice;
    private static double maxPrice;
    private static int minExperience;
    private static int maxExperience;
    private static int maxOrder;

    private static boolean loaded = false;

    private final EdenAuctionEXP plugin = EdenAuctionEXP.getINSTANCE();

    /**
     * Load parameters of Auction from config.yml and also data to get all existing orders.
     */
    public void load() {
        loadParameters();
        loadData();

        loaded = true;
    }

    /**
     * This method is very useful to create a reload command.
     * Call it when configuration file "config.yml" has been modified.
     */
    public void loadParameters() {
        FileConfiguration config = getConfig();
        plugin.log(Level.INFO, "⏳ Loading parameters for Auction from config.yml ...");
        minPrice = config.getDouble("min-price");
        plugin.log(Level.INFO, "| 👉 min-price: " + minPrice);
        maxPrice = config.getDouble("max-price");
        plugin.log(Level.INFO, "| 👉 max-price: " + maxPrice);
        minExperience = config.getInt("min-experience");
        plugin.log(Level.INFO, "| 👉 min-experience: " + minExperience);
        maxExperience = config.getInt("max-experience");
        plugin.log(Level.INFO, "| 👉 max-experience: " + maxExperience);
        maxOrder = config.getInt("max-order");
        plugin.log(Level.INFO, "| 👉 max-order: " + maxOrder);
        plugin.log(Level.INFO, "✅ Parameters loaded");
    }

    /**
     * Load data for Auction from AuctionData.json.
     * If file can't be parsed, plugin will be disabled.
     */
    public void loadData() {
        this.orders = new ArrayList<>();
        File dataFile = getDataFile();
        plugin.log(Level.INFO, "⏳ Loading data for Auction from AuctionData.json ...");

        if (!dataFile.exists()) {
            plugin.log(Level.INFO, "| ❌ No data to load from AuctionData.json.");
            plugin.log(Level.INFO, "✅ Load ignored.");
            return;
        }
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(dataFile));
            JsonArray orders = jsonElement.getAsJsonObject().get("orders").getAsJsonArray();
            for (JsonElement orderElement : orders) {
                int id = orderElement.getAsJsonObject().get("id").getAsInt();
                JsonElement uuidElement = orderElement.getAsJsonObject().get("playerUUID");
                UUID uuid = null;
                if (!uuidElement.isJsonNull()) {
                    uuid = UUID.fromString(uuidElement.getAsString());
                }
                double amount = orderElement.getAsJsonObject().get("amount").getAsDouble();
                double price = orderElement.getAsJsonObject().get("price").getAsDouble();
                OrderType orderType = OrderType.valueOf(orderElement.getAsJsonObject().get("orderType").getAsString());
                long creationDate = orderElement.getAsJsonObject().get("creationDate").getAsLong();
                Order order = new Order(id, uuid, amount, price, orderType, creationDate);
                this.orders.add(order);
                plugin.log(Level.INFO, "| 👉 Order id n°" + order.getId() + " has been added to auction.");
            }
        } catch (FileNotFoundException e) {
            plugin.log(Level.SEVERE, "| ⚠️  " + dataFile.getName() + " can't be parsed.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            e.printStackTrace();
        }

        plugin.log(Level.INFO, "✅ Loaded !");
    }

    public void saveData() {
        // Save as JSON File
        plugin.log(Level.INFO, "⏳ Saving data for Auction to AuctionData.json ...");
        File dataFolder = getDataFolder();

        if (dataFolder.mkdir()) {
            plugin.log(Level.INFO, "| 📂 " + dataFolder.getName() + " has been created successfully.");
        }

        File dataFile = getDataFile();
        if (this.orders == null || this.orders.isEmpty()) {
            plugin.log(Level.INFO, "| ❌ No data need to be saved in " + dataFile.getName() + ".");
            if (dataFile.exists()) {
                dataFile.delete();
                if (dataFile.delete()) {
                    plugin.log(Level.INFO, "| 🗑 File " + dataFile.getName()
                            + " has been deleted.");
                }
            }
            plugin.log(Level.INFO, "✅ Save ignored.");
            return;
        }

        try {
            if(dataFile.createNewFile()) {
                plugin.log(Level.INFO, "| 📄 " + dataFile.getName() + " has been created successfully.");
            }
        } catch (IOException e) {
            plugin.log(Level.SEVERE, "| ⚠️  " + dataFile.getName() + " can't be created ! See below:");
            e.printStackTrace();
        }

        HashMap<String, Object> elementMap = new HashMap<>();
        elementMap.put("orders", this.orders);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();

        JsonUtils.writeJsonData(dataFile, gson, elementMap);

        plugin.log(Level.INFO, "✅ Saved !");
    }

    private File getDataFile() {
        return new File(getDataFolder().getAbsolutePath() + File.separatorChar + "AuctionData.json");
    }

    /**
     * @param order to add in the auction.
     * @return true in case of success else false.
     */
    public boolean addOrder(Order order) {
        if (!loaded) return false;
        if (!idIsFree(order.getId())) return false;

        OrderPlaced orderPlaced = new OrderPlaced(false, this, order);
        Bukkit.getPluginManager().callEvent(orderPlaced);
        if (orderPlaced.isCancelled()) return false;

        this.orders.add(order);
        return true;
    }

    /**
     * @return true in case of success else false.
     */
    public boolean addOrder(UUID uuid, double amount, double price, OrderType orderType) {
        if (!loaded) return false;

        int id = getFreeId();
        Order order = new Order(id, uuid, amount, price, orderType, System.currentTimeMillis());

        OrderPlaced orderPlaced = new OrderPlaced(false, this, order);
        Bukkit.getPluginManager().callEvent(orderPlaced);
        if (orderPlaced.isCancelled()) return false;

        this.orders.add(order);
        return true;
    }

    public boolean idIsFree(int id) {
        return orders.stream().noneMatch(order -> order.getId() == id);
    }

    /**
     * @param id of the {@link Order}.
     * @return true in case of success else false.
     */
    public boolean removeOrder(int id) {
        if (!loaded) return false;
        if (idIsFree(id)) return false;

        Order toRemove = null;
        for (Order order : this.orders) {
            if (order.getId() == id) {
                toRemove = order;
                break;
            }
        }
        if (toRemove == null) return false;

        OrderCanceled orderCanceled = new OrderCanceled(false, this, toRemove);
        Bukkit.getPluginManager().callEvent(orderCanceled);
        if (orderCanceled.isCancelled()) return false;

        orders.remove(toRemove);
        return true;
    }

    /**
     * @param id of the {@link Order}.
     * @param buyer of the {@link Order}.
     * @return true in case of success else false.
     */
    public boolean successOrder(int id, Player buyer) {
        if (!loaded) return false;
        if (idIsFree(id)) return false;

        Order order = orders.stream().filter(order1 -> order1.getId() == id).findFirst().orElse(null);
        if (order == null) return false;

        OrderSuccess orderSuccess = new OrderSuccess(this, order, buyer);
        Bukkit.getPluginManager().callEvent(orderSuccess);
        if (orderSuccess.isCancelled()) return false;

        orders.remove(order);
        return true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @return FileConfiguration "config.yml"
     */
    public FileConfiguration getConfig() {
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        return configurationManager.getConfigurationFile("config.yml");
    }

    private File getDataFolder() {
        return new File(EdenAuctionEXP.getINSTANCE().getDataFolder().getAbsolutePath() + File.separatorChar
                + "data");
    }

    public static double getMinPrice() {
        return minPrice;
    }

    public static double getMaxPrice() {
        return maxPrice;
    }

    public static int getMinExperience() {
        return minExperience;
    }

    public static int getMaxExperience() {
        return maxExperience;
    }

    public static int getMaxOrder() {
        return maxOrder;
    }

    public long getOrderCount(UUID uuid) {
        return orders.stream().filter(order -> order.getPlayerUUID().equals(uuid)).count();
    }

    public int getFreeId() {
        int id = 0;
        while (!idIsFree(id)) {
            id++;
        }
        return id;
    }

    public List<Order> getPlayerOrders(Player player) {
        if (orders == null || orders.isEmpty()) return new ArrayList<>();
        return orders.stream().filter(order -> order.getPlayerUUID().equals(player.getUniqueId())).collect(Collectors.toList());
    }

    public List<Order> getOrders() {
        return orders;
    }
}
