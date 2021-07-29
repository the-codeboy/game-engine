package ml.codeboy.engine.exampleGames.rpg.Items;

import ml.codeboy.engine.Game;

public class ItemStack {

    private final Game game;
    private Material type;
    private int count;

    public ItemStack(Material type, Game game) {
        this(type, game, 1);
    }

    public ItemStack(Material type, Game game, int count) {
        this.type = type;
        this.game = game;
        this.count = count;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addItems(int count) {
        this.count += count;
    }

    public void removeItems(int count) {
        this.count -= count;
    }

    public int getMaxCount() {
        return type.maxStack;
    }

    public Game getGame() {
        return game;
    }

    public enum Material {
        SWORD("sword.png");

        public final String sprite;
        public final int maxStack;

        Material(String sprite) {
            this(sprite, 64);
        }

        Material(String sprite, int maxStack) {
            this.sprite = sprite;
            this.maxStack = maxStack;
        }
    }
}

