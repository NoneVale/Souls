package net.nighthawkempires.souls.user;

import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class UserModel implements Model {

    private String key;

    private int level;
    private int mobSouls;
    private int playerSouls;

    public UserModel(UUID uuid) {
        this.key = uuid.toString();

        this.level = 1;
        this.mobSouls = 0;
        this.playerSouls = 0;
    }

    public UserModel(String key, DataSection data) {
        this.key = key;

        this.level = 1;
        if (data.isSet("level"))
            this.level = data.getInt("level");
        this.mobSouls = data.getInt("mob_souls");
        this.playerSouls = data.getInt("player_souls");
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        SoulsPlugin.getUserRegistry().register(this);
    }

    public int getMobSouls() {
        return this.mobSouls;
    }

    public void setMobSouls(int mobSouls) {
        this.mobSouls = mobSouls;
        SoulsPlugin.getUserRegistry().register(this);
    }

    public void addMobSouls(int amount) {
        setMobSouls(getMobSouls() + amount);
    }

    public void removeMobSouls(int amount) {
        setMobSouls(getMobSouls() - amount);
    }

    public int getPlayerSouls() {
        return this.playerSouls;
    }

    public void setPlayerSouls(int playerSouls) {
        this.playerSouls = playerSouls;
        SoulsPlugin.getUserRegistry().register(this);
    }

    public void addPlayerSouls(int amount) {
        setPlayerSouls(getPlayerSouls() + amount);
    }

    public void removePlayerSouls(int amount) {
        setPlayerSouls(getPlayerSouls() - amount);
    }

    public String getKey() {
        return this.key;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("level", this.level);
        map.put("mob_souls", this.mobSouls);
        map.put("player_souls", this.playerSouls);

        return map;
    }
}
