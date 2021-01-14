package ml.codeboy.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Sprites {
    private static Sprites sprites_instance=new Sprites();
    private Sprites(){
        sprites_instance=this;
    }

    /**
     * @param path the path to the image - this starts in the folder 'Sprites' in the resources folder
     * @return a BufferedImage loaded from the specified location
     * warning: if you intent to do operations with the image returned by this method use a clone of it instead
     * since every BufferedImage loaded will be saved and reused when this method gets called again with the same parameter
     * if this becomes a problem this method might return a clone of the image by default in the future
     */
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
    private BufferedImage read(String path){
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

    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((float)(newWidth - w) / 2, (float)(newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();

        return rotated;
    }
}
