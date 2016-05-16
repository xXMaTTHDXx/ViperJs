package com.viperpvp.viperjs.backend.req;

import com.viperpvp.viperjs.ViperJs;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/**
 * Created by Matt on 15/05/2016.
 */
public final class ViperEventExecutor<T extends Event> implements EventExecutor, Listener {

    private ViperJs plugin;
    private ViperEventCallback<T> callback;
    private Class<T> eventType;

    public ViperEventExecutor(ViperJs plugin, Class<T> eventType, ViperEventCallback<T> callback) {
        this.plugin = plugin;
        this.eventType = eventType;
        this.callback = callback;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if(eventType.isInstance(event)) {
            T t = eventType.cast(event);
            callback.callback(t);
        }
    }
}
