package com.viperpvp.viperjs.commands;

import com.viperpvp.viperjs.ViperJs;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * Created by Matt on 15/05/2016.
 */
public class ScriptCommand implements CommandExecutor {

    private ViperJs instance = ViperJs.get();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("viperjs")) {
            if (!sender.hasPermission("viperjs.use")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/viperpvp pull");
                sender.sendMessage(ChatColor.RED + "/viperpvp pull [script]");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("pull")) {
                    String scriptName = args[1];
                    File file = new File(instance.getDataFolder() + "/scripts", scriptName + ".js");

                    if (!file.exists()) {
                        sender.sendMessage(ChatColor.RED + "This script doesn't exist!");
                        return true;
                    } else {
                        instance.loadScript(scriptName);
                        sender.sendMessage(ChatColor.GREEN + "Reloaded script: " + scriptName);
                        return true;
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("pull")) {
                    instance.pullAll();
                    sender.sendMessage(ChatColor.GREEN + "Pulled and updated all scripts!");
                    return true;
                }
            }
        }
        return false;
    }
}
