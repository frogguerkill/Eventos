package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import classes.EventoManager;
import classes.EventoState;
import classes.EventoType;
import models.Evento;
import utils.NumberUtils;

public class CommandEvento implements CommandExecutor {

    private static final String ADMIN_PERMISSION = "evento.admin";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Evento evento = EventoManager.getEvento();

        // /evento
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cApenas jogadores podem usar este comando.");
                return true;
            }

            if (evento == null) {
                sender.sendMessage("§cNão há nenhum evento ocorrendo no momento.");
                return true;
            }

            if (EventoState.INICIANDO.isState()) {

                if (EventoManager.timer > 15) {
                    EventoManager.join(player);
                } else {
                    player.sendMessage("§cO tempo para participação terminou.");
                }

                return true;
            }

            if (EventoState.INICIADO.isState()) {
                player.sendMessage("");
                player.sendMessage(" §fInformações do evento §6" + evento.getName() + " §7(em andamento)");
                player.sendMessage("");
                player.sendMessage(" §fParticipantes: §7" + EventoManager.getPlayers().size());
                player.sendMessage("");
                return true;
            }

            sender.sendMessage("§cNão há nenhum evento ocorrendo no momento.");
            return true;
        }

        /* =========================
           COMANDOS ADMIN
           ========================= */

        if (!sender.hasPermission(ADMIN_PERMISSION)) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando.");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "list":
            case "lista": {

                sender.sendMessage("");
                sender.sendMessage(" §fLista de eventos disponíveis:");
                sender.sendMessage("");

                for (EventoType type : EventoType.values()) {
                    sender.sendMessage(" §7- " + type.name().toLowerCase());
                }

                sender.sendMessage("");
                return true;
            }

            case "iniciar":
            case "start": {

                if (args.length < 2) {
                    sender.sendMessage("§cUso correto: /evento iniciar <evento> [premio]");
                    return true;
                }

                if (evento != null) {
                    sender.sendMessage("§cJá existe um evento ocorrendo no momento.");
                    return true;
                }

                try {
                    EventoType type = EventoType.valueOf(args[1].toUpperCase());

                    Evento novoEvento = type.getEventoClass()
                            .getDeclaredConstructor()
                            .newInstance();

                    if (args.length >= 3) {
                        if (!NumberUtils.isInteger(args[2]) || Integer.parseInt(args[2]) < 0) {
                            sender.sendMessage("§cO prêmio deve ser um número igual ou maior que 0.");
                            return true;
                        }
                        novoEvento.setAward(Integer.parseInt(args[2]));
                    }

                    novoEvento.start();
                    sender.sendMessage("§aEvento §f" + novoEvento.getName() + " §ainiciado com sucesso.");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cEvento não encontrado.");
                } catch (Exception e) {
                    sender.sendMessage("§cErro ao iniciar o evento. Verifique o console.");
                    e.printStackTrace();
                }
                return true;
            }

            case "parar":
            case "encerrar":
            case "stop": {

                if (evento == null) {
                    sender.sendMessage("§cNenhum evento ativo para encerrar.");
                    return true;
                }

                Bukkit.broadcastMessage("§cO evento foi encerrado manualmente pela administração.");
                evento.stop();
                return true;
            }

            default:
                sender.sendMessage("§cUso correto: /evento <lista|iniciar|parar>");
                return true;
        }
    }
}
