package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Player;

public abstract class Upgrade {
    private static int upgrades = 0;
    private static BuyAtOnce buyAtOnce = BuyAtOnce.One;
    private final int cost;
    protected Player player = Player.getPlayer();
    Button button;
    String name;
    private int level;

    public Upgrade(int cost, String name, Game game) {
        this.cost = cost;
        upgrades++;
        this.name = name;
        button = new Button(name + " cost: " + cost + " current level " + level, this::tryBuy);
        button.setPosition(game.getFrame().getWidth() - 150, game.getFrame().getHeight() - 70 * upgrades);
        button.setWidthAndHeight(200, 50);
    }

    public static void clear() {
        upgrades = 0;
    }

    public static BuyAtOnce getBuyAtOnce() {
        return buyAtOnce;
    }

    public static void cycleBuyAtOnce() {
        buyAtOnce = buyAtOnce.getNext();
    }

    protected int getLevel() {
        return level;
    }

    private void tryBuy() {
        for (int i = 0; i < buyAtOnce.value; i++) {
            if (Player.getPlayer() != null)
                if (!buy(Player.getPlayer()))
                    return;

        }
    }

    public boolean buy(Player player) {
        if (player.getCoins() >= cost) {
            player.setCoins(player.getCoins() - cost);
            level++;
            button.setText(name + " cost: " + cost + " current level " + level);
            onBuy();
            return true;
        }
        return false;
    }

    protected abstract void onBuy();

    public enum BuyAtOnce {
        One(1), Two(2), Five(5), Ten(10), Fifty(50), Infinite(Integer.MAX_VALUE);

        public final int value;

        BuyAtOnce(int value) {
            this.value = value;
        }

        public BuyAtOnce getNext() {
            return ordinal() == values().length - 1 ? values()[0] : values()[ordinal() + 1];
        }
    }
}
