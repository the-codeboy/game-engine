package ml.codeboy.engine.animation;

import ml.codeboy.engine.Sprites;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Animation {
    private List<BufferedImage>images=new ArrayList<>();
    private double pos=0;

    private float delayBetweenFrames = 0.1f;

    public float getDelayBetweenFrames() {
        return delayBetweenFrames;
    }

    public Animation setDelayBetweenFrames(float delayBetweenFrames) {
        this.delayBetweenFrames = delayBetweenFrames;
        return this;
    }

    private static HashMap<String,Animation>animations=new HashMap<>();


    public static Animation fromSpriteSheet(String name, int rows){
        return fromSpriteSheet(name, rows,1);
    }

    /**
     * @param name name of the spritesheet - this should be an image file located in the Sprites directory of the resources directory
     * @param rows the number of rows on the spritesheet
     * @param columns the number of columns on the spritesheet
     * @return the created Animation or null if the image could not be found
     */
    public static Animation fromSpriteSheet(String name, int rows, int columns){
        if(animations.containsKey(name))
            return animations.get(name).clone();
        BufferedImage image=Sprites.getSprite(name);
        if(image==null)
            return null;
        int width=image.getWidth()/rows;
        int height= image.getHeight()/columns;
        if(height*columns!=image.getHeight()||width*rows!=image.getWidth())
            System.err.println("tried splitting an image in a number of subimages that don´t fit " +
                    "- this might throw an Exception in the future");
        ArrayList<BufferedImage>frames=new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                frames.add(image.getSubimage(row*width,column*height,width,height));
            }
        }
        return new Animation(frames);
    }

    /**
     * @param gifName name of the gif to load - the gif needs to be located in a folder called 'Sprites' in the resources folder
     * @return the Animation object created
     * @deprecated not working yet
     */
    @Deprecated
    public static Animation fromGif(String gifName){
        if(animations.containsKey(gifName))
            return animations.get(gifName).clone();
        try {
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream ciis = ImageIO.createImageInputStream(Animation.class.getClassLoader().getResourceAsStream("Sprites/" + gifName));
            reader.setInput(ciis, false);

            int noi = reader.getNumImages(true);

            ArrayList<BufferedImage>images=new ArrayList<>();
            for (int i = 0; i < noi; i++) {
                images.add(reader.read(i));
            }

            IIOMetadata imageMetaData =  reader.getImageMetadata(0);
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();

            IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);

            IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
            float delay=Float.valueOf(graphicsControlExtensionNode.getAttribute("delayTime"));
            System.out.println("delay: "+graphicsControlExtensionNode.getAttribute("delayTime"));
            Animation anim= new Animation(images).setDelayBetweenFrames(delay);
            animations.put(gifName,anim);
            return anim;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)== 0) {
                return((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return(node);
    }

    public Animation(List<BufferedImage> images){
        this.images=images;
    }

    public Animation(String... imagePaths){
        if(imagePaths.length==0)
            throw new IllegalArgumentException("You need at least one Frame for an animation");
        for (String path:imagePaths){
            images.add(Sprites.getSprite(path));
        }
    }

    public Animation(String path,int number){
        this(pathsFromPath(path, number));
    }

    private static String[]pathsFromPath(String path,int number){
        String[]paths=new String[number];
        for (int i = 0; i < number; i++) {
            paths[i]=path.replace("%n%",String.valueOf(i));
        }
        return paths;
    }

    public void tick(double deltaTime){
        pos+=deltaTime/ delayBetweenFrames;
        pos%=images.size();
    }

    public BufferedImage getCurrentFrame(){
        return images.get((int) pos);
    }

    public int getFramesTotal(){
        return images.size();
    }

    public int getFramesLeft(){
        return (int) (images.size()-pos);
    }

    @Override
    public Animation clone(){
        return new Animation(images).setDelayBetweenFrames(getDelayBetweenFrames());
    }
}
