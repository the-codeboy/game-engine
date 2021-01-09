package ml.codeboy.engine.exampleGames.creeperSweeper.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.Sprites;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Block extends GameObject {
    private static BufferedImage grass,pathWay;
    
    static{
        grass=Sprites.getSprite("grass.png").getSubimage(20,20,120,120);
        pathWay=Sprites.getSprite("grass.png").getSubimage(554,20,120,120);
    }

    private enum BlockType{
        GRASS,PATHWAY,AIR
    }

    private BlockType blockType=BlockType.GRASS;
    private boolean isCreeper;

    public Block(Game game,boolean isCreeper) {
        super(game,SpriteType.Custom);
        this.isCreeper=isCreeper;
        setSize(120);
    }

    @Override
    public void render(Graphics2D g) {
        if(blockType==BlockType.GRASS)
        g.drawImage(grass, getX() -getWidth()/2, getY() -getHeight()/2,getWidth(),getHeight(),null);
        else if(blockType==BlockType.PATHWAY)
            g.drawImage(pathWay, getX() -getWidth()/2, getY() -getHeight()/2,getWidth(),getHeight(),null);
    }
}
