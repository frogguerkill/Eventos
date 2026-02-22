package models;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import utils.DestemidoDataMySQL;

public class DestemidoGUI {

    public static void open(Player player) {

        Inventory inv = Bukkit.createInventory(null, 45, "§c§lDESTEMIDO");

        /* ===============================
           INFO DO JOGADOR (TOPO)
           =============================== */
        ItemStack perfil = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) perfil.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName("§aSeu Histórico");
        meta.setLore(List.of(
                "§7Vezes como Destemido:",
                "§f" + DestemidoDataMySQL.getVezes(player.getUniqueId())
        ));
        perfil.setItemMeta(meta);

        inv.setItem(4, perfil);

        /* ===============================
           ÚLTIMO DESTEMIDO (BAIXO)
           =============================== */
        ItemStack ultimo = new ItemStack(Material.PAPER);
        ItemMeta ultimoMeta = ultimo.getItemMeta();
        ultimoMeta.setDisplayName("§7Último Destemido");
        ultimoMeta.setLore(List.of(
                "§f" + DestemidoDataMySQL.getUltimoDestemido()
        ));
        ultimo.setItemMeta(ultimoMeta);

        inv.setItem(40, ultimo);

        /* ===============================
           TOPS
           =============================== */
        inv.setItem(20, criarTop(
                Material.DIAMOND_SWORD,
                "§cTop Finalizador",
                DestemidoDataMySQL.getTopFinalizador()
        ));

        inv.setItem(22, criarTop(
                Material.BLAZE_POWDER,
                "§cTop Maior Dano",
                DestemidoDataMySQL.getTopDano()
        ));

        inv.setItem(24, criarTop(
                Material.IRON_SWORD,
                "§cTop Participação",
                DestemidoDataMySQL.getTopParticipacao()
        ));

        player.openInventory(inv);
    }

    /* ===============================
       ITEM PADRÃO DE TOP
       =============================== */
	private static ItemStack criarTop(Material material, String nome, List<String> lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(nome);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
