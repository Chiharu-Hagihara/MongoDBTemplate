package chiharu.hagihara.mongotemplate.paper.kotlin

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class MongoDBFunc(HOST: String, USER: String, PASS: String, PORT: Int, DATABASE: String, CUSTOM: String) {

    /**
     * Created by Chiharu-Hagihara
     * Reference by takatronix:MySQLFunc
     */

    lateinit var plugin: JavaPlugin
    var HOST = ""
    var USER = ""
    var PASS = ""
    var PORT = 0
    var DATABASE = ""
    var CUSTOM = ""
    private var con: MongoClient? = null

    init {
        this.HOST = HOST
        this.USER = USER
        this.PASS = PASS
        this.PORT = PORT
        this.DATABASE = DATABASE
        this.CUSTOM = CUSTOM
    }

    fun open(): MongoClient {
        try {
            // mongodb://user1:pwd1@host1/?authSource=db1
            val uri = MongoClientURI("mongodb://$USER:$PASS@$HOST:$PORT/$DATABASE$CUSTOM")
            con = MongoClient(uri)
            return con!!
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Could not connect to MongoDB server, error code: $e")
        }
        return con!!
    }

    fun checkConnection(): Boolean {
        return con != null
    }

    fun close(c: MongoClient?) {
        var c = c
        c = null
    }

    fun getCon(): MongoClient? {
        return con
    }

    fun setCon(con: MongoClient?) {
        this.con = con
    }
}