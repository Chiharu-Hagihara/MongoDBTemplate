package chiharu.hagihara.mongotemplate.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigFile {
    private Plugin plugin = null;
    private File file;
    private Configuration config;
    private String filePatch = "config.yml";

    public ConfigFile(Plugin plugin){
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists())plugin.getDataFolder().mkdir();
        file = new File(plugin.getDataFolder(), filePatch);

        if (!file.exists()){
            try{
                file.createNewFile();
                try {
                    InputStream inputStream = plugin.getResourceAsStream("config.yml");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    ByteStreams.copy(inputStream, fileOutputStream);
                }catch (IOException e){
                    e.printStackTrace();
                    plugin.getLogger().warning("Unable to create storage file." +filePatch);
                }
            }catch (IOException e){
                e.printStackTrace();
                plugin.getLogger().warning("failed to create config.yml");
            }
        }
    }

    private InputStream getResourceAsStream(String patch){
        return plugin.getResourceAsStream(patch);
    }

    public Configuration getConfig(){
        try{
            return (this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file));
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    void saveConfig(){
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(plugin.getDataFolder(), "config.yml"));
        }catch (IOException e){
            e.printStackTrace();
            plugin.getLogger().severe("Could not save storage file!");
        }
    }

    File dataFolder(){
        return plugin.getDataFolder();
    }

}
