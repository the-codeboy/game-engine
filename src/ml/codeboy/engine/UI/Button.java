package ml.codeboy.engine.UI;

import ml.codeboy.engine.Game;

public class Button extends UIObject{
    Runnable runnable;
    public Button(Game game,String text,Runnable onClick) {
        super(game);
        this.text=text;
        runnable=onClick;
    }

    @Override
    public void press() {
        runnable.run();
    }
}
