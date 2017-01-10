package com.viperpvp.viperjs.database;

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.viperpvp.viperjs.ViperJs;
import org.bukkit.Bukkit;

import java.util.Collections;

/**
 * Created by Matt on 03/01/2017.
 */
public class MongoDatabaseManager {

    private DB db;
    private Mongo mongo;

    private BasicDBObject thisServer;

    public void connect(String ip, int port) {
        MongoCredential credential = MongoCredential.createCredential("user", "db", "pass".toCharArray());
        mongo = new MongoClient(new ServerAddress(ip, port), Collections.singletonList(credential));
        db = mongo.getDB("Velocity");

        DBCollection collection = db.getCollection("servers");

        DBObject query = new BasicDBObject("_id", ViperJs.get().getConfig().getString("servername"));

        DBObject object = new BasicDBObject("_id", ViperJs.get().getConfig().getString("servername"))
                .append("playerCount", Bukkit.getOnlinePlayers().size()).append("maxPlayers", Bukkit.getMaxPlayers());

        if (collection.find(query).one() != null) {
            collection.update(query, object);
        }
        else {
            collection.insert(object);
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
