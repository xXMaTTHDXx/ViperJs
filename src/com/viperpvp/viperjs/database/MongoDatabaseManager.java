package com.viperpvp.viperjs.database;

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.viperpvp.viperjs.ViperJs;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Matt on 03/01/2017.
 */
public class MongoDatabaseManager {

    private DB db;
    private Mongo mongo;

    private BasicDBObject thisServer;

    public void connect(String ip, int port) {
        mongo = new MongoClient(ip, port);
        thisServer = new BasicDBObject("name", ViperJs.get().getConfig().getString("servername"));
        thisServer.put("max", Bukkit.getMaxPlayers());
        new BukkitRunnable() {
            public void run() {
                thisServer.put("count", Bukkit.getOnlinePlayers());
            }
        }.runTaskTimerAsynchronously(ViperJs.get(), 0L, 20*5L);

        init();
    }

    public void init() {
        db = mongo.getDB("Velocity");

        DBCollection servers = db.getCollection("servers");
        if (servers.find(thisServer).getQuery() == null) {
            servers.insert(thisServer);
        }
        else {

        }
    }

    public Mongo getMongo() {
        return mongo;
    }

    public DBCollection getCollection(String col) {
        return db.getCollection(col);
    }

    public DB getDb() {
        return db;
    }

    public void insert(DBCollection col, String json) {
        col.insert((DBObject[]) JSON.parse(json));
    }

    public String find(DBCollection col, String toFind) {
        return JSON.serialize(col.find((DBObject) JSON.parse(toFind)));
    }
}
