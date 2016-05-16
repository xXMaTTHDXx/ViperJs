package com.viperpvp.viperjs.backend.req;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Matt on 16/05/2016.
 */
public interface ViperTaskCallback<T extends BukkitRunnable> {

    public void callback(T t);
}
