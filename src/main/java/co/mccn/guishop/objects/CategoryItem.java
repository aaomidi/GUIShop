package co.mccn.guishop.objects;

import org.bukkit.inventory.ItemStack;

/**
 * Created with IntelliJ IDEA.
 * User: Amir
 * Date: 1/29/14
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryItem {
    private final ItemStack item;
    private String name;
    private int size;
    private String description;
    private int position;

    public CategoryItem(String name, ItemStack item, int size, String description, int position) {
        this.name = name;
        this.item = item;
        this.size = size;
        this.description = description;
        this.position = position;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
