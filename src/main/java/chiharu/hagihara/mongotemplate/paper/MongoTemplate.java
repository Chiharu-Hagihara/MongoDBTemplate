package chiharu.hagihara.mongotemplate.paper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.Objects;

public final class MongoTemplate extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        LoginThread thread = new LoginThread(this, e.getPlayer());
        thread.start();
    }
}

class LoginThread extends Thread {
    private MongoTemplate plugin;
    private Player player;
    private LocalDateTime time = LocalDateTime.now();
    public LoginThread(MongoTemplate plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void run() {
        try (MongoDBManager mongo = new MongoDBManager(plugin, "test")) {
            mongo.queryInsertOne(
                    "{'mcid':'" + player.getName() +"', " +
                            "'uuid':'" + player.getUniqueId() + "', " +
                            "'ip':'" + Objects.requireNonNull(player.getAddress()).getHostName() + "', " +
                            "'date':'" + time + "'}"
            );
        }
    }
}
