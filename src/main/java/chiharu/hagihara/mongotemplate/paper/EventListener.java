package chiharu.hagihara.mongotemplate.paper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventListener implements Listener {

    private MongoTemplate plugin;

    public EventListener(MongoTemplate plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        //        LoginThread thread = new LoginThread(this, e.getPlayer());
        //        thread.start();
        Player player = e.getPlayer();
        LocalDateTime time = LocalDateTime.now();
        MongoDBManager mongo = new MongoDBManager(plugin, "test");
        mongo.queryInsertOne(
                "{'mcid':'" + player.getName() + "', " +
                        "'uuid':'" + player.getUniqueId() + "', " +
                        "'ip':'" + Objects.requireNonNull(player.getAddress()).getHostName() + "', " +
                        "'date':'" + time + "'}"
        );
        mongo.close();
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
        MongoDBManager mongo = new MongoDBManager(plugin, "test");
        mongo.queryInsertOne(
                "{'mcid':'" + player.getName() + "', " +
                        "'uuid':'" + player.getUniqueId() + "', " +
                        "'ip':'" + Objects.requireNonNull(player.getAddress()).getHostName() + "', " +
                        "'date':'" + time + "'}"
        );
        mongo.close();
    }
}
