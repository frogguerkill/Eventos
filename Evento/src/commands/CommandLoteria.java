package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import classes.EventoManager;
import models.Loteria;
import utils.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandLoteria implements CommandExecutor {

    private static final long COOLDOWN_MS = 3_000;
    private final Map<UUID, Long> cooldown = new HashMap<>();

    private boolean emCooldown(Player player) {
        long agora = System.currentTimeMillis();
        long ultimo = cooldown.getOrDefault(player.getUniqueId(), 0L);

        if (agora - ultimo < COOLDOWN_MS) {
            long restante = (COOLDOWN_MS - (agora - ultimo)) / 1000;
            player.sendMessage("§cAguarde " + restante + "s para apostar novamente.");
            return true;
        }

        cooldown.put(player.getUniqueId(), agora);
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§cUse: /loteria <número|iniciar|parar>");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "iniciar":
            case "start":
                if (!sender.hasPermission("loteria.admin")) {
                    sender.sendMessage("§cVocê não tem permissão.");
                    return true;
                }

                if (EventoManager.getEvento() != null) {
                    sender.sendMessage("§cJá existe um evento ocorrendo.");
                    return true;
                }

                int max = 200;
                int premio = 500;

                if (args.length >= 2 && NumberUtils.isInteger(args[1])) {
                    max = Integer.parseInt(args[1]);
                }
                if (args.length >= 3 && NumberUtils.isInteger(args[2])) {
                    premio = Integer.parseInt(args[2]);
                }

                Loteria loteria = new Loteria(max, premio, 180);
                loteria.setAward(premio);
                loteria.start();
                break;

            case "parar":
            case "stop":
            case "encerrar":
                if (!sender.hasPermission("loteria.admin")) {
                    sender.sendMessage("§cSem permissão.");
                    return true;
                }

                if (EventoManager.getEvento() instanceof Loteria) {
                    EventoManager.getEvento().stop();
                    Bukkit.broadcastMessage("§cA loteria foi encerrada.");
                }
                break;

            default:
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cApenas jogadores podem apostar.");
                    return true;
                }

                if (emCooldown(player)) return true;

                if (!NumberUtils.isInteger(args[0])) {
                    player.sendMessage("§cNúmero inválido.");
                    return true;
                }

                if (!(EventoManager.getEvento() instanceof Loteria loteriaAtiva)) {
                    player.sendMessage("§cNenhuma loteria ativa.");
                    return true;
                }

                int numero = Integer.parseInt(args[0]);
                loteriaAtiva.addAposta();

                if (numero == loteriaAtiva.getSelected()) {
                    loteriaAtiva.finish(player);
                } else {
                    player.sendMessage("§cOops, você errou!");
                }
                break;
        }

        return true;
    }
}
