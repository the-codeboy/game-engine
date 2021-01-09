package ml.codeboy.engine;

import ml.codeboy.engine.events.DestroyEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;

public class Sprite {

    protected void setImage(BufferedImage image) {
        this.image = image;
    }

    private BufferedImage image;
    private final String name;
    private double x,y;
    private SpriteType type=SpriteType.Image;
    protected Game game;
    private boolean isDestroyed=false;

    private boolean interactable=true;

    public boolean isInteractable() {
        return interactable;
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private Color color;

    public enum SpriteType {
        Image,Circle,Rectangle,Custom;
    }

    public void destroy(){
        destroy(this);
    }

    public void destroy(Sprite sprite){
        if(sprite.isDestroyed)
            return;
        sprite.isDestroyed=true;
        DestroyEvent event=new DestroyEvent(this);
        sprite.onDestruction(event);
        if(!event.isCanceled())
            sprite.deleteNextTick();
    }

    protected void deleteNextTick(){
        Game.doNext(this::delete);
    }

    protected void delete(){
        getLayer().getSprites().remove(this);
    }

    protected void onDestruction(DestroyEvent event){}

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Sprite setWidthAndHeight(int width,int height) {
        this.height = height;
        this.width = width;
        return this;
    }

    public void setSize(int size) {
        this.height = size;
        this.width = size;
    }

    public int getSize(){
        return getWidth();
    }

    int width,height;

    public Layer getLayer() {
        return layer;
    }

    private Layer layer=Layer.DEFAULT;

    private static final HashMap<Layer,ArrayList<Sprite>>sprites=new HashMap<>();
    static {
        sprites.put(Layer.UI,new ArrayList<>());
        sprites.put(Layer.TOP,new ArrayList<>());
        sprites.put(Layer.MIDDLE,new ArrayList<>());
        sprites.put(Layer.DEFAULT,new ArrayList<>());
        sprites.put(Layer.BACK,new ArrayList<>());
        sprites.put(Layer.BACKGROUND,new ArrayList<>());
    }

    public static ArrayList<Sprite>getSpritesAt(Layer layer){
        return sprites.getOrDefault(layer,new ArrayList<>());
    }

    public Sprite(SpriteType type) {
        this.type=type;
        name="custom";
        init();
    }

    public Sprite(String name) {
        this(name,name);
    }

    public Sprite(String name,String path) {
        this(name, path,0,0);
    }

    public Sprite(String name,String path,double x,double y) {
        this(name, path,x,y,-1,-1);
    }

    public Sprite(String name,double x,double y,int width,int height) {
        this(name,name,x,y,width,height);
    }

    public Sprite(String name,String path,double x,double y,int width,int height) {
        this.name = name;
        image=Sprites.getSprite(path);
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        if(image!=null&&width==-1&&width==height)
            setWidthAndHeight(image.getWidth(), image.getHeight());
        init();
    }

    private void init() {
        setLayer(layer);
    }

    public void setLayer(Layer layer){
        getSpritesAt(this.layer).remove(this);
        this.layer=layer;
        getSpritesAt(layer).add(this);
    }

    public int getX() {
        return (int)getXDouble();
    }

    public double getXDouble() {
        return x!=-1||image==null?x:image.getWidth();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void addX(double x){
        setX(getXDouble()+x);
    }

    public int getY() {
        return (int)getYDouble();
    }

    public double getYDouble() {
        return y!=-1||image==null?y:image.getHeight();
    }

    public void setY(double y) {
        this.y = y;
    }

    public void addY(double y){
        setY(getYDouble()+y);
    }

    public void setPosition(Point position){
        setX(position.getX());
        setY(position.getY());
    }
    public void setPosition(int x,int y){
        setX(x);
        setY(y);
    }

    public boolean isTouching(Point point){
        return interactable&&
                getXDouble()-getWidth()/(float)2<= point.getX()&&getYDouble()-getHeight()/(float)2<= point.getY()&&
                getXDouble()+getWidth()/(float)2>= point.getX()&&getYDouble()+getHeight()/(float)2>= point.getY();
    }

    public void render(Graphics2D g){
        switch (type){
            case Image:{
                if(image==null)
                    return;
                if(width!=-1&&height!=-1)
                    g.drawImage(image,(int)x-getWidth()/2,(int)y-getHeight()/2,width,height,null);
                else
                    g.drawImage(image,(int)x-getWidth()/2,(int)y-getHeight()/2,null);
                break;
            }
            case Circle:{
                g.setColor(color);
                if(width>0&&height>0)
                g.drawOval(getX()-getWidth()/2,getY()-getHeight()/2,getWidth(),getHeight());
                break;
            }
            case Rectangle:{
                g.setColor(color);
                if(width>0&&height>0)
                    g.drawRect(getX()-getWidth()/2,getY()-getHeight()/2,getWidth(),getHeight());
                break;
            }
        }
    }


}
