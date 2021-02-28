package chiharu.hagihara.mongotemplate.paper;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MongoDBManager implements AutoCloseable {

    private JavaPlugin plugin;
    public String HOST;
    public String USER;
    public String PASS;
    public int PORT;
    public String DATABASE;
    public String CUSTOM;
    private boolean connected = false;
    private MongoCollection<Document> coll;
    private MongoClient con = null;
    private MongoDBFunc MongoDB;

    /**
     * Created by Chiharu-Hagihara
     * Reference by takatronix:MySQLManager
     */

    ////////////////////////////////
    //      Constructor
    ////////////////////////////////
    public MongoDBManager(JavaPlugin plugin, String coll) {
        this.plugin = plugin;

        loadConfig();

        this.connected = false;
        this.connected = Connect();
        this.coll = con.getDatabase(DATABASE).getCollection(coll);

        if(!this.connected) {
            this.plugin.getLogger().info("Unable to establish a MongoDB connection.");
        }
    }

    /////////////////////////////////
    //       Load YAML
    /////////////////////////////////
    public void loadConfig(){
        plugin.getLogger().info("MongoDB Config loading");

        plugin.reloadConfig();
        HOST = plugin.getConfig().getString("mongo.host");
        USER = plugin.getConfig().getString("mongo.user");
        PASS = plugin.getConfig().getString("mongo.pass");
        PORT = plugin.getConfig().getInt("mongo.port");
        CUSTOM = plugin.getConfig().getString("mongo.uri");
        DATABASE = plugin.getConfig().getString("mongo.db");

        plugin.getLogger().info("Config loaded");

    }

    ////////////////////////////////
    //       Connect
    ////////////////////////////////
    public Boolean Connect() {
        this.MongoDB = new MongoDBFunc(HOST, USER, PASS, PORT, DATABASE, CUSTOM);
        this.con = this.MongoDB.open();
        if(this.con == null){
            plugin.getLogger().info("failed to open MongoDB");
            return false;
        }

        try {
            this.connected = true;
            this.plugin.getLogger().info("Connected to the database.");
        } catch (Exception var6) {
            this.connected = false;
            this.plugin.getLogger().info("Could not connect to the database.");
        }

        this.MongoDB.close(this.con);
        return this.connected;
    }

    ////////////////////////////////
    //       InsertOne Query
    ////////////////////////////////
    public void queryInsertOne(Document doc) {
        coll.insertOne(doc);
    }

    ////////////////////////////////
    //       UpdateOne Query
    ////////////////////////////////
    public void queryUpdateOne(Document filter, String updateType, Document update) {
        Document updateSet = new Document();
        updateSet.append(updateType, update);
        coll.updateOne(filter, updateSet);
    }

    ////////////////////////////////
    //       DeleteOne Query
    ////////////////////////////////
    public void queryDelete(Document filter) {
        coll.deleteOne(filter);
    }

    ////////////////////////////////
    //       Find Query
    ////////////////////////////////
    public List<Document> queryFind(Document filter) {
        return coll.find(filter).into(new ArrayList<>());
    }

    ////////////////////////////////
    //       Count Query
    ////////////////////////////////
    public long queryCount(Document filter) {
        return coll.countDocuments(filter);
    }

    ////////////////////////////////
    //       Connection Close
    ////////////////////////////////
    @Override
    public void close(){

        try {
            this.con.close();
            this.MongoDB.close(this.con);

        } catch (Exception var4) {
        }

    }

    ////////////////////////////////
    //       Setup BlockingQueue
    ////////////////////////////////
    static LinkedBlockingQueue<Document> blockingQueue = new LinkedBlockingQueue<>();

    public static void setupBlockingQueue(JavaPlugin plugin, String coll) {
        new Thread(() -> {
            MongoDBManager mongo = new MongoDBManager(plugin, coll);
            try {
                while (true) {
                    Document take = blockingQueue.take();
                    mongo.queryInsertOne(take);
                }
            }catch (Exception e) {
            }
        }).start();
    }

    public static void executeQueue(Document query) {
        blockingQueue.add(query);
    }
}
