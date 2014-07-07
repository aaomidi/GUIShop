package com.aaomidi.dev.guishop.engine;

import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.objects.GUICategory;
import com.aaomidi.dev.guishop.engine.objects.GUIStock;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.WeakHashMap;

public class Caching {
    @Getter
    private static WeakHashMap<Player, GUICategory> openInventoryMap;
    @Getter
    private static WeakHashMap<Player, GUIStock> buyInventoryMap;
    @Getter
    private static WeakHashMap<Player, GUIStock> sellInventoryMap;
    private final GUIShop _plugin;

    public Caching(GUIShop plugin) {
        _plugin = plugin;
        openInventoryMap = new WeakHashMap<>();
        buyInventoryMap = new WeakHashMap<>();
        sellInventoryMap = new WeakHashMap<>();
    }
}
