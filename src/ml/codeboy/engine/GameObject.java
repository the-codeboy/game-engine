package ml.codeboy.engine;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashSet;

public class GameObject extends Sprite {

    private static final ArrayList<GameObject> gameObjects = new ArrayList<>();

    protected Game game;
    protected double deltaTime = 0;
    private boolean initialised = false;
    private Point target = null, startingPos;
    private double timeLeft = 0;
    private double nextPhysicsUpdate = Math.random(), physicsUpdateEvery = 0.5;
    private boolean isPhysicsTick = false;
    private boolean hasCollision = true;
    private boolean listenForCollision = false;
    private Class<? extends GameObject> type = GameObject.class;
    private HashSet<GameObject> lastTickCollisions = new HashSet<>();

    public GameObject(Game game) {
        super(SpriteType.Circle);
        init(game);
    }

    public GameObject(Game game, SpriteType type) {
        super(type);
        init(game);
    }

    public GameObject(String name, Game game) {
        super(name);
        init(game);
    }

    public GameObject(String name, String path, Game game) {
        super(name, path);
        init(game);
    }

    public GameObject(String name, int x, int y, Game game) {
        this(name, name, x, y, game);
    }

    public GameObject(String name, String path, int x, int y, Game game) {
        super(name, path, x, y);
        init(game);
    }

    public GameObject(String name, int x, int y, int width, int height, Game game) {
        super(name, x, y, width, height);
        init(game);
    }

    public GameObject(String name, String path, int x, int y, int width, int height, Game game) {
        super(name, path, x, y, width, height);
        init(game);
    }

    public static ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    protected void init(Game game) {
        if (initialised)
            throw new IllegalStateException("Gameobject already initialised");
        this.game = game;

        Game.get().getScheduler().scheduleTask(() -> {
            gameObjects.add(this);
        }, 0);
        //registerListeners();
        initialised = true;
    }

    private void registerListeners() {
        if (initialised)
            return;
        if (this instanceof MouseMotionListener)
            Game.get().getFrame().addMouseMotionListener((MouseMotionListener) this);
        if (this instanceof MouseWheelListener)
            Game.get().getFrame().addMouseWheelListener((MouseWheelListener) this);
        if (this instanceof MouseListener)
            Game.get().getFrame().addMouseListener((MouseListener) this);
        if (this instanceof KeyListener)
            Game.get().getFrame().addKeyListener((KeyListener) this);
    }

    public Game getGame() {
        return game;
    }

    @Override
    protected void delete() {
        super.delete();
        gameObjects.remove(this);
    }

    public Point getPosition() {
        return new Point(getX(), getY());
    }

    /**
     * @param point position to move to (this might never be reached)
     * @param when  the time in seconds it will take to get there
     */
    public void moveTo(Point point, double when) {
        target = point;
        startingPos = getPosition();
        this.timeLeft = when;
    }

    void internalTick() {
        deltaTime = Game.deltaTime();
        if (target != null && getPosition().distance(target) != 0) {
            timeLeft -= deltaTime;
            if (timeLeft <= 0) {
                setPosition(target);
            } else {
                addX((target.x - startingPos.x) * deltaTime / (timeLeft + deltaTime));
                addY((target.y - startingPos.y) * deltaTime / (timeLeft + deltaTime));
            }
        }
        if (isListeningForCollision() && (isPhysicsTick = (nextPhysicsUpdate -= deltaTime) < 0)) {
            nextPhysicsUpdate += physicsUpdateEvery;
            physicsUpdate();
        }
    }

    protected boolean isPhysicsTick() {
        return isPhysicsTick;
    }

    protected void setPhysicsUpdateEvery(double physicsUpdateEvery) {
        this.physicsUpdateEvery = physicsUpdateEvery;
        nextPhysicsUpdate = 0;
    }

    public boolean isMouseDown() {
        return Input.isMouseDown();
    }

    public Point getMousePosition() {
        Point position = Input.getMousePosition();
        if (position == null) {
            System.out.println("position is null");
            position = new Point();
        }
        return position;
    }

    public int getMouseX() {
        return Input.getMouseX();
    }

    public int getMouseY() {
        return Input.getMouseY();
    }

    public boolean hasCollision() {
        return hasCollision;
    }

    public void setCollision(boolean hasCollision) {
        this.hasCollision = hasCollision;
    }

    public boolean isListeningForCollision() {
        return listenForCollision;
    }

    public void listenForCollision(boolean listenForCollision) {
        this.listenForCollision = listenForCollision;
        if (listenForCollision)
            hasCollision = true;
    }

    public void listenForCollision(Class<? extends GameObject> type) {
        this.listenForCollision = true;
        this.type = type;
        hasCollision = true;
    }

    private void physicsUpdate() {
        if (hasCollision && listenForCollision) {
            HashSet<GameObject> thisTickCollisions = new HashSet<>();
            for (GameObject other : gameObjects) {
                if (other != null && other.getClass().isAssignableFrom(type) && other != this && other.collidesWith(this)) {
                    if (!lastTickCollisions.contains(other))
                        onCollision(other);
                    else
                        onCollisionStay(other);
                    thisTickCollisions.add(other);
                }
            }
            lastTickCollisions = thisTickCollisions;
        }
    }

    public HashSet<GameObject> getCollidingObjects() {
        return lastTickCollisions;
    }

    private boolean collidesWith(GameObject other) {
        if (!hasCollision || !other.hasCollision)
            return false;
        if (getY() + getHeight() / 2 < other.getY() - other.getHeight() / 2
                || getY() - getHeight() / 2 > other.getY() + other.getHeight() / 2) {
            return false;
        } else return getX() + getWidth() / 2 >= other.getX() - other.getWidth() / 2
                && getX() - getWidth() / 2 <= other.getX() + other.getWidth() / 2;
    }

    protected void onCollision(GameObject other) {

    }

    protected void onCollisionStay(GameObject other) {

    }

    protected void earlyTick() {
    }

    protected void tick() {
    }

    protected void lateTick() {
    }

}
