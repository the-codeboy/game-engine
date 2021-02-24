package ml.codeboy.engine.exampleGames.rpg.Dialogs;

public class DialogScreen {

    private final DialogOption[]options;
    private String text="";

    private DialogScreen next;

    public DialogScreen(String text,DialogOption... options) {
        this.options = options;
        this.text=text;
    }

    public DialogOption[] getOptions() {
        return options;
    }

    public String getText() {
        return text;
    }

    public void openNext(Dialog dialog){
        if(next==null)
            System.err.println("tried opening next dialog screen but there are no more");
        else dialog.setScreen(next);
    }

    public void setNext(DialogScreen next) {
        this.next = next;
    }
}
