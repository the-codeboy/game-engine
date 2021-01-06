package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Player;

public abstract class Upgrade {
    private int cost;
    protected Player player=Player.getPlayer();

    public static void clear(){
        upgrades=0;
    }

    protected int getLevel() {
        return level;
    }

    private int level;
    private static int upgrades=0;
    Button button;
    String name;

    protected Upgrade(int cost,String name, Game game) {
        this.cost = cost;
        upgrades++;
        this.name=name;
        button=new Button(game,name+" cost: "+cost+" current level "+level,this::tryBuy);
        button.setPosition(game.getFrame().getWidth()-150,game.getFrame().getHeight()-70*upgrades);
        button.setWidthAndHeight(200,50);
    }

    private void tryBuy(){
        if(Player.getPlayer()!=null)
        buy(Player.getPlayer());
    }

    public void buy(Player player){
        if(player.getCoins()>=cost){
            player.setCoins(player.getCoins()-cost);
            level++;
            button.setText(name+" cost: "+cost+" current level "+level);
            onBuy();
        }
    }

    protected abstract void onBuy();
}
