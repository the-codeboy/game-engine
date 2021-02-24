package ml.codeboy.engine.exampleGames.rpg.GameObjects.characters;

import ml.codeboy.engine.animation.Animation;
import ml.codeboy.engine.animation.CharacterAnimator;
import ml.codeboy.engine.Game;
import ml.codeboy.engine.animation.Direction;
import ml.codeboy.engine.animation.State;
import ml.codeboy.engine.Damageable;
import ml.codeboy.engine.events.DestroyEvent;
import ml.codeboy.engine.exampleGames.rpg.Items.Inventory;

import java.util.Collections;

public class Character extends Damageable {
    //<editor-fold desc="constructors">
    public Character(String name, Game game) {
        super(name, game);
    }

    public Character(String name, String path, Game game) {
        super(name, path, game);
    }

    public Character(String name, int x, int y, Game game) {
        super(name, x, y, game);
    }

    public Character(String name, String path, int x, int y, Game game) {
        super(name, path, x, y, game);
    }

    public Character(String name, int x, int y, int width, int height, Game game) {
        super(name, x, y, width, height, game);
    }

    public Character(String name, String path, int x, int y, int width, int height, Game game) {
        super(name, path, x, y, width, height, game);
    }

    @Override
    protected void init(Game game) {
        super.init(game);
        animator=new CharacterAnimator();
        animator.addAnimation(new Animation(Collections.singletonList(getImage())),State.IDLE);
    }

    //</editor-fold>

    //<editor-fold desc="Animation">
    private CharacterAnimator animator;

    protected void setAnimator(CharacterAnimator animator){
        this.animator=animator;
        setAnimation(animator.getAnimation(state,direction));
    }

    private double lastX,lastY;
    private Direction direction=Direction.RIGHT,lastDirection=Direction.DEFAULT;

    private State state=State.IDLE,lastState=State.IDLE;

    protected void addAnimation(Animation animation,State state,Direction direction){
        animator.addAnimation(animation, state, direction);
    }
    protected void addAnimation(Animation animation,State state){
        animator.addAnimation(animation, state);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    protected void onDestruction(DestroyEvent event) {
        event.setCanceled(true);
        setState(State.DEATH);
        Animation anim=animator.getAnimation(State.DEATH,direction);
        float time=anim.getDelayBetweenFrames()*anim.getFramesLeft();
        game.getScheduler().scheduleTask(this::deleteNextTick,time);
    }

    /**
     * @deprecated don´t override or call super if you have to
     */
    @Override
    @Deprecated
    protected void lateTick() {
        if(animator==null)
            animator=new CharacterAnimator();
        double x=getXDouble(),y=getYDouble();
        if(x>lastX)
            direction= Direction.RIGHT;
        else if(x<lastX)
            direction= Direction.LEFT;
        else if(y>lastY)
            direction= Direction.DOWN;
        else if(y<lastY)
            direction= Direction.UP;

        if(lastDirection!=direction||lastState!=state)
            setAnimation(animator.getAnimation(state,direction));

        lastDirection=direction;
        lastState=state;
        lastX=x;
        lastY=y;
    }
    //</editor-fold>

    public boolean isAlive(){
        return getLives()>0;
    }

    private Inventory inventory=new Inventory();

    public Inventory getInventory() {
        return inventory;
    }
}
