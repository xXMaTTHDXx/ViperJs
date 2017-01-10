package com.viperpvp.viperjs.listeners;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.viperpvp.viperjs.ViperJs;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Matt on 10/01/2017.
 */
public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        DBObject find = new BasicDBObject("_id", ViperJs.get().getConfig().getString("servername"));

        DBObject toReplace = new BasicDBObject("_id", ViperJs.get().getConfig().getString("servername"))
                .append("playerCount", Bukkit.getOnlinePlayers().size()).append("maxPlayers", Bukkit.getMaxPlayers());

        ViperJs.manager.getCollection("servers").update(find, toReplace);
    }
}
