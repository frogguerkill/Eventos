package eventos;

import java.sql.Connection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import classes.DestemidoListener;
import classes.EventoListener;
import classes.EventoManager;
import commands.CommandBolao;
import commands.CommandEvento;
import commands.CommandLoteria;
import commands.DestemidoCommand;
import net.milkbowl.vault.economy.Economy;
import utils.DestemidoDataMySQL;
import utils.MySQLConnection;

public class Main extends JavaPlugin {

    private static Main INSTANCE;

    @Override
    public void onEnable() {
    	
    	String host = "localhost";
        String database = "minecraft";
        String user = "root";
        String password = "123";

        Connection connection = MySQLConnection.connect(
                host,
                database,
                user,
                password
        );

        if (connection == null) {
            getLogger().severe("Não foi possível conectar ao MySQL!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 2️⃣ Cria tabelas se não existirem
        DestemidoDataMySQL.createTables(connection);

        // 3️⃣ Inicializa sistema Destemido
        DestemidoDataMySQL.setup(this, connection);

        // 4️⃣ Registra comando e eventos
        getCommand("destemido").setExecutor(new DestemidoCommand(this));
        Bukkit.getPluginManager().registerEvents(new DestemidoListener(), this);

        getLogger().info("Evento Destemido carregado com sucesso.");
        
        INSTANCE = this;

        // Registro de comandos (padrão Bukkit)
        getCommand("bolao").setExecutor(new CommandBolao());
        getCommand("loteria").setExecutor(new CommandLoteria());
        getCommand("evento").setExecutor(new CommandEvento());
        getCommand("destemido").setExecutor(new DestemidoCommand(this));

        // Registro de listeners
        Bukkit.getPluginManager().registerEvents(new DestemidoListener(), this);
        Bukkit.getPluginManager().registerEvents(new EventoListener(), this);    

        initVault();
    }

    @Override
    public void onDisable() {
        if (EventoManager.getEvento() != null) {
            EventoManager.getEvento().stop();
        }
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    /* ==========================
       VAULT (OPCIONAL)
       ========================== */

    private static Economy ECON;

    private void initVault() {
        RegisteredServiceProvider<Economy> provider =
                Bukkit.getServicesManager().getRegistration(Economy.class);

        if (provider != null) {
            ECON = provider.getProvider();
        }
    }

    public static Economy getEcon() {
        return ECON;
    }
}
