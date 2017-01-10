package com.viperpvp.viperjs.database;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.viperpvp.viperjs.ViperJs;
import org.bukkit.entity.Player;

/**
 * Created by Matt on 08/01/2017.
 */
public class ServerUtil {


    /*
        DATABASE
     */
    public int getCurrentPlayers(String serverName) {
        DBObject query = new BasicDBObject("_id", ViperJs.get().getConfig().getString("servername"));


        DBCursor cursor = ViperJs.manager.getCollection("servers").find(query);
        return (int) cursor.one().get("playerCount");
    }

    public int getMaxPlayers(String serverName) {
        DBObject query = new BasicDBObject("_id", ViperJs.get().getConfig().getString("servername"));


        DBCursor cursor = ViperJs.manager.getCollection("servers").find(query);
        return (int) cursor.one().get("maxPlayers");
    }

    /*
        UTILS
     */

    public void send(Player player, String server) {
        ByteArrayDataOutput bo = ByteStreams.newDataOutput();
        bo.writeUTF("Connect");
        bo.writeUTF(server);

        player.sendPluginMessage(ViperJs.get(), "BungeeCord", bo.toByteArray());
    }

    public void kickPlayer(Player sender, String toKick, String reason) {
        ByteArrayDataOutput bo = ByteStreams.newDataOutput();
        bo.writeUTF("KickPlayer");
        bo.writeUTF(toKick);
        bo.writeUTF(reason);

        sender.sendPluginMessage(ViperJs.get(), "BungeeCord", bo.toByteArray());
    }
}
