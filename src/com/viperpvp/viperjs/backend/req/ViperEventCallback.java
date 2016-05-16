package com.viperpvp.viperjs.backend.req;

import org.bukkit.event.Event;

/**
 * Created by Matt on 15/05/2016.
 */
public interface ViperEventCallback<T extends Event> {

    public void callback(T t);
}
