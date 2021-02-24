package ml.codeboy.engine.exampleGames.rpg.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.Sprites;
import ml.codeboy.engine.animation.Animation;
import ml.codeboy.engine.animation.State;
import ml.codeboy.engine.exampleGames.rpg.Dialogs.Dialog;
import ml.codeboy.engine.exampleGames.rpg.Dialogs.DialogOption;
import ml.codeboy.engine.exampleGames.rpg.Dialogs.DialogScreen;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.characters.Character;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.characters.Player;

import java.util.Collections;

public class NPC extends Character {

    private Dialog dialog;

    public NPC(String name, Game game) {
        super(name, game);
        listenForCollision(Player.class);
        this.dialog=createDialog();
        addAnimation(new Animation(Collections.singletonList(Sprites.getSprite(name))), State.IDLE);
    }

    @Override
    protected void onCollision(GameObject other) {
        if(other instanceof Player)
            openDialog((Player) other);
    }

    protected Dialog createDialog(){
        return new Dialog(new DialogScreen("Hi I am Jonny the trader",
                new DialogOption("...",DialogOption.nextScreen)
                ,new DialogOption("quit",DialogOption.quit)),
                new DialogScreen("Do you want to buy something?",
                        new DialogOption("yes",DialogOption.nextScreen),
                        new DialogOption("no",d->d.setScreen(
                                new DialogScreen("Well too bad",new DialogOption("quit",DialogOption.quit)))),
                        new DialogOption("quit",DialogOption.quit)),
                new DialogScreen("This is just a test I can´t really sell you anything",
                        new DialogOption("quit",Dialog::close)));
    }

    public void openDialog(Player player){
        if(dialog!=null)
            player.getGame().openInteraction(dialog);
    }
}
