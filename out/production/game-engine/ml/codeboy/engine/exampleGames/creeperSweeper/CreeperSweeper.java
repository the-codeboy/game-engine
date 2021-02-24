package ml.codeboy.engine.exampleGames.creeperSweeper;

import ml.codeboy.engine.Game;

public class CreeperSweeper extends Game {
    public CreeperSweeper() {
        super("CreeperSweeper");
    }

    public static CreeperSweeper getInstance(){
        if(get()instanceof CreeperSweeper)
        return (CreeperSweeper) get();
        return null;
    }

    public void loose(){

    }
}
