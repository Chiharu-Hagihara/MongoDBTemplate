package chiharu.hagihara.mongotemplate.paper.kotlin

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class MongoDBManager(plugin: JavaPlugin, coll: String) : AutoCloseable {

    lateinit var plugin: JavaPlugin
    var HOST = ""
    var USER = ""
    var PASS = ""
    var PORT = 0
    var DATABASE = ""
    var CUSTOM = ""
    var connected = false
    lateinit var coll: MongoCollection<Document>
    lateinit var con: MongoClient
    lateinit var MongoDB: MongoDBFunc


    /**
     * Created by Chiharu-Hagihara
     * Reference by takatronix:MySQLManager
     */

    ////////////////////////////////
    //      Constructor
    ////////////////////////////////
    init {
        this.plugin = plugin

        loadConfig()

        connected = false
        connected = Connect()
        this.coll = con.getDatabase(DATABASE).getCollection(coll)

        if (!connected) {
            this.plugin.logger.info("Unable to establish a MongoDB connection.")
        }
    }

    /////////////////////////////////
    //       Load YAML
    /////////////////////////////////
    fun loadConfig() {
        plugin.logger.info("MongoDB Config loading")

        plugin.reloadConfig()
        HOST = plugin.config.getString("mongo.host")!!
        USER = plugin.config.getString("mongo.user")!!
        PASS = plugin.config.getString("mongo.pass")!!
        PORT = plugin.config.getInt("mongo.port")
        CUSTOM = plugin.config.getString("mongo.uri")!!
        DATABASE = plugin.config.getString("mongo.db")!!

        plugin.logger.info("Config loaded")
    }

    ////////////////////////////////
    //       Connect
    ////////////////////////////////
    fun Connect(): Boolean {
        this.MongoDB = MongoDBFunc(HOST, USER, PASS, PORT, DATABASE, CUSTOM)
        this.con = this.MongoDB.open()

        try {
            connected = true
            plugin.logger.info("Connected to the database.")
        } catch (e: Exception) {
            connected = false
            plugin.logger.info("Could not connect to the database.")
        }

        MongoDB.close(con)
        return connected
    }

    ////////////////////////////////
    //       InsertOne Query
    ////////////////////////////////
    fun queryInsertOne(doc: Document?) {
        coll.insertOne(doc)
    }

    ////////////////////////////////
    //       UpdateOne Query
    ////////////////////////////////
    fun queryUpdateOne(filter: Document?, updateType: String?, update: Document?) {
        val updateSet = Document()
        updateSet.append(updateType, update)
        coll.updateOne(filter, updateSet)
    }

    ////////////////////////////////
    //       DeleteOne Query
    ////////////////////////////////
    fun queryDelete(filter: Document?) {
        coll.deleteOne(filter)
    }

    ////////////////////////////////
    //       Find Query
    ////////////////////////////////
    fun queryFind(filter: Document?): List<Document?>? {
        return coll.find(filter).into<ArrayList<Document?>>(ArrayList())
    }

    ////////////////////////////////
    //       Count Query
    ////////////////////////////////
    fun queryCount(filter: Document?): Long {
        return coll.countDocuments(filter)
    }

    ////////////////////////////////
    //       Connection Close
    ////////////////////////////////
    override fun close() {
        try {
            con.close()
            MongoDB.close(con)
        } catch (var4: java.lang.Exception) {
        }
    }
}