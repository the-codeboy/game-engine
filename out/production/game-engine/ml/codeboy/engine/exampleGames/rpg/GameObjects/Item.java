package ml.codeboy.engine.exampleGames.rpg.GameObjects;

import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.exampleGames.rpg.Items.Inventory;
import ml.codeboy.engine.exampleGames.rpg.Items.ItemStack;

public class Item extends GameObject {

    private final ItemStack itemStack;

    public Item(ItemStack stack) {
        super(stack.getType().sprite, stack.getGame());
        setSize(10);
        itemStack = stack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }


    public void collect(Inventory inventory) {
        boolean fits = inventory.addItem(getItemStack());
        if (fits)
            deleteNextTick();
    }
}
