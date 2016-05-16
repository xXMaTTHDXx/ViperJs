package com.viperpvp.viperjs.backend.req;

import com.viperpvp.viperjs.ViperJs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

/**
 * Created by Matt on 15/05/2016.
 */
public class ViperCommandExecutor implements CommandExecutor {

    private final ViperJs plugin;
    private final ViperCommandCallback callback;

    public ViperCommandExecutor(ViperJs plugin, ViperCommandCallback callback) {
        this.plugin = plugin;
        this.callback = callback;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        try {
            return callback.callback(cs, string, strings);
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING, ex.getMessage());
            return false;
        }
    }
}
