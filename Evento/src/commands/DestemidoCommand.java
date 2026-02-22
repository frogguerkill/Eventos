package commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import classes.DestemidoManager;
import eventos.Main;
import models.DestemidoGUI;

public class DestemidoCommand implements CommandExecutor {

    private final Main plugin;

    public DestemidoCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            if (sender instanceof Player p) {
                DestemidoGUI.open(p);
            }
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "iniciar" -> {
                double coins = 1500;
                if (args.length >= 2) {
                    try { coins = Double.parseDouble(args[1]); } catch (Exception ignored) {}
                }
                DestemidoManager.iniciar(plugin, coins);
            }

            case "entrar" -> {
                if (sender instanceof Player p) {
                    DestemidoManager.entrar(p);
                }
            }

            case "setspawn" -> {
                if (sender instanceof Player p) {
                    DestemidoManager.setSpawnJogadores(p.getLocation());
                    p.sendMessage("§aSpawn dos jogadores definido.");
                }
            }

            case "setdragao" -> {
                if (sender instanceof Player p) {
                    DestemidoManager.setSpawnDragao(p.getLocation());
                    p.sendMessage("§aSpawn do dragão definido.");
                }
            }

            case "status" -> {
                sender.sendMessage("§eStatus: " +
                        (DestemidoManager.isAtivo() ? "§cAtivo" :
                        DestemidoManager.isAguardando() ? "§aAguardando" : "§7Inativo"));
            }

            case "stop" -> {
                DestemidoManager.finalizar();
            }
        }
        return true;
    }
}
