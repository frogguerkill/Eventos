package utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import classes.DestemidoManager;
import eventos.Main;

public class DestemidoBossBar {

    private static BossBar bossBar;
    private static BukkitTask task;

    /* ===============================
       CRIAR BOSSBAR
       =============================== */
    public static void create(Main plugin, EnderDragon dragon) {

        remove();

        bossBar = Bukkit.createBossBar(
                "§c§lBAMGUELA",
                BarColor.RED,
                BarStyle.SEGMENTED_10
        );

        bossBar.setVisible(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (DestemidoManager.isParticipante(p)) {
                bossBar.addPlayer(p);
            }
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (dragon == null || dragon.isDead() || !dragon.isValid()) {
                remove();
                return;
            }

            @SuppressWarnings("deprecation")
			double progress = dragon.getHealth() / dragon.getMaxHealth();
            bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));

        }, 0L, 20L);
    }

    /* ===============================
       REMOVER BOSSBAR
       =============================== */
    public static void remove() {

        if (task != null) {
            task.cancel();
            task = null;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            bossBar = null;
        }
    }

    /* ===============================
       ADICIONAR JOGADOR
       =============================== */
    public static void addPlayer(Player p) {
        if (bossBar != null) {
            bossBar.addPlayer(p);
        }
    }

    /* ===============================
       REMOVER JOGADOR
       =============================== */
    public static void removePlayer(Player p) {
        if (bossBar != null) {
            bossBar.removePlayer(p);
        }
    }
}


