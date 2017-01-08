package com.viperpvp.viperjs.backend.script;

import com.google.gson.JsonObject;
import com.viperpvp.viperjs.ViperJs;
import com.viperpvp.viperjs.backend.unsafe.CommandRegistration;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import javax.script.CompiledScript;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Matt on 29/12/2016.
 */
public class ScriptInstance {

    private CompiledScript script;
    private List<BukkitTask> tasks;
    private List<Listener> events;
    private List<PluginCommand> commands;
    private ScriptManager scriptManager;

    private HashMap<String, JsonObject> jsonVariables = new HashMap<>();

    public ScriptInstance(CompiledScript script, List<BukkitTask> tasks, List<Listener> events, List<PluginCommand> commands) {
        this.script = script;
        this.tasks = tasks;
        this.events = events;
        this.commands = commands;
        this.scriptManager = new ScriptManager(this);
    }

    public ScriptInstance(CompiledScript script) {
        this.script = script;
        this.tasks = new ArrayList<>();
        this.events = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.scriptManager = new ScriptManager(this);
    }

    private void unregisterTasks() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }

    private void unregisterEvents() {
        for (Listener e : events) {
            HandlerList.unregisterAll(e);
        }
    }

    private void unregisterCommands() {
        CommandRegistration.unregisterPluginCommands(ViperJs.get().getServer(),  new HashSet<>(commands));
    }

    public void disableScriptFile() {
        unregisterTasks();
        unregisterEvents();
        unregisterCommands();
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public List<BukkitTask> getTasks() {
        return tasks;
    }

    public List<Listener> getEvents() {
        return events;
    }

    public List<PluginCommand> getCommands() {
        return commands;
    }

    public HashMap<String, JsonObject> getJsonVariables() {
        return jsonVariables;
    }
}
