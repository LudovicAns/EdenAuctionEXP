package fr.edencraft.edenauctionexp;

import co.aikar.commands.PaperCommandManager;
import fr.edencraft.edenauctionexp.auction.Auction;
import fr.edencraft.edenauctionexp.command.EdenAuctionEXPCommand;
import fr.edencraft.edenauctionexp.manager.ConfigurationManager;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class EdenAuctionEXP extends JavaPlugin {

    private static EdenAuctionEXP INSTANCE;

    private ConfigurationManager configurationManager = null;
    private Auction auction = null;
    private Economy economy = null;
    private InventoryManager inventoryManager = null;

    private String asciiArt;
    {
        this.asciiArt = """
                   
                   ▄████████    ▄████████    ▄████████ ▀████    ▐████▀    ▄███████▄\t
                  ███    ███   ███    ███   ███    ███   ███▌   ████▀    ███    ███\t
                  ███    █▀    ███    ███   ███    █▀     ███  ▐███      ███    ███\t EdenAuctionEXP v1.0.0
                 ▄███▄▄▄       ███    ███  ▄███▄▄▄        ▀███▄███▀      ███    ███\t Developed by NayeOne
                ▀▀███▀▀▀     ▀███████████ ▀▀███▀▀▀        ████▀██▄     ▀█████████▀ \t For EdenCraft Server
                  ███    █▄    ███    ███   ███    █▄    ▐███  ▀███      ███       \t
                  ███    ███   ███    ███   ███    ███  ▄███     ███▄    ███       \t
                  ██████████   ███    █▀    ██████████ ████       ███▄  ▄████▀     \t                                                         
                """;
    }

    @Override
    public void onEnable() {
        long delay = System.currentTimeMillis();
        Bukkit.getLogger().log(Level.INFO, "\n" + asciiArt);

        if (!setupEconomy()) {
            log(Level.SEVERE, "⚠️ Missing Vault on this server !");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();

        this.INSTANCE = this;
        this.configurationManager = new ConfigurationManager(this);
        this.configurationManager.setupFiles();

        this.auction = new Auction();
        this.auction.load();

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new EdenAuctionEXPCommand());

        log(Level.INFO, "✅ EdenAuctionEXP enabled. (took " + (System.currentTimeMillis() - delay) + "ms)");
    }

    @Override
    public void onDisable() {
        configurationManager.saveFiles();
        this.auction.saveData();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public static EdenAuctionEXP getINSTANCE() {
        return INSTANCE;
    }

    public void log(Level level, String message) {
        switch (level.getName()) {
            default -> Bukkit.getLogger()
                    .log(level, "[" + getPlugin(EdenAuctionEXP.class).getName() + "] " + message);
            case "INFO" -> Bukkit.getLogger()
                    .log(level, ChatColor.GREEN + "[" + getPlugin(EdenAuctionEXP.class).getName() + "] " + message);
            case "WARNING" -> Bukkit.getLogger()
                    .log(level, ChatColor.GOLD + "[" + getPlugin(EdenAuctionEXP.class).getName() + "] " + message);
            case "SEVERE" -> Bukkit.getLogger()
                    .log(level, ChatColor.RED + "[" + getPlugin(EdenAuctionEXP.class).getName() + "] " + message);
        }
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public Auction getAuction() {
        return auction;
    }

    public Economy getEconomy() {
        return economy;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
