package ml.codeboy.engine.exampleGames.rpg;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.exampleGames.rpg.Dialogs.Interaction;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.NPC;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.characters.Player;

public class Rpg extends Game {
    public Player player;
    private Interaction interaction = null;

    public Rpg() {
        super("Rpg");
        player = new Player(this);
        player.setPosition(getMiddleOfWindow());
        NPC npc = new NPC("max.png", this);
    }

    public void openInteraction(Interaction interaction) {
        interaction = interaction.clone();
        if (this.interaction != null)
            return;
        this.interaction = interaction;
        interaction.open(this);
    }

    public void closeDialog() {
        interaction = null;
    }

    @Override
    protected void displayStats(String[] toDisplay) {
        super.displayStats(toDisplay);
    }
}
