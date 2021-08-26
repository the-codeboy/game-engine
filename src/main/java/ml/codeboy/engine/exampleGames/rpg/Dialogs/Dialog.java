package ml.codeboy.engine.exampleGames.rpg.Dialogs;

import ml.codeboy.engine.UI.ButtonGroup;
import ml.codeboy.engine.UI.UIText;
import ml.codeboy.engine.exampleGames.rpg.Rpg;

public class Dialog extends Interaction {

    private ButtonGroup group;
    private UIText text;
    private DialogScreen screen;

    public Dialog(DialogScreen screen, DialogScreen... screens) {
        setScreen(screen);
        DialogScreen last = screen;
        for (int i = 0; i < screens.length; i++) {
            last.setNext(screens[i]);
            System.out.println(last.getText() + "->" + screens[i].getText());
            last = screens[i];
        }
    }

    public void setScreen(DialogScreen screen) {
        this.screen = screen;
        if (!active || screen == null)
            return;
        text.setText(screen.getText());
        group.clear();
        for (DialogOption option : screen.getOptions()) {
            group.addButton(option.getText(), () -> option.execute(this));
        }
    }

    @Override
    public void open(Rpg rpg) {
        super.open(rpg);
        text = new UIText("", (int) (rpg.getWidth() * 0.3), (int) (rpg.getHeight() * 0.6), (int) (rpg.getWidth() * 0.5), (int) (rpg.getHeight() * 0.3));
        group = new ButtonGroup((int) (rpg.getWidth() * 0.8), (int) (rpg.getHeight() * 0.6), (int) (rpg.getWidth() * 0.15), (int) (rpg.getHeight() * 0.3));
        setScreen(screen);
    }

    @Override
    public void close() {
        super.close();
        text.destroy();
        group.destroy();
    }

    public void openNextScreen() {
        screen.openNext(this);
    }

    public Rpg getRpg() {
        return rpg;
    }

    public Dialog clone() {
        return new Dialog(screen);
    }
}
