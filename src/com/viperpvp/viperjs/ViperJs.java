package com.viperpvp.viperjs;

import com.viperpvp.viperjs.backend.req.ViperCommandCallback;
import com.viperpvp.viperjs.backend.req.ViperCommandExecutor;
import com.viperpvp.viperjs.backend.req.ViperEventCallback;
import com.viperpvp.viperjs.backend.req.ViperEventExecutor;
import com.viperpvp.viperjs.backend.unsafe.CommandRegistration;
import com.viperpvp.viperjs.commands.ScriptCommand;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.script.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Matt on 15/05/2016.
 */
public class ViperJs extends JavaPlugin {

    private static ViperJs instance;

    private ScriptEngine nashorn = null;
    private Invocable invocable;
    private List<ViperEventExecutor> eventExecutors = new ArrayList<>();

    private Bindings bindings;

    private int loadedScripts = 0;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("viperjs").setExecutor(new ScriptCommand());

        /**
         Nashorn stuff
         */
        nashorn = new ScriptEngineManager().getEngineByName("nashorn");
        invocable = (Invocable) nashorn;

        getConfig().options().copyDefaults(true);
        saveConfig();

        try {
            init();
        } catch (FileNotFoundException | ScriptException e) {
            e.printStackTrace();
        }
    }

    public void loadScript(String scriptName) {
        File file = new File(getDataFolder() + "/scripts", scriptName + ".js");

        if (!file.exists()) {
            System.out.println("Cannot load script: " + file.getName() + " as it does not exist!");
            return;
        }

        try {
            nashorn.eval(new FileReader(file), bindings);
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void pullAll() {
        File scriptDir = new File(getDataFolder() + "/scripts");
        if (!scriptDir.exists()) {
            scriptDir.mkdir();
        } else {
            for (File file : scriptDir.listFiles()) {
                if (!file.getName().endsWith(".js")) {
                    System.out.println("Skipping file: " + file.getName() + " as it is not a JavaScript file!");
                } else {
                    try {
                        nashorn.eval(new FileReader(file), bindings);
                    } catch (ScriptException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    loadedScripts++;
                }
            }
        }
        System.out.println("Scripts loaded: " + loadedScripts + "!");
    }

    public void init() throws FileNotFoundException, ScriptException {
        bindings = nashorn.createBindings();
        bindings.put("viper", this);
        bindings.put("server", getServer());

        File scriptDir = new File(getDataFolder() + "/scripts");
        if (!scriptDir.exists()) {
            scriptDir.mkdir();
        } else {
            for (File file : scriptDir.listFiles()) {
                if (!file.getName().endsWith(".js")) {
                    System.out.println("Skipping file: " + file.getName() + " as it is not a JavaScript file!");
                } else {
                    nashorn.eval(new FileReader(file), bindings);
                    loadedScripts++;
                }
            }
        }
        System.out.println("Scripts loaded: " + loadedScripts + "!");
    }

    private Object invokeLibFunction(String functionName, Object... args) {
        try {
            return invocable.invokeFunction(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, String function) {
        registerEvent(eventClass, EventPriority.NORMAL, function);
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, EventPriority normal, String function) {
        registerEvent(eventClass, normal, true, function);
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, ViperEventCallback<T> callback) {
        registerEvent(eventClass, EventPriority.NORMAL, callback);
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, EventPriority priority, ViperEventCallback callback) {
        registerEvent(eventClass, priority, true, callback);
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, ViperEventCallback<T> callback) {
        ViperEventExecutor executor = new ViperEventExecutor(this, eventClass, callback);
        eventExecutors.add(executor);
        getServer().getPluginManager().registerEvent(eventClass, executor, priority, executor, this, ignoreCancelled);
    }

    public <T extends Event> void registerEvent(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, final String function) {
        registerEvent(eventClass, priority, ignoreCancelled, t -> {
            try {
                invokeLibFunction(function, t);
            } catch (RuntimeException ex) {
                getLogger().log(Level.WARNING, ex.getMessage());
            }
        });
    }

    public BukkitTask runSyncTimer(Runnable run, int delay, int ticks) {
        return Bukkit.getServer().getScheduler().runTaskTimer(this, run, delay, ticks);
    }

    public BukkitTask runAsyncTimer(Runnable run, int delay, int ticks) {
        return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, run, delay, ticks);
    }

    public BukkitTask runTaskLater(Runnable run, int later) {
        return Bukkit.getServer().getScheduler().runTaskLater(this, run, later);
    }

    public BukkitTask runAsyncTaskLater(Runnable run, int later) {
        return Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this, run, later);
    }

    public PluginCommand registerCommand(String cmd, String function) {
        return registerCommand(cmd, (sender, command, args) -> {
            try {
                return Boolean.TRUE == invokeLibFunction(function, sender, command, args);
            } catch (RuntimeException ex) {
                getLogger().log(Level.WARNING, ex.getMessage());
            }
            return false;
        });
    }

    public PluginCommand registerCommand(String cmd, ViperCommandCallback callback) {
        try {
            final PluginCommand command = CommandRegistration.registerCommand(this, cmd);
            if (command != null) {
                command.setExecutor(new ViperCommandExecutor(this, callback));
                return command;
            }
        } catch (UnsupportedOperationException ex) {
            getLogger().log(Level.WARNING, null, ex);
        }
        return null;
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static ViperJs get() {
        return instance;
    }
}
