package de.shockbase.levelborder.commands;

import de.shockbase.levelborder.config.PlayerConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LevelBorderCommands implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {

            if (args[0].equalsIgnoreCase("reset")) {
                player.kick(Component.text("Reset done. Please reconnect."));
                PlayerConfig.reset(player);
                return true;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            commands.add("reset");
            return commands;
        }

        return null;
    }
}