package de.shockbase.levelborder.config;

import de.shockbase.levelborder.Levelborder;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class PlayerConfig {

    private static File file;
    private static FileConfiguration fileConfiguration;

    public static void setup(Player player) {
        JavaPlugin plugin = Levelborder.getInstance();

        file = new File(plugin.getDataFolder(), player.getUniqueId() + ".yml");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Bukkit.getLogger().info("[LevelBorder] new config file created for: " + player.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getPlayerConfig() {
        return fileConfiguration;
    }

    public static void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reset(Player player) {
        JavaPlugin plugin = Levelborder.getInstance();
        String playerName = player.getName();

        long startTime = PlayerConfig.getPlayerConfig().getLong("startTime");
        Location origin = PlayerConfig.getPlayerConfig().getLocation("borderCenter");
        int minBorderRadius = PlayerConfig.getPlayerConfig().getInt("minBorderRadius");
        int level = PlayerConfig.getPlayerConfig().getInt("level");

        int radius = minBorderRadius + level;
        long currentTime = System.currentTimeMillis() / 1000;
        int time = (int) (currentTime - startTime) + 10;

        if (origin != null) {
            resetChunks(origin, radius, time);
            Collection<LivingEntity> livingEntities = player.getWorld().getNearbyLivingEntities(origin, radius);
            if(!livingEntities.isEmpty()) {
                for (LivingEntity livingEntity : livingEntities) {
                    livingEntity.remove();
                }
            }
        }

        deleteFile(plugin.getDataFolder().getPath(), player.getUniqueId() + ".yml", playerName + " configuration deleted");
        deleteFile(Bukkit.getServer().getWorldContainer().getPath(), player.getWorld().getName() + "/playerdata/" + player.getUniqueId() + ".dat", playerName + " player data deleted");
        deleteFile(Bukkit.getServer().getWorldContainer().getPath(), player.getWorld().getName() + "/stats/" + player.getUniqueId() + ".json", playerName + " player data deleted");
    }

    private static void deleteFile(String parent, String child, String info) {
        File playerStats = new File(parent, child);
        if (playerStats.exists()) {
            if (playerStats.delete()) {
                Bukkit.getLogger().info("[LevelBorder] " + info);
            }
        }
    }

    private static void resetChunks(Location origin, int radius, int time) {
        Runnable runnable = () -> {
            try {
                CoreProtectAPI CoreProtect = getCoreProtect();
                if (CoreProtect != null) { //Ensure we have access to the API
                    List<String[]> lookup = CoreProtect.performRollback(time, null, null, null, null, null, radius, origin);
                    if (lookup != null) {
                        Bukkit.getLogger().info("[LevelBorder] " + lookup.size() + " changes rolled back.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private static CoreProtectAPI getCoreProtect() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        if (CoreProtect.APIVersion() < 7) {
            return null;
        }

        return CoreProtect;
    }
}
