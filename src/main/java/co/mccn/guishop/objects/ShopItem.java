package co.mccn.guishop.objects;

import org.bukkit.inventory.ItemStack;


public class ShopItem {
    private final ItemStack item;
    private double buyPrice;
    private double sellPrice;
    private int position;

    public ShopItem(ItemStack item, double buyPrice, double sellPrice, int position) {
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.position = position;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(float sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
