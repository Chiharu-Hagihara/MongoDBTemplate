package chiharu.hagihara.mongotemplate.paper;

import org.bson.Document;
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
        new Thread(() -> {
            Player player = e.getPlayer();
            LocalDateTime time = LocalDateTime.now();
            MongoDBManager mongo = new MongoDBManager(plugin, "LoginListener");
            Document doc = new Document();

            doc.append("mcid", player.getName());
            doc.append("uuid", player.getUniqueId().toString());
            doc.append("ip", Objects.requireNonNull(player.getAddress()).getHostName());
            doc.append("date", time.toString());
            mongo.queryInsertOne(doc);
            mongo.close();
        }
        ).start();
    }
}
