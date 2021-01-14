package ml.codeboy.engine;


public class Test extends Game
{
    public Test()
    {
        super("Test");
        GameObject object=new GameObject(this,Sprite.SpriteType.Image);
        object.setPosition(getMiddleOfWindow());
        object.setAnimation(Animation.fromGif("gif.gif").setDelayBetweenFrames(0.2f));
        object.setWidthAndHeight(getWidth(),getHeight());
    }
}
