package com.viperpvp.viperjs.backend.req;

import org.bukkit.command.CommandSender;

/**
 * Created by Matt on 15/05/2016.
 */
public interface ViperCommandCallback {

    public boolean callback(CommandSender sender, String command, String[] args);
}
