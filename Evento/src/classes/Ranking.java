package classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Ranking {

    private static Location[] podiums;
    private static Location[] signs;
    private static ArmorStand[] stands = new ArmorStand[3];

    @SuppressWarnings("deprecation")
	public static void updateHead(int posicao, String playerName) {

        if (posicao < 1 || posicao > 3) return;

        int index = posicao - 1;

        Location standLoc = getPodiums()[index];
        Location signLoc = getSigns()[index];

        // Remove armorstand antigo
        if (stands[index] != null && !stands[index].isDead()) {
            stands[index].remove();
        }

        // Cria armorstand
        ArmorStand stand = (ArmorStand) standLoc.getWorld().spawnEntity(
                standLoc,
                EntityType.ARMOR_STAND
        );

        stand.setSmall(true);
        stand.setGravity(false);
        stand.setBasePlate(false);
        stand.setArms(true);
        stand.setInvulnerable(true);
        stand.setCustomNameVisible(false);

        // Cabeça com skin do jogador
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        head.setItemMeta(meta);

        stand.getEquipment().setHelmet(head);

        stands[index] = stand;

        // Atualiza placa
        updateSign(signLoc, posicao, playerName);
    }

    /* ==========================
       SIGN
       ========================== */

    @SuppressWarnings("deprecation")
	private static void updateSign(Location loc, int posicao, String name) {
        if (loc == null || loc.getWorld() == null) return;

        Block block = loc.getBlock();
        block.setType(Material.OAK_WALL_SIGN);

        Sign sign = (Sign) block.getState();

        sign.setLine(0, "§6§lTOP " + posicao);
        sign.setLine(1, "§f" + name);
        sign.setLine(2, "§7Evento");

        sign.update();
    }

    /* ==========================
       LOCATIONS SEGURAS
       ========================== */

    private static Location[] getPodiums() {
        if (podiums == null) {
            World world = Bukkit.getWorld("eventos");
            if (world == null) throw new IllegalStateException("Mundo 'eventos' não encontrado.");

            podiums = new Location[] {
                new Location(world, 3.480, 31.0, -152.545),
                new Location(world, 2.530, 30.0, -152.445),
                new Location(world, 4.540, 29.0, -152.692)
            };
        }
        return podiums;
    }

    private static Location[] getSigns() {
        if (signs == null) {
            World world = Bukkit.getWorld("eventos");
            if (world == null) throw new IllegalStateException("Mundo 'eventos' não encontrado.");

            signs = new Location[] {
                new Location(world, 3.491, 30.0, -153.300),
                new Location(world, 2.539, 29.0, -153.300),
                new Location(world, 4.517, 28.0, -153.300)
            };
        }
        return signs;
    }
}
