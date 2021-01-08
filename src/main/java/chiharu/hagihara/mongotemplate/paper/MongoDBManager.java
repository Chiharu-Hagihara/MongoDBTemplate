package chiharu.hagihara.mongotemplate.paper;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MongoDBManager implements AutoCloseable {

    private JavaPlugin plugin;
    public String HOST;
    public String USER;
    public String PASS;
    public int PORT;
    public String DATABASE;
    public String customUri;
    private boolean connected = false;
    private String conURI;
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
        loadConfig();
        this.plugin = plugin;
        this.coll = con.getDatabase(DATABASE).getCollection(coll);
        this.connected = false;
        this.connected = Connect(conURI);

        if(!this.connected) {
            plugin.getLogger().info("Unable to establish a MySQL connection.");
        }
    }

    /////////////////////////////////
    //       Load YAML
    /////////////////////////////////
    public void loadConfig(){
        plugin.getLogger().info("MYSQL Config loading");

        plugin.reloadConfig();
        HOST = plugin.getConfig().getString("mongo.host");
        USER = plugin.getConfig().getString("mongo.user");
        PASS = plugin.getConfig().getString("mongo.pass");
        PORT = plugin.getConfig().getInt("mongo.port");
        DATABASE = plugin.getConfig().getString("mongo.db");

        plugin.getLogger().info("Config loaded");

    }

    ////////////////////////////////
    //       Connect
    ////////////////////////////////
    public Boolean Connect(String conURI) {
        this.conURI = conURI;
        this.MongoDB = new MongoDBFunc(HOST, USER, PASS, PORT, DATABASE);
        this.con = this.MongoDB.open();
        if(this.con == null){
            plugin.getLogger().info("failed to open MYSQL");
            return false;
        }

        try {
            this.connected = true;
            this.plugin.getLogger().info("Connected to the database.");
        } catch (MongoException var6) {
            this.connected = false;
            this.plugin.getLogger().info("Could not connect to the database.");
        }

        this.MongoDB.close(this.con);
        return this.connected;
    }

    ////////////////////////////////
    //       InsertOne Query
    ////////////////////////////////
    public void queryInsertOne(String doc) {
        coll.insertOne(Document.parse(doc));
        plugin.getLogger().info("Insert: " + doc);
    }

    ////////////////////////////////
    //       UpdateOne Query
    ////////////////////////////////
    public void queryUpdateOne(String filterKey, String filterValue, String updateKey, String updateValue) {
        Document filterdoc = new Document();
        Document updatedoc = new Document();
        Document update = new Document();
        filterdoc.append(filterKey, filterValue);
        updatedoc.append(updateKey, updateValue);
        update.append("$set", updatedoc);

        coll.updateOne(filterdoc, update);
    }

    ////////////////////////////////
    //       DeleteOne Query
    ////////////////////////////////
    public void queryDelete(String filterKey, String filterValue) {
        coll.deleteOne(Filters.eq(filterKey, filterValue));
    }

    ////////////////////////////////
    //       Find Query
    ////////////////////////////////
    public List<Document> queryFind(String key, String value) {
        BasicDBObject query = new BasicDBObject(key, value);
        MongoCursor<Document> cursor = coll.find(query).cursor();
        List<Document> result = new ArrayList<>();
        try {
            while(cursor.hasNext()) {
                result.add(cursor.next());
            }
        }catch(Exception e) {
        }
        return result;
    }

    ////////////////////////////////
    //       Count Query
    ////////////////////////////////
    public long queryCount() {
        return coll.countDocuments();
    }

    ////////////////////////////////
    //       Connection Close
    ////////////////////////////////
    @Override
    public void close(){

        try {
            this.con.close();
            this.MongoDB.close(this.con);

        } catch (MongoException var4) {
        }

    }
}
