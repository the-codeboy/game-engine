package ml.codeboy.engine.exampleGames.rpg.Dialogs;

import java.util.function.Consumer;

public class DialogOption {

    private final String text;
    public static Consumer<Dialog> quit= Dialog::close,nextScreen= Dialog::openNextScreen;
    private final Consumer<Dialog>action;

    public DialogOption(String text, Consumer<Dialog> action) {
        this.text = text;
        this.action = action;
    }

    void execute(Dialog dialog){
        action.accept(dialog);
    }

    public String getText() {
        return text;
    }
}
