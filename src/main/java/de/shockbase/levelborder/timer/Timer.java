package de.shockbase.levelborder.timer;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0);
    private boolean paused;
    private int elapsedTime;

    public void start(Player player) {
        executorService.scheduleAtFixedRate(() -> {
            if (!this.paused) {
                elapsedTime += 100;
            }
            int days = (elapsedTime / 86400000);
            int hours = (elapsedTime / 3600000) % 24;
            int minutes = (elapsedTime / 60000) % 60;
            int seconds = (elapsedTime / 1000) % 60;

            StringBuilder message = new StringBuilder("§6§l"); //bold and gold
            if (days > 0) {
                message.append(String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds));
            } else if (hours > 0) {
                message.append(String.format("%02dh %02dm %02ds", hours, minutes, seconds));
            } else if (minutes > 0) {
                message.append(String.format("%02dm %02ds", minutes, seconds));
            } else {
                message.append(String.format("%02ds", seconds));
            }

            sendActionbar(player, message.toString());

        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdownNow();
    }

    public int getElapsedTime() {
        return this.elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    private void sendActionbar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }
}
