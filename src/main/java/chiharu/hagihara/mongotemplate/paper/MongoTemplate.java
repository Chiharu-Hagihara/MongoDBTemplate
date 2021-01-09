package chiharu.hagihara.mongotemplate.paper;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MongoTemplate extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
