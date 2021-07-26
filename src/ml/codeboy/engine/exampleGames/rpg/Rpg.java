package ml.codeboy.engine.exampleGames.rpg;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.exampleGames.rpg.Dialogs.Interaction;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.Item;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.NPC;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.characters.Player;
import ml.codeboy.engine.exampleGames.rpg.Items.ItemStack;

public class Rpg extends Game {
    public Player player;
    private Interaction interaction = null;

    public Rpg() {
        super("Rpg");
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
    protected void initialise() {
        player = new Player(this);
        player.setPosition(getMiddleOfWindow());
        NPC npc=new NPC("max.png",this);
        new Item(new ItemStack(ItemStack.Material.SWORD,this)).setPosition(100,50);
        new Item(new ItemStack(ItemStack.Material.SWORD,this)).setPosition(100,500);
        setInitialised();
    }

    @Override
    protected void displayStats(String[] toDisplay) {
        super.displayStats(toDisplay);
    }
}
