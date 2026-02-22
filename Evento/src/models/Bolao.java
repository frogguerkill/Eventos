package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import classes.EventoData;
import classes.EventoManager;
import classes.EventoState;
import eventos.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;

public class Bolao extends Evento {

    private int apostas;
    private final int custo;
    private Player winner;
    private boolean none;
    private final List<UUID> inscritos;

    public Bolao(int custo, int timer) {
        super("Bolão", null, new EventoData(0, 1000, 90, timer, null, null));
        this.custo = custo;
        this.apostas = 0;
        this.inscritos = new ArrayList<>();
        this.none = false;
    }

    public int getCusto() {
        return custo;
    }

    public int getApostas() {
        return apostas;
    }

    public void addAposta(Player player) {

        if (inscritos.contains(player.getUniqueId())) {
            player.sendMessage("§cVocê já se inscreveu no bolão.");
            return;
        }

        // Economia (Vault) — opcional
        /*
        if (Main.ECON.getBalance(player) < custo) {
            player.sendMessage("§cVocê precisa de §7" + custo + "§c coins para participar.");
            return;
        }
        Main.ECON.withdrawPlayer(player, custo);
        */

        inscritos.add(player.getUniqueId());
        apostas++;

        player.sendMessage("§aVocê se inscreveu no bolão. Boa sorte!");
        player.sendMessage("§fPara mais informações utilize §6/bolao status§f.");
    }

    @Override
    public void start() {

        EventoManager.timer = getData().timer;
        EventoManager.setEvento(this);
        EventoState.INICIANDO.define();

        addTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {

            int timer = EventoManager.timer;

            if (timer == 0) {
                EventoState.INICIADO.define();

                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§fFim do §6bolão§f! Iniciando o sorteio...");
                Bukkit.broadcastMessage("");

                List<Player> online = getOnlineParticipants();

                if (online.isEmpty()) {
                    none = true;
                } else {
                    winner = online.get(new Random().nextInt(online.size()));
                }
            }

            else if (timer == -10) {
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§eSorteando...");
                Bukkit.broadcastMessage("");
            }

            else if (timer == -20) {
                if (none || winner == null) {
                    noone();
                } else {
                    finish(winner);
                }
                return;
            }

            else if (timer > 0 && timer % 30 == 0) {
                broadcastInfo();
            }

            EventoManager.timer = timer - 1;

        }, 0L, 20L));
    }

    private List<Player> getOnlineParticipants() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : inscritos) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                list.add(p);
            }
        }
        return list;
    }

    public void finish(Player player) {

        String prefix = getPrefix(player);

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§fO vencedor do bolão foi §7" + prefix + player.getName()
                + " §fe ganhou §6" + (apostas * custo) + ".0 coins§f!");
        Bukkit.broadcastMessage("§fHouve apenas §7" + apostas + " §finscrições.");
        Bukkit.broadcastMessage("");

        // Economia (Vault)
        // Main.ECON.depositPlayer(player, apostas * custo);

        stop();
    }

    private String getPrefix(Player player) {
        try {
            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                CachedMetaData meta = user.getCachedData().getMetaData();
                if (meta.getPrefix() != null) {
                    return meta.getPrefix().replace("&", "§");
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    public void noone() {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§cNão houve nenhuma inscrição no evento bolão.");
        Bukkit.broadcastMessage("§cNenhum vencedor foi encontrado.");
        Bukkit.broadcastMessage("");
        stop();
    }

    @Override
    public void stop() {
        cancelTasks();
        EventoState.FECHADO.define();
        EventoManager.setEvento(null);
    }

    @Override
    public void broadcastInfo() {

        int time = EventoManager.timer;
        String tempo;

        if (time >= 60 && time % 60 == 0) {
            tempo = (time / 60) + " minuto(s)";
        } else {
            tempo = (time > 30 ? (time / 60) + " minuto e " : "") + "30 segundos";
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(" §6Evento §e§lBOLÃO §6aberto!");
        Bukkit.broadcastMessage(" §fUse §7/bolao §fpara participar.");
        Bukkit.broadcastMessage(" §fCusto de inscrição: §7" + custo + ".0");
        Bukkit.broadcastMessage(" §fO evento encerra em §e" + tempo + "§f.");
        Bukkit.broadcastMessage("");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5F, 1.2F);
        }
    }
}
