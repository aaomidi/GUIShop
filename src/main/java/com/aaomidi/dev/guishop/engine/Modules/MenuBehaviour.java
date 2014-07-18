package com.aaomidi.dev.guishop.engine.Modules;

import org.bukkit.entity.Player;


public abstract class MenuBehaviour {
    public abstract void onClose(Player player);

    public abstract void onLeftClick(Player player);

    public abstract void onRightClick(Player player);

}
