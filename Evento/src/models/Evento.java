package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import classes.EventoData;
import classes.EventoManager;
import classes.EventoState;
import classes.EventoType;
import eventos.Main;

public abstract class Evento {

    private final EventoData data;
    private final String name;
    private final List<String> description;
    private EventoType type;

    private final Map<Integer, String> winners = new HashMap<>();
    private final List<BukkitTask> tasks = new ArrayList<>();

    private int award;

    protected Evento(String name, List<String> description, EventoData data) {
        this.name = name;
        this.description = description;
        this.data = data;
        this.award = data.defaultAward;
    }

    /* =========================
       GETTERS / SETTERS
       ========================= */

    public String getName() {
        return name;
    }

    public EventoData getData() {
        return data;
    }

    public List<String> getDescription() {
        return description;
    }

    public EventoType getType() {
        return type;
    }

    public int getAward() {
        return award;
    }

    public void setAward(int award) {
        this.award = award;
    }

    public Map<Integer, String> getWinners() {
        return winners;
    }

    public void setWinner(int index, String value) {
        winners.put(index, value);
    }
    
    public String getWinner(int position) {
        return winners.get(position);
    }

    public void setWinner(String playerName, int position) {
        winners.put(position, playerName);
    }

    /* =========================
       TASK CONTROL
       ========================= */

    protected void addTask(BukkitTask task) {
        tasks.add(task);
    }

    protected void cancelTasks() {
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }

    /* =========================
       EVENT FLOW
       ========================= */

    @SuppressWarnings("deprecation")
	public void join(Player player) {
        player.sendMessage("§aVocê entrou no evento. Boa sorte!");
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        EventoManager.addPlayer(player);

        if (data.lobbySpawn != null) {
            player.teleport(data.lobbySpawn);
        }
    }

    public void start() {

        EventoManager.timer = data.timer;
        EventoManager.setEvento(this);
        EventoState.INICIANDO.define();

        addTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {

            int timer = EventoManager.timer;

            if (timer == 0) {
                cancelTasks();
                started();
                EventoState.INICIADO.define();
                ontick();
                broadcastSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                return;
            }

            if (timer > 0 && timer % 30 == 0) {
                broadcastInfo();
            }

            else if (timer == 15) {
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(" §6Evento §e§l" + getName() + " §6fechado!");
                Bukkit.broadcastMessage("");

                EventoManager.getPlayers()
                        .forEach(p -> p.sendMessage(" §fIniciando evento em §715§f segundos..."));
            }

           // else if (timer <= 5 && timer > 0) {
//
  //          	Evento.sendActionBar(Component("§cPrepare-se! §f§l" + timer + "..."));
//
     //           broadcastSound(Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.5f);
    //        }

            ontick();
            EventoManager.timer = timer - 1;

        }, 0L, 20L));
    }

    	protected void started() {

    		if (EventoManager.getPlayers().isEmpty()) {
    			stop();
    			return;
    		}

    		sendInfo();

    		int index = 0;
    		for (Player p : EventoManager.getPlayers()) {
    			if (data.spawns.size() <= index) index = 0;
    			p.teleport(data.spawns.get(index++));
        }
    }

    protected void ontick() {}

    public void handle(Player player) {}

    /* =========================
       FINALIZAÇÃO
       ========================= */

    public void finish() {

        EventoState.FECHADO.define();

        Bukkit.broadcastMessage("");

        if (winners.isEmpty()) {
            Bukkit.broadcastMessage("§fNão houve nenhum vencedor no evento §7" + name + "§f.");
        } else {
            Bukkit.broadcastMessage(" §fFim do §6" + name + "§f!");
            winners.forEach((pos, value) ->
                    Bukkit.broadcastMessage("  §e" + pos + "º LUGAR §7- §f" + value));
        }

        Bukkit.broadcastMessage("");
        EventoManager.clearPlayers();
        EventoManager.setEvento(null);

        EventoManager.getPlayers().forEach(p ->
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f));

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::stop, 60L);
    }

    public void stop() {

        cancelTasks();
        EventoState.FECHADO.define();

        World world = Bukkit.getWorlds().get(0);
        EventoManager.getPlayers().forEach(p -> p.teleport(world.getSpawnLocation()));

        EventoManager.clearPlayers();
        EventoManager.setEvento(null);
    }

    /* =========================
       BROADCAST
       ========================= */

    protected void broadcastSound(Sound sound, float volume, float pitch) {
        EventoManager.getPlayers()
                .forEach(p -> p.playSound(p.getLocation(), sound, volume, pitch));
    }

    protected void sendInfo() {

        Evento evento = EventoManager.getEvento();
        if (evento == null) return;

        EventoManager.getPlayers().forEach(p -> {
            p.sendMessage("");
            p.sendMessage(" §9§l> §f" + evento.getName());
            p.sendMessage("");

            if (evento.getDescription() != null) {
                evento.getDescription()
                        .forEach(line -> p.sendMessage("  §7" + line));
            }

            p.sendMessage("");
        });
    }

    protected void broadcastInfo() {

        int time = EventoManager.timer;
        String tempo = (time >= 60)
                ? (time / 60) + " minuto(s)"
                : "30 segundos";

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(" §6Evento §e§l" + name + " §6aberto!");
        Bukkit.broadcastMessage(" §fUse §7/evento §fpara participar.");
        Bukkit.broadcastMessage(" §fPremiação: §7" + award + ".0");
        Bukkit.broadcastMessage(" §fO evento fecha em §e" + tempo + "§f.");
        Bukkit.broadcastMessage("");

        Bukkit.getOnlinePlayers().forEach(p ->
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.2f));
    }

    protected void endTask() {

        EventoManager.timer = data.maxTime;

        addTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {

            if (EventoManager.timer <= 0) {
                finish();
                return;
            }

            EventoManager.timer--;

        }, 0L, 20L));
    }
}
