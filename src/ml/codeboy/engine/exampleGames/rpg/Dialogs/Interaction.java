package ml.codeboy.engine.exampleGames.rpg.Dialogs;

import ml.codeboy.engine.exampleGames.rpg.Rpg;

public class Interaction {

    protected boolean active = false;

    protected Rpg rpg;


    public void open(Rpg rpg) {
        this.rpg = rpg;
        active = true;
    }

    public void close() {
        active = false;
        rpg.closeDialog();
    }

    public Interaction clone() {
        return this;
    }
}
