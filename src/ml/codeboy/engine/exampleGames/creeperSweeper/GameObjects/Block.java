package ml.codeboy.engine.exampleGames.creeperSweeper.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.Sprites;
import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.exampleGames.creeperSweeper.CreeperSweeper;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Block extends UIObject {
    private static final BufferedImage grass,pathWay;
    
    static{
        grass=Sprites.getSprite("grass.png").getSubimage(20,20,120,120);
        pathWay=Sprites.getSprite("grass.png").getSubimage(554,20,120,120);
    }

    private enum BlockType{
        GRASS,PATHWAY,AIR
    }

    private BlockType blockType=BlockType.GRASS;
    private boolean isCreeper;
    CreeperSweeper cs;

    public Block(CreeperSweeper cs,boolean isCreeper) {
        super();
        this.isCreeper=isCreeper;
        this.cs=cs;
    }

    @Override
    public void press() {
        cs.doNextTick(cs::loose);
    }

    @Override
    public void render(Graphics2D g) {
        if(blockType==BlockType.GRASS)
        g.drawImage(grass, getX() -getWidth()/2, getY() -getHeight()/2,getWidth(),getHeight(),null);
        else if(blockType==BlockType.PATHWAY)
            g.drawImage(pathWay, getX() -getWidth()/2, getY() -getHeight()/2,getWidth(),getHeight(),null);
    }
}
