package ml.codeboy.engine.exampleGames.rpg.GameObjects.characters;

import ml.codeboy.engine.animation.Animation;
import ml.codeboy.engine.animation.CharacterAnimator;
import ml.codeboy.engine.animation.Direction;
import ml.codeboy.engine.animation.State;

public class PlayerAnimator extends CharacterAnimator {
    private static PlayerAnimator instance;
    public static PlayerAnimator getInstance() {
        return instance!=null?instance:(instance=new PlayerAnimator());
    }

    private PlayerAnimator() {
        new Thread(this::init).start();
    }

    private void init(){
        addAnimation(Animation.fromSpriteSheet("knight-idle.png",1), State.IDLE, Direction.DEFAULT);
        addAnimation(Animation.fromSpriteSheet("knight-walking.png",9), State.WALK, Direction.DEFAULT);
        addAnimation(Animation.fromSpriteSheet("knight-death.png",6), State.DEATH, Direction.DEFAULT);
    }
}
