package com.aaomidi.dev.guishop.engine.modules;

import org.bukkit.entity.Player;

/**
 * Created by Amir on 7/2/2014.
 */
public abstract class MenuBehaviour {
    public abstract void onClose(Player player);

    public abstract void onLeftClick(Player player);

    public abstract void onRightClick(Player player);

}
