package ml.codeboy.engine;

import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.animation.Animation;
import ml.codeboy.engine.events.DestroyEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * represents a Sprite that gets drawn on the games window
 * sprites are one of the layers specified by the Layer enum
 */
public class Sprite implements Comparable<Sprite> {

    private static final HashMap<Layer, ArrayList<Sprite>> sprites = new HashMap<>();

    static {
        sprites.put(Layer.UI, new ArrayList<>());
        sprites.put(Layer.TOP, new ArrayList<>());
        sprites.put(Layer.MIDDLE, new ArrayList<>());
        sprites.put(Layer.DEFAULT, new ArrayList<>());
        sprites.put(Layer.BACK, new ArrayList<>());
        sprites.put(Layer.BACKGROUND, new ArrayList<>());
    }

    private final String name;
    protected Game game;
    int width, height;
    private BufferedImage image;
    private double x, y;
    private SpriteType type = SpriteType.Image;
    private boolean isDestroyed = false;
    private boolean hasChanged = false;
    private double rotation = 0;
    private Animation animation;

    private boolean interactable = false;
    private Color color;
    private Layer layer = Layer.DEFAULT;

    /**
     * Depth of the element lower values will preferred over bigger ones default value 1024
     */
    private int depth = 1024;

    /**
     * @param type the type this Sprite has
     * @see SpriteType
     */
    public Sprite(SpriteType type) {
        this.type = type;
        name = "custom";
        init();
    }

    /**
     * @param name the name of this Sprite - this will also be the name of the image the sprite will try to load using
     *             {@link Sprites#getSprite(String)}
     */
    public Sprite(String name) {
        this(name, name);
    }

    /**
     * @param name the name of this Sprite - this currently doesn´t get used
     * @param path the path of the image for this Sprite
     * @see Sprites#getSprite(String)
     */
    public Sprite(String name, String path) {
        this(name, path, 0, 0);
    }

    /**
     * @param name the name of this Sprite - this currently doesn´t get used
     * @param path the path of the image for this Sprite
     * @param x    the x position of the new Sprite
     * @param y    the y position of the new Sprite
     * @see Sprites#getSprite(String)
     */
    public Sprite(String name, String path, double x, double y) {
        this(name, path, x, y, -1, -1);
    }

    /**
     * @param name   the name of this Sprite - this currently doesn´t get used
     * @param x      the x position of the new Sprite
     * @param y      the y position of the new Sprite
     * @param width  the width of the new Sprite
     * @param height the height of the new Sprite
     * @see Sprites#getSprite(String)
     */
    public Sprite(String name, double x, double y, int width, int height) {
        this(name, name, x, y, width, height);
    }

    /**
     * @param name   the name of this Sprite - this currently doesn´t get used
     * @param path   the path of the image for this Sprite
     * @param x      the x position of the new Sprite
     * @param y      the y position of the new Sprite
     * @param width  the width of the new Sprite
     * @param height the height of the new Sprite
     * @see Sprites#getSprite(String)
     */
    public Sprite(String name, String path, double x, double y, int width, int height) {
        this.name = name;
        image = Sprites.getSprite(path);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (image != null && width == -1 && width == height)
            setWidthAndHeight(image.getWidth(), image.getHeight());
        init();
    }

    /**
     * @param layer a Layer
     * @return the Sprites on the specified Layer
     */
    public static ArrayList<Sprite> getSpritesAt(Layer layer) {
        return sprites.getOrDefault(layer, new ArrayList<>());
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void setChanged() {
        this.hasChanged = true;
    }

    /**
     * @return this Sprite´s Animation - this might be null
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * @param animation sets the Animation for this Sprite to a clone of the specified Object - if the object is null nothing will happen
     */
    public void setAnimation(Animation animation) {
        if (animation == null)
            return;
        this.animation = animation.clone();
        image = this.animation.getCurrentFrame();
    }

    /**
     * @return whether this sprite is interactable - meaning you can click on it
     * this method is currently only used for UIObjects to determine if they can be clicked and it might
     * get moved to that class in the future
     * @see UIObject
     */
    public boolean isInteractable() {
        return interactable;
    }

    /**
     * @param interactable wheter this Sprite should be interactable
     * @see Sprite#isInteractable()
     */
    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    /**
     * @return the color of this Sprite
     * @see Sprite#setColor(Color)
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the new color of this Sprite - this will only get used if {@link Sprite#type} is
     *              {@link SpriteType#Circle} or {@link SpriteType#Rectangle} and it might be used for {@link SpriteType#Custom}
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * destroys this Sprite - this does the same as {@link Sprite#destroy(Sprite)} with this Sprite as parameter
     */
    public void destroy() {
        destroy(this);
    }

    /**
     * @param sprite the sprite to destroy
     */
    public void destroy(Sprite sprite) {
        if (sprite.isDestroyed)
            return;
        sprite.isDestroyed = true;
        DestroyEvent event = new DestroyEvent(this);
        sprite.onDestruction(event);
        if (!event.isCanceled())
            sprite.deleteNextTick();
    }

    /**
     * deletes this Sprite next tick
     */
    protected void deleteNextTick() {
        Game.doNext(this::delete);
    }

    /**
     * deletes this Sprite - don´t call this method use {@link Sprite#deleteNextTick()} instead
     */
    protected void delete() {
        getLayer().getSprites().remove(this);
    }

    /**
     * @param event the DestroyEvent - this might get removed in the future
     *              this will get called before a Sprite gets destroyed
     */
    protected void onDestruction(DestroyEvent event) {
    }

    /**
     * @return the width of this Sprite
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the new width for this Sprite
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height of this Sprite
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the new height for this Sprite
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param width  the new width
     * @param height the new height
     * @return the Sprite instance - for chaining
     */
    public Sprite setWidthAndHeight(int width, int height) {
        this.height = height;
        this.width = width;
        return this;
    }

    /**
     * @return the current size of this Sprite - if this Sprite doesn´t have a size because {@link Sprite#setSize(int)}
     * never got called this will return the width of the Sprite
     */
    public int getSize() {
        return getWidth();
    }

    /**
     * this is equivalent to {@link Sprite#setWidthAndHeight(int, int)} with size as parameter
     * for both width and height
     *
     * @param size the new size of this Sprite
     * @see Sprite#setWidthAndHeight(int, int)
     */
    public void setSize(int size) {
        this.height = size;
        this.width = size;
    }

    /**
     * @return a clone of the image of this sprite - this might be null
     */
    public BufferedImage getImage() {
        return image == null ? null : image.getSubimage(0, 0, image.getWidth(), image.getWidth());
    }

    /**
     * @param image this sprites new image
     */
    protected void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the name of this Sprite
     */
    public String getName() {
        return name;
    }

    /**
     * @return the Layer this Sprite is on
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * @param layer the new Layer for this Sprite
     */
    public void setLayer(Layer layer) {
        getSpritesAt(this.layer).remove(this);
        this.layer = layer;
        getSpritesAt(layer).add(this);
        Collections.sort(getSpritesAt(layer));
    }

    private void init() {
        this.game = Game.get();
        setLayer(layer);
    }

    /**
     * @return the x position of this Sprite casted to an int - it doesn´t get rounded the decimal places just get removed
     */
    public int getX() {
        return (int) getXDouble();
    }

    /**
     * @param x the new x position of this Sprite
     */
    public void setX(double x) {
        this.x = x;
        setChanged();
    }

    public int getLeftX() {
        return getX() - getWidth() / 2;
    }

    public int getXOnScreen() {
        return (int) getXOnScreenDouble();
    }

    public double getXOnScreenDouble() {
        return getX() - Game.get().getCameraPosition().getX();
    }

    /**
     * @return the exact x position of this Sprite
     */
    public double getXDouble() {
        return x != -1 || image == null ? x : image.getWidth();
    }

    public int getTopY() {
        return getY() - getHeight() / 2;
    }

    /**
     * @param x the value to add to the x position - this can be negative
     */
    public void addX(double x) {
        setX(getXDouble() + x);
    }

    /**
     * @return the y position of this Sprite casted to an int - it doesn´t get rounded the decimal places just get removed
     */
    public int getY() {
        return (int) getYDouble();
    }

    /**
     * @param y the new y position of this Sprite
     */
    public void setY(double y) {
        this.y = y;
        setChanged();
    }

    /**
     * @return the exact y position of this Sprite
     */
    public double getYDouble() {
        return y != -1 || image == null ? y : image.getHeight();
    }

    public int getYOnScreen() {
        return (int) getYOnScreenDouble();
    }

    public double getYOnScreenDouble() {
        return getY() - Game.get().getCameraPosition().getY();
    }

    /**
     * @param y the value to add to the y position - this can be negative
     */
    public void addY(double y) {
        setY(getYDouble() + y);
    }

    /**
     * returns the Position as a Point
     */
    public Point getPosition() {
        return new Point(getX(), getY());
    }

    /**
     * @param position the new position of this Sprite as a Point object
     */
    public void setPosition(Point position) {
        setX(position.getX());
        setY(position.getY());
    }

    /**
     * @param x the new x position of this Sprite
     * @param y the new y position of this Sprite
     */
    public void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }


    /**
     * Gets element depth
     *
     * @return element depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets depth of the element lower values will preferred over higher ones
     * @param depth any int value
     */
    public void setDepth(int depth) {
        this.depth = depth;
        Collections.sort(Sprite.getSpritesAt(layer));
    }

    /**
     * rotates a Sprite
     *
     * @param degrees how many degrees to add to the Sprites current rotation
     */
    public void rotate(double degrees) {
        this.rotation += degrees;
    }

    /**
     * @return this Sprites current rotation
     */
    public int getRotation() {
        return (int) rotation;
    }

    /**
     * sets the rotation of  a Sprite
     *
     * @param degrees the new rotation of this Sprite
     */
    public void setRotation(double degrees) {
        this.rotation = degrees;
    }

    /**
     * @param point the point to check if it is touching this Sprite
     * @return if the point is touching this Sprite
     */
    public boolean isTouching(Point point) {
        return interactable &&
                getXDouble() - getWidth() / (float) 2 <= point.getX() && getYDouble() - getHeight() / (float) 2 <= point.getY() &&
                getXDouble() + getWidth() / (float) 2 >= point.getX() && getYDouble() + getHeight() / (float) 2 >= point.getY();
    }

    public boolean intersects(Rectangle rectangle) {
        return rectangle.intersects(getX(), getY(), getWidth(), getHeight());
    }

    public boolean isOnScreen() {
        return game != null && game.getCamera().isOnScreen(this);
    }

    /**
     * @param g the Graphics2D object to render to
     */
    public void render(Graphics2D g) {
        if (Input.debugMode) {
            g.setColor(Color.RED);
            g.drawRect(getXOnScreen() - getWidth() / 2, getYOnScreen() - getHeight() / 2, getWidth(), getHeight());
        }
        //if(getLayer()==Layer.UI||isOnScreen())

        switch (type) {
            case Image: {
                if (animation != null && Game.get() != null) {
                    animation.tick(Game.get().getDeltaTime());
                    image = animation.getCurrentFrame();
                }
                if (image == null)
                    return;
                renderImage(g);
                break;
            }
            case Circle: {
                g.setColor(color);
                if (width > 0 && height > 0)
                    g.drawOval(getXOnScreen() - getWidth() / 2, getYOnScreen() - getHeight() / 2, getWidth(), getHeight());
                break;
            }
            case Rectangle: {
                g.setColor(color);
                if (width > 0 && height > 0)
                    g.drawRect(getXOnScreen() - getWidth() / 2, getYOnScreen() - getHeight() / 2, getWidth(), getHeight());
                break;
            }
            case Custom: {
                customRender(g);
                break;
            }
        }
        hasChanged = false;
    }

    private void renderImage(Graphics2D g) {
        BufferedImage image = getImage();
        if (image == null)
            return;
        if (getRotation() != 0) {
            double rotationRequired = Math.toRadians(getRotation());
            AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, (float) image.getWidth() / 2, (float) image.getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            image = op.filter(image, null);
        }
        if (width != -1 && height != -1)
            g.drawImage(image, getXOnScreen() - getWidth() / 2, getYOnScreen() - getHeight() / 2, width, height, null);
        else
            g.drawImage(image, getXOnScreen() - getWidth() / 2, getYOnScreen() - getHeight() / 2, null);
    }

    /**
     * called if {@link Sprite#type} is {@link SpriteType#Custom}
     * override this in your class - you can also use {@link Sprite#render(Graphics2D)} but this is not recommended
     *
     * @param g the Graphics2D object to render to
     */
    protected void customRender(Graphics2D g) {
    }

    @Override
    public int compareTo(Sprite o) {
        return Integer.compare(depth, o.depth);
    }

    /**
     * the type a Sprite can have
     */
    public enum SpriteType {
        Image, Circle, Rectangle, Custom
    }

}
