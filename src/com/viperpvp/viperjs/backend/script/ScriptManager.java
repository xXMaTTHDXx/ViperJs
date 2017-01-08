package com.viperpvp.viperjs.backend.script;

import com.viperpvp.viperjs.ViperJs;
import com.viperpvp.viperjs.backend.req.ViperCommandCallback;
import com.viperpvp.viperjs.backend.req.ViperCommandExecutor;
import com.viperpvp.viperjs.backend.req.ViperEventCallback;
import com.viperpvp.viperjs.backend.req.ViperEventExecutor;
import com.viperpvp.viperjs.backend.unsafe.CommandRegistration;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

/**
 * Created by Matt on 30/12/2016.
 */
public class ScriptManager {

    private ScriptInstance owner;

    public ScriptManager(ScriptInstance owner) {
        this.owner = owner;
    }

    /*
        COMMANDS
     */

    public PluginCommand registerCommand(String cmd, String function) {
        return registerCommand(cmd, (sender, command, args) -> {
            try {
                return Boolean.TRUE == ViperJs.get().invokeLibFunction(function, sender, command, args);
            } catch (RuntimeException ex) {
                ViperJs.get().getLogger().log(Level.WARNING, ex.getMessage());
            }
            return false;
        });
    }

    public PluginCommand registerCommand(String cmd, ViperCommandCallback callback) {
        try {
            final PluginCommand command = CommandRegistration.registerCommand(ViperJs.get(), cmd);
            if (command != null) {
                command.setExecutor(new ViperCommandExecutor(ViperJs.get(), callback));
                this.owner.getCommands().add(command);
                return command;
            }
        } catch (UnsupportedOperationException ex) {
            ViperJs.get().getLogger().log(Level.WARNING, null, ex);
        }
        return null;
    }

    /*
        EVENTS
     */

    public <T extends Event> void registerEvent(String className, String function) {
        registerEvent(className, EventPriority.NORMAL, function);
    }

    public <T extends Event> void registerEvent(String className, EventPriority normal, String function) {

        Class eventClass = null;

        try {
            eventClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        registerEvent(eventClass, normal, true, function);
    }

    /*public <T extends Event> void registerEvent(Class<T> eventClass, ViperEventCallback<T> callback) {
        registerEvent(eventClass, EventPriority.NORMAL, callback);
    }*/

    public <T extends Event> void registerEvent(String className, ViperEventCallback<T> callback) {
        registerEvent(className, EventPriority.NORMAL, callback);
    }


    public <T extends Event> void registerEvent(String className, EventPriority priority, ViperEventCallback callback) {
        Class eventClass = null;

        try {
            eventClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        registerEvent(eventClass, priority, true, callback);
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, ViperEventCallback<T> callback) {


        ViperEventExecutor<T> executor = new ViperEventExecutor<>(ViperJs.get(), eventClass, callback);
        owner.getEvents().add(executor);
        ViperJs.get().getServer().getPluginManager().registerEvent(eventClass, executor, priority, executor, ViperJs.get(), ignoreCancelled);
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, final String function) {
        registerEvent(eventClass, priority, ignoreCancelled, t -> {
            try {
                ViperJs.get().invokeLibFunction(function, t);
            } catch (RuntimeException ex) {
                ViperJs.get().getLogger().log(Level.WARNING, ex.getMessage());
            }
        });
    }

    /*
        SCHEDULES
     */

    public BukkitTask runSyncTimer(Runnable run, int delay, int ticks) {
        BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(ViperJs.get(), run, delay, ticks);
        owner.getTasks().add(task);
        return task;
    }

    public BukkitTask runAsyncTimer(Runnable run, int delay, int ticks) {
        BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(ViperJs.get(), run, delay, ticks);
        owner.getTasks().add(task);
        return task;
    }

    public BukkitTask runTaskLater(Runnable run, int later) {
        BukkitTask task = Bukkit.getServer().getScheduler().runTaskLater(ViperJs.get(), run, later);
        owner.getTasks().add(task);
        return task;
    }

    public BukkitTask runAsyncTaskLater(Runnable run, int later) {
        BukkitTask task = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(ViperJs.get(), run, later);
        owner.getTasks().add(task);
        return task;
    }
}
