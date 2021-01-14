package ml.codeboy.engine;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Animation {
    private ArrayList<BufferedImage>images=new ArrayList<>();
    private double pos=0;

    private float delayBetweenFrames =0.2f;

    public float getDelayBetweenFrames() {
        return delayBetweenFrames;
    }

    public Animation setDelayBetweenFrames(float delayBetweenFrames) {
        this.delayBetweenFrames = delayBetweenFrames;
        return this;
    }

    private static HashMap<String,Animation>animations=new HashMap<>();

    /**
     * @param gifName name of the gif to load - the gif needs to be located in a folder called 'Sprites' in the resources folder
     * @return the Animation object created
     * deprecated not working yet
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

    public Animation(ArrayList<BufferedImage>images){
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

    @Override
    protected Animation clone(){
        return new Animation(images).setDelayBetweenFrames(getDelayBetweenFrames());
    }
}
