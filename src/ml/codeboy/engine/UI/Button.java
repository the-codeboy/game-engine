package ml.codeboy.engine.UI;

public class Button extends UIObject{
    Runnable runnable;
    public Button(String text,Runnable onClick) {
        super();
        setAnimated(true);
        setText(text);
        runnable=onClick;
    }

    @Override
    public void press() {
        runnable.run();
    }
}
