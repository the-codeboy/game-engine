package ml.codeboy.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Sprites {
    private static Sprites sprites_instance=new Sprites();
    public Sprites(){
        sprites_instance=this;
    }
    public static BufferedImage getSprite(String path){
        return sprites_instance.find(path);
    }
    public ArrayList<String> spriteNames=new ArrayList<>();
    public ArrayList<BufferedImage> sprites=new ArrayList<>();

    public BufferedImage find(String path){
        for (int i = 0; i < spriteNames.size(); i++) {
            if (spriteNames.get(i).equals(path))
                return sprites.get(i);
        }
        return read(path);
    }
    public BufferedImage read(String path){
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource("Sprites/" + path));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("can´t find " + path + " !!!");
        }
        spriteNames.add(path);
        sprites.add(img);
        return img;
    }
}
