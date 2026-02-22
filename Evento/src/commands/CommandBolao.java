package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import classes.EventoManager;
import classes.EventoState;
import models.Bolao;
import models.Evento;
import utils.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandBolao implements CommandExecutor {

    private static final String ADMIN_PERMISSION = "bolao.admin";
    private static final long COOLDOWN_TIME = 60_000; // 60 segundos

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Evento eventoAtual = EventoManager.getEvento();

        // /bolao
        if (args.length == 0) {

            if (!(eventoAtual instanceof Bolao) || EventoState.FECHADO.isState()) {
                sender.sendMessage("§cNão está ocorrendo nenhum bolão no momento.");
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cApenas jogadores podem participar do bolão.");
                return true;
            }

            if (EventoState.INICIADO.isState()) {
                sender.sendMessage("§cO bolão já está acabando. Tente no próximo.");
                return true;
            }

            if (isInCooldown(player)) {
                long tempo = getRemainingCooldown(player);
                sender.sendMessage("§cAguarde " + tempo + "s para apostar novamente.");
                return true;
            }

            ((Bolao) eventoAtual).addAposta(player);
            applyCooldown(player);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "estado":
            case "status": {

                if (!(eventoAtual instanceof Bolao bolao) || EventoState.FECHADO.isState()) {
                    sender.sendMessage("§cNão está ocorrendo nenhum bolão no momento.");
                    return true;
                }

                sender.sendMessage("§7§m---------------------------");
                sender.sendMessage("§fNúmero de apostas: §7" + bolao.getApostas());
                sender.sendMessage("§fPreço de inscrição: §7" + bolao.getCusto());
                sender.sendMessage("§fPrêmio total: §6" + (bolao.getApostas() * bolao.getCusto()));
                sender.sendMessage("§7§m---------------------------");
                return true;
            }

            case "iniciar":
            case "start": {

                if (!sender.hasPermission(ADMIN_PERMISSION)) {
                    sender.sendMessage("§cVocê não tem permissão para usar este comando.");
                    return true;
                }

                if (eventoAtual != null) {
                    sender.sendMessage("§cJá existe um evento ocorrendo no momento.");
                    return true;
                }

                int custo = 200;
                if (args.length >= 2 && NumberUtils.isInteger(args[1])) {
                    int valor = Integer.parseInt(args[1]);
                    if (valor > 0) {
                        custo = valor;
                    }
                }

                Bolao bolao = new Bolao(custo, 180);
                bolao.start();
                sender.sendMessage("§aBolão iniciado com sucesso. Custo: §f" + custo);
                return true;
            }

            case "parar":
            case "encerrar":
            case "stop": {

                if (!sender.hasPermission(ADMIN_PERMISSION)) {
                    sender.sendMessage("§cVocê não tem permissão para usar este comando.");
                    return true;
                }

                if (eventoAtual instanceof Bolao) {
                    eventoAtual.stop();
                    Bukkit.broadcastMessage("§cO bolão foi encerrado manualmente pela administração.");
                } else {
                    sender.sendMessage("§cNenhum bolão ativo para encerrar.");
                }
                return true;
            }

            default:
                sender.sendMessage("§cUso correto: /bolao [status|iniciar|parar]");
                return true;
        }
    }

    /* ==========================
       COOLDOWN
       ========================== */

    private boolean isInCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) &&
               (System.currentTimeMillis() - cooldowns.get(player.getUniqueId()) < COOLDOWN_TIME);
    }

    private void applyCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private long getRemainingCooldown(Player player) {
        long last = cooldowns.get(player.getUniqueId());
        long remaining = (COOLDOWN_TIME - (System.currentTimeMillis() - last)) / 1000;
        return Math.max(remaining, 0);
    }
}
