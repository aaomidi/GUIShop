package com.aaomidi.dev.guishop.engine.objects;

import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.Caching;
import com.aaomidi.dev.guishop.engine.InventoryManager;
import com.aaomidi.dev.guishop.engine.Modules.MenuBehaviour;
import com.aaomidi.dev.guishop.utils.StringManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@AllArgsConstructor
public class GUICategory extends MenuBehaviour {
    @Getter
    private final String fqn;
    @Getter
    private final String name;
    @Getter
    private final int size;
    @Getter
    private final String icon;
    @Getter
    private final List<String> lore;
    @Getter
    @Setter
    private List<GUIStock> stock;

    public ItemStack toItemStack() {
        try {
            ItemStack item = GUIShop.getEss().getItemDb().get(icon);
            item.setAmount(1);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(StringManager.colorize(name));
            itemMeta.setLore(StringManager.colorize(lore, null));
            item.setItemMeta(itemMeta);
            return item;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onClose(Player player) {

    }

    @Override
    public void onLeftClick(Player player) {
        player.closeInventory();
        player.openInventory(InventoryManager.createStockInventory(this));
        Caching.getOpenInventoryMap().put(player, this);
    }

    @Override
    public void onRightClick(Player player) {
        this.onLeftClick(player);
    }

}
