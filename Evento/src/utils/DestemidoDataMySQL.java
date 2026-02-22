package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eventos.Main;

public class DestemidoDataMySQL {

    private static Connection connection;
    private static Main plugin;

    /* ===============================
       SETUP
       =============================== */
    public static void setup(Main pl, Connection conn) {
        plugin = pl;
        connection = conn;
    }

    /* ===============================
       REGISTRO DO RESULTADO DO EVENTO
       =============================== */
    public static void registrarResultado(
            Player finalizador,
            UUID maiorDano,
            UUID maiorParticipacao
    ) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {

                if (finalizador != null) {
                    UUID uuid = finalizador.getUniqueId();

                    upsertPlayer(uuid, finalizador.getName(),
                            "vezes_destemido = vezes_destemido + 1",
                            "top_finalizador = top_finalizador + 1");

                    setUltimoDestemido(finalizador.getName());

                    aplicarTag(finalizador);
                }

                if (maiorDano != null) {
                    upsertPlayer(maiorDano, null,
                            "top_dano = top_dano + 1");
                }

                if (maiorParticipacao != null) {
                    upsertPlayer(maiorParticipacao, null,
                            "top_participacao = top_participacao + 1");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    public static void createTables(Connection connection) {

        try {

            String playersTable = """
                CREATE TABLE IF NOT EXISTS destemido_players (
                    uuid VARCHAR(36) PRIMARY KEY,
                    nome VARCHAR(16),
                    vezes_destemido INT DEFAULT 0,
                    top_finalizador INT DEFAULT 0,
                    top_participacao INT DEFAULT 0,
                    top_dano DOUBLE DEFAULT 0
                );
                """;

            String globalTable = """
                CREATE TABLE IF NOT EXISTS destemido_global (
                    id INT PRIMARY KEY,
                    ultimo_destemido VARCHAR(16)
                );
                """;

            String insertGlobal = """
                INSERT IGNORE INTO destemido_global (id, ultimo_destemido)
                VALUES (1, 'Nenhum');
                """;

            Statement stmt = connection.createStatement();
            stmt.execute(playersTable);
            stmt.execute(globalTable);
            stmt.execute(insertGlobal);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ===============================
       UPSERT PLAYER
       =============================== */
    private static void upsertPlayer(UUID uuid, String nome, String... updates) throws SQLException {

        boolean incluiVezes = Arrays.stream(updates)
                .anyMatch(s -> s.contains("vezes_destemido"));

        String sql;

        if (incluiVezes) {
            sql = """
                INSERT INTO destemido_players (uuid, nome, vezes_destemido)
                VALUES (?, ?, 1)
                ON DUPLICATE KEY UPDATE
                """ + String.join(", ", updates);
        } else {
            sql = """
                INSERT INTO destemido_players (uuid, nome)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE
                """ + String.join(", ", updates);
        }

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, uuid.toString());
        ps.setString(2, nome);
        ps.executeUpdate();
        ps.close();
    }

    /* ===============================
       ÚLTIMO DESTEMIDO
       =============================== */
    private static void setUltimoDestemido(String nome) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(
                "UPDATE destemido_global SET ultimo_destemido=? WHERE id=1");
        ps.setString(1, nome);
        ps.executeUpdate();
        ps.close();
    }

    public static String getUltimoDestemido() {

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT ultimo_destemido FROM destemido_global WHERE id=1");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("ultimo_destemido");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Nenhum";
    }

    /* ===============================
       VEZES DESTEMIDO (PLAYER)
       =============================== */
    public static int getVezes(UUID uuid) {

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT vezes_destemido FROM destemido_players WHERE uuid=?")) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("vezes_destemido");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* ===============================
       TOP 3
       =============================== */
    public static List<String> getTopFinalizador() {
        return getTop("top_finalizador");
    }

    public static List<String> getTopDano() {
        return getTop("top_dano");
    }

    public static List<String> getTopParticipacao() {
        return getTop("top_participacao");
    }

    private static List<String> getTop(String coluna) {

        List<String> lore = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT nome FROM destemido_players ORDER BY " + coluna + " DESC LIMIT 3");
             ResultSet rs = ps.executeQuery()) {

            int pos = 1;
            while (rs.next()) {
                lore.add("§f" + pos++ + "º §7" + rs.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lore.isEmpty()) {
            lore.add("§7Nenhum registro");
        }

        return lore;
    }

    /* ===============================
       TAG [DESTEMIDO]
       =============================== */
    private static void aplicarTag(Player player) {

        // remove tag antiga (caso queira apenas 1 destemido)
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "lp user " + player.getName() + " meta removeprefix 100"
        );

        // aplica nova
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "lp user " + player.getName() + " meta addprefix 100 \"&c[Destemido] \""
        );
    }
}

