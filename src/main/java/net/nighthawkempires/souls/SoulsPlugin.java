package net.nighthawkempires.souls;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.souls.command.*;
import net.nighthawkempires.souls.config.ConfigModel;
import net.nighthawkempires.souls.config.registry.ConfigRegistry;
import net.nighthawkempires.souls.config.registry.FConfigRegistry;
import net.nighthawkempires.souls.data.InventoryData;
import net.nighthawkempires.souls.enchantment.*;
import net.nighthawkempires.souls.listener.InventoryListener;
import net.nighthawkempires.souls.listener.PlayerListener;
import net.nighthawkempires.souls.scoreboard.SoulsScoreboard;
import net.nighthawkempires.souls.tabcompleters.SoulChestTabCompleter;
import net.nighthawkempires.souls.tabcompleters.SoulTopTabCompleter;
import net.nighthawkempires.souls.tabcompleters.SoulsTabCompleter;
import net.nighthawkempires.souls.user.registry.MUserRegistry;
import net.nighthawkempires.souls.user.registry.UserRegistry;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static net.nighthawkempires.core.CorePlugin.*;

public class SoulsPlugin extends JavaPlugin {

    private static ConfigRegistry configRegistry;
    private static UserRegistry userRegistry;

    private static Plugin plugin;

    private static InventoryData inventoryData;

    public static Enchantment MOB_SOUL_ENCHANTMENT;
    public static Enchantment MUTATED_SOUL_ENCHANTMENT;
    public static Enchantment PLAYER_SOUL_ENCHANTMENT;
    public static Enchantment SOUL_ARMOR_ENCHANTMENT;
    public static Enchantment SOUL_CHEST_ENCHANTMENT;
    public static Enchantment SOUL_SWORD_ENCHANTMENT;

    public static NamespacedKey CONTAINED_SOULS_KEY;
    public static NamespacedKey EFFECT_TYPE_KEY;

    private static MongoDatabase mongoDatabase;

    public void onEnable() {
        plugin = this;
        if (getConfigg().getServerType() != ServerType.SETUP) {
            String pluginName = getPlugin().getName();
            try {
                String hostname = getConfigg().getMongoHostname();
                String database = getConfigg().getMongoDatabase().replaceAll("%PLUGIN%", pluginName);
                String username = getConfigg().getMongoUsername().replaceAll("%PLUGIN%", pluginName);
                String password = getConfigg().getMongoPassword();

                ServerAddress serverAddress = new ServerAddress(hostname);
                MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
                mongoDatabase = new MongoClient(serverAddress, mongoCredential, new MongoClientOptions.Builder().build()).getDatabase(database);

                configRegistry = new FConfigRegistry();
                userRegistry = new MUserRegistry(mongoDatabase);

                getLogger().info("Successfully connected to MongoDB.");

                registerCommands();
                registerEnchantments();

                inventoryData = new InventoryData();

                registerCommands();
                registerEnchantments();
                registerKeys();
                registerListeners();

                CorePlugin.getScoreboardManager().addScoreboard(new SoulsScoreboard());
            } catch (Exception exception) {
                exception.printStackTrace();
                getLogger().warning("Could not connect to MongoDB, shutting plugin down...");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    private void registerCommands() {
        getCommand("extractsouls").setExecutor(new ExtractSoulsCommand());
        getCommand("soulchest").setExecutor(new SoulChestCommand());
        getCommand("soulcount").setExecutor(new SoulCountCommand());
        getCommand("souls").setExecutor(new SoulsCommand());
        getCommand("soulshop").setExecutor(new SoulShopCommand());
        getCommand("soulsrankup").setExecutor(new SoulsRankupCommand());
        getCommand("soultop").setExecutor(new SoulTopCommand());
    }

    private void registerEnchantments() {
        getEnchantmentManager().registerEnchantment(new MobSoulWrapper());
        getEnchantmentManager().registerEnchantment(new MutatedSoulWrapper());
        getEnchantmentManager().registerEnchantment(new PlayerSoulWrapper());
        getEnchantmentManager().registerEnchantment(new SoulArmorWrapper());
        getEnchantmentManager().registerEnchantment(new SoulChestWrapper());
        getEnchantmentManager().registerEnchantment(new SoulSwordWrapper());

        MOB_SOUL_ENCHANTMENT = getEnchantmentManager().getEnchantment(plugin, "mob_soul");
        MUTATED_SOUL_ENCHANTMENT = getEnchantmentManager().getEnchantment(plugin, "mutated_soul");
        PLAYER_SOUL_ENCHANTMENT = getEnchantmentManager().getEnchantment(plugin, "player_soul");
        SOUL_ARMOR_ENCHANTMENT = getEnchantmentManager().getEnchantment(plugin, "soul_armor");
        SOUL_CHEST_ENCHANTMENT = getEnchantmentManager().getEnchantment(plugin, "soul_chest");
        SOUL_SWORD_ENCHANTMENT = getEnchantmentManager().getEnchantment(plugin, "soul_sword");
    }

    public void registerKeys() {
        CONTAINED_SOULS_KEY = new NamespacedKey(this, "contained_souls");
        EFFECT_TYPE_KEY = new NamespacedKey(this, "effect_type");
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new PlayerListener(), this);
    }

    public void registerTabCompleters() {
        this.getCommand("soulchest").setTabCompleter(new SoulChestTabCompleter());
        this.getCommand("souls").setTabCompleter(new SoulsTabCompleter());
        this.getCommand("soultop").setTabCompleter(new SoulTopTabCompleter());
    }

    public static UserRegistry getUserRegistry() {
        return userRegistry;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static ConfigModel getSoulsConfig() {
        return configRegistry.getConfig();
    }

    public static InventoryData getInventoryData() {
        return inventoryData;
    }
}