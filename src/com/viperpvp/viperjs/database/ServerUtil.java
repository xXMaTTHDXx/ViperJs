package com.viperpvp.viperjs.database;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
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
        DBCursor cursor = ViperJs.manager.getMongo().getDB("Velocity").getCollection("servers").find(new BasicDBObject("id", serverName));
        return (int) cursor.getQuery().get("count");
    }

    public int getMaxPlayers(String serverName) {
        DBCursor cursor = ViperJs.manager.getMongo().getDB("Velocity").getCollection("servers").find(new BasicDBObject("id", serverName));
        return (int) cursor.getQuery().get("max");
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
