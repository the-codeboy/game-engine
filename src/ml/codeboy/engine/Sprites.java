package ml.codeboy.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Sprites {
    private static Sprites sprites_instance = new Sprites();
    public static final BufferedImage error = getSprite("error.png");
    private final HashMap<String, BufferedImage> loadedSprites = new HashMap<>();

    public Sprites() {
        sprites_instance = this;
    }

    public static BufferedImage getSprite(String path) {
        return sprites_instance.find(path);
    }

    public BufferedImage find(String path) {
        if (loadedSprites.containsKey(path))
            return loadedSprites.get(path);
        return read(path);
    }

    public BufferedImage read(String path) {
        BufferedImage img;
        try {
//            System.out.println("Sprites/" + path+" : "+Sprites.class.getClassLoader().getResource(""));
            img = ImageIO.read(getClass().getClassLoader().getResource("Sprites/" + path));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("can´t find " + path + " !!!");
            return null;
        }
        loadedSprites.put(path, img);
        return img;
    }
}
