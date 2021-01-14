package ml.codeboy.engine.UI;

public class Button extends UIObject{
    Runnable runnable;
    public Button(String text) {
        this(text,()->{});
    }
    public Button(String text,Runnable onClick) {
        super();
        setInteractable(true);
        setAnimated(true);
        setText(text);
        runnable=onClick;
    }

    public void setClickAction(Runnable action){
        runnable=action;
    }

    @Override
    public void press() {
        runnable.run();
    }
}
