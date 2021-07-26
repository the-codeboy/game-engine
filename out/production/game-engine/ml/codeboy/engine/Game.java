package ml.codeboy.engine;

import com.sun.istack.internal.Nullable;
import ml.codeboy.engine.UI.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;

public abstract class Game implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

    private static final JFrame frame = new JFrame();
    private static Game instance;

    static {
        frame.addKeyListener(Input.getInstance());
        frame.addMouseListener(Input.getInstance());
        frame.addMouseWheelListener(Input.getInstance());
        frame.addMouseMotionListener(Input.getInstance());
    }

    private final String name;
    private final boolean fullScreen;
    private final long start = System.currentTimeMillis() / 1000;
    private final TaskScheduler scheduler = new TaskScheduler(this);
    protected UITheme theme = UITheme.DEFAULT;
    protected Color defaultColor = Color.WHITE;
    private BufferedImage screen;
    private int preferredX = 1000, preferredY = 1000;
    private Graphics2D graphics;
    private Scene scene;
    private double lastFPS;
    private long lastFrame = System.nanoTime();
    private int FPS;
    private boolean closed = false;
    private Camera camera;

    private BufferedImage backgroundImage;

    private double speed = 1;
    private Runnable exitAction = () -> {
    };
    private long tickStartTime, gameLogic, render, schedulerTime, earlyTick, tick, lateTick, internalTick, fullTick;
    private long average_gameLogic, average_render, average_schedulerTime, average_earlyTick, average_tick, average_lateTick, average_internalTick, average_fullTick;
    private String[] stats = {};
    private double deltaSeconds;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    /**
     * @param name the name of the new Game
     */
    public Game(String name) {
        this(name, true);
    }

    /**
     * @param name  name of the Game
     * @param theme UITheme of the Game
     */
    public Game(String name, UITheme theme) {
        this(name, true, theme);
    }

    /**
     * @param name       the name of the new Game
     * @param preferredX the width of the Game´s window
     * @param preferredY the height of the Game´s window
     * @deprecated there´s problems with this please use fullscreen
     */
    @Deprecated
    public Game(String name, int preferredX, int preferredY) {
        if (instance != null)
            throw new IllegalStateException("can not create second instance of game!");
        instance = this;
        this.name = name;
        this.fullScreen = false;
        this.preferredX = preferredX;
        this.preferredY = preferredY;
        init();
    }

    /**
     * @param name       name of the Game
     * @param fullScreen if the Game should be fullscreen or not
     */
    public Game(String name, boolean fullScreen) {
        this(name, fullScreen, UITheme.DEFAULT);
    }

    /**
     * @param name       the name of the new Game
     * @param fullScreen if the Game should be fullscreen - this defaults to true
     *                   use the other constructors instead
     */
    @Deprecated
    public Game(String name, boolean fullScreen, UITheme theme) {
        this.theme = theme;
        this.name = name;
        this.fullScreen = fullScreen;
        if (instance != null)
            throw new IllegalStateException("can not create second instance of game!");
        instance = this;
        init();
    }

    /**
     * @return the current Game singleton
     */
    @Nullable
    public static Game get() {
        return instance;
    }

    /**
     * @return mouse x position
     * @deprecated this returns the position on screen use Input#getMouseX()
     */
    //<editor-fold desc="getter">
    @Deprecated
    public static int getMouseX() {
        return MouseInfo.getPointerInfo().getLocation().x;
    }

    /**
     * @return mouse y position
     * @deprecated this returns the position on screen use Input#getMouseY()
     */
    @Deprecated
    public static int getMouseY() {
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    /**
     * @param ignorePaused if paused should be ignored - see Game#getDeltaTime
     * @return the time in seconds that passed since the last frame or 0 if there is no Game
     */
    @Nullable
    public static double deltaTime(boolean ignorePaused) {
        if (get() == null)
            return 0;
        return get().getDeltaTime(ignorePaused);
    }

    /**
     * @return the time since the last frame
     */
    @Nullable
    public static double deltaTime() {
        return deltaTime(false);
    }

    public static void doNext(Runnable runnable) {
        if (get() != null)
            get().getScheduler().scheduleTask(runnable, 0, true);
        else System.err.println("there is no game");
    }

    public void componentResized(ComponentEvent ce) {
        screen = new BufferedImage(getFrame().getWidth(), getFrame().getHeight(), 1);
        graphics = screen.createGraphics();
    }

    public Camera getCamera() {
        return camera;
    }

    public Point getCameraPosition() {
        return camera != null ? camera.getPosition() : new Point();
    }

    public UITheme getTheme() {
        return theme;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * @return how many seconds have passed since this Game has been instantiated
     */
    public long getSecondsRun() {
        return System.currentTimeMillis() / 1000 - start;
    }

    /**
     * @return gets the TaskScheduler of this Game. The TaskScheduler can be used to schedule Tasks which will
     * be run at a specified interval or once a certain time. For more information see TaskScheduler#scheduleTask
     */
    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public Point getMiddleOfWindow() {
        return new Point(getFrame().getWidth() / 2, getFrame().getHeight() / 2);
    }

    Graphics2D getGraphics() {
        return graphics;
    }

    BufferedImage getScreen() {
        return screen;
    }

    BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    protected void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    /**
     * @return the JFrame this Game is rendering to
     * might be removed in a future release but is still safe to use at the moment
     */
    @Deprecated
    public JFrame getFrame() {
        return frame;
    }

    /**
     * @return the name of this Game
     */
    public String getName() {
        return name;
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    /**
     * creates the window for the Game and initialises listeners - also starts the gameLoop
     */
    private void init() {
        frame.getContentPane().addComponentListener(this);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if (fullScreen) {
            if (!getFrame().isDisplayable())
                getFrame().setUndecorated(true);
            getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            getFrame().setSize(preferredX, preferredY);
        }
        if (getFrame().getWidth() > 0 && getFrame().getHeight() > 0) {
            screen = new BufferedImage(getFrame().getWidth(), getFrame().getHeight(), 1);
        } else {
            screen = new BufferedImage(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, 1);
        }
        getFrame().setLocationRelativeTo(null);
        //panel=new JPanel(){
        //    @Override
        //    protected void paintComponent(Graphics g) {
        //        g.drawImage(screen,0,0,null);
        //    }
        //};
        //panel.setVisible(true);
        //getFrame().removeAll();
        //getFrame().add(panel);
        getFrame().setVisible(true);

        graphics = screen.createGraphics();
        camera = new Camera(this);
        camera.setLayer(Layer.INVISIBLE);
        scene = new Scene(this);
        initialise();
        Thread gameThread = new Thread(this::startGameLoop);
        gameThread.start();
    }

    protected void exit() {
        closed = true;
    }

    protected void setExitAction(Runnable exitAction) {
        this.exitAction = exitAction;
    }

    private void onExit() {
        Layer layer = Layer.BACKGROUND;
        while (layer != null) {
//            for (Sprite sprite:layer.getSprites())
//                sprite.destroy();
            layer.clear();
            layer = layer.getNext();
        }
//        GameObject.getGameObjects().forEach(g->g.onDestruction(new DestroyEvent(g)));
        GameObject.getGameObjects().clear();
        exitAction.run();
    }

    protected void launchGame(Class<? extends Game> game) {
        Constructor<? extends Game> constructor;
        try {
            constructor = game.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(game.getName() + " does not have the right constructor!");
        }
        exit();
        setExitAction(() -> {
            try {
                constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * will get called when the Game is initialised right before the gameLoop gets started (before the first game tick)
     * override this on your Game to do initialisation
     *
     * @deprecated might get removed because you can use your Game´s constructor for initialisation
     */
    @Deprecated
    protected void initialise() {
    }

    /**
     * starts the gameLoop
     * this is synchronised so that you wont have problems with multithreading
     */
    private synchronized void startGameLoop() {
        while (!closed) {
            tickStartTime = System.nanoTime();
            double deltaTime = tickStartTime - lastFrame;
            lastFrame = tickStartTime;
            deltaSeconds = deltaTime / 1000000000;
            if ((tickStartTime - lastFPS) > 1000000000) {
                FPS = (int) (1000000000 / deltaTime);
                lastFPS = tickStartTime;

                stats = new String[]{"FPS: " + FPS, "full tick: " + average_fullTick, "render time: " + average_render, "scheduler time: " + average_schedulerTime,
                        "early tick: " + average_earlyTick, "tick: " + average_tick, "internal tick: " + average_internalTick, "late tick: " + average_lateTick};

                average_gameLogic = 0;
                average_render = 0;
                average_schedulerTime = 0;
                average_earlyTick = 0;
                average_tick = 0;
                average_lateTick = 0;
                average_internalTick = 0;
                average_fullTick = 0;
            }
            average_gameLogic += gameLogic;
            average_render += render;
            average_schedulerTime += schedulerTime;
            average_earlyTick += earlyTick;
            average_tick += tick;
            average_lateTick += lateTick;
            average_internalTick += internalTick;
            average_fullTick += fullTick;
            try {
                tickStartTime = System.currentTimeMillis();
                executeGameLogic();
                gameLogic = System.currentTimeMillis() - tickStartTime;
                render();
                render = System.currentTimeMillis() - tickStartTime - gameLogic;
                fullTick = System.currentTimeMillis() - tickStartTime;
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
        instance = null;
        onExit();
    }

    /**
     * calls the tick functions on the GameObjects and on this Game´s scheduler
     */
    private void executeGameLogic() {
        scheduler.doTick();
        schedulerTime = System.currentTimeMillis() - tickStartTime;
        earlyTick();
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.earlyTick();
        }
        earlyTick = System.currentTimeMillis() - tickStartTime - schedulerTime;
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.internalTick();
        }
        internalTick = System.currentTimeMillis() - tickStartTime - schedulerTime - earlyTick;
        tick();
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.tick();
        }
        tick = System.currentTimeMillis() - tickStartTime - schedulerTime - earlyTick - internalTick;
        lateTick();
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.lateTick();
        }
        lateTick = System.currentTimeMillis() - tickStartTime - schedulerTime - earlyTick - internalTick - tick;
    }

    /**
     * renders the sprites from each layer
     */
    private void render() {
        graphics.clearRect(0, 0, screen.getWidth(), screen.getHeight());
        if (backgroundImage != null)
            graphics.drawImage(backgroundImage, 0, 0, null);
        Layer currentLayer = Layer.BACKGROUND;
        while (currentLayer != null) {
            for (Sprite sprite :
                    currentLayer.getSprites()) {
                sprite.render(graphics);
            }
            currentLayer = currentLayer.getNext();
        }
        graphics.setFont(graphics.getFont().deriveFont(20f));
        displayStats(stats);

        getFrame().getGraphics().drawImage(screen, 0, 0, getWidth(), getHeight(), null);
    }

    /**
     * @param toDisplay a String array with information to be displayed on the top left of the screen
     *                  override this and call super#displayStats to change the displayed stats
     *                  even if you don´t have stats to display you should definitely override this
     */
    protected void displayStats(String[] toDisplay) {
        graphics.setColor(Color.WHITE);
        int textHeight = graphics.getFontMetrics().getHeight();
        for (int i = 0; i < toDisplay.length; i++) {
            graphics.drawString(toDisplay[i], 10, textHeight + textHeight * i);
        }
    }

    /**
     * @return the current FPS
     */
    public int getCurrentFPS() {
        return FPS;
    }

    /**
     * @return the time in seconds that passed since the last frame
     */
    public double getDeltaTime() {
        return isPaused ? 0 : speed * deltaSeconds;
    }

    /**
     * @param ignorePaused if paused should be ignored for the return value - this can be useful
     *                     if you want to only pause GameObjects but not UI animations
     * @return the current deltaTime or #getDeltaTime
     */
    public double getDeltaTime(boolean ignorePaused) {
        return ignorePaused ? deltaSeconds : getDeltaTime();
    }

    protected void setCursor(Cursor cursor) {
        getFrame().setCursor(cursor);
    }

    /**
     * for restarting the Game - override to use this
     */
    public void restart() {
        setGameOver(false);
    }

    public void pause() {
        isPaused = true;
    }

    public void unPause() {
        isPaused = false;
        isGameOver = false;
    }

    public void togglePause() {
        if (!isGameOver) {
            if (isPaused)
                unPause();
            else
                pause();
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
        if (isGameOver)
            pause();
        else
            unPause();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    protected void earlyTick() {
    }

    protected void tick() {
    }

    protected void lateTick() {
    }

    /**
     * @return the width of the game - this value might change if the game is not in fullscreen mode
     */
    public int getWidth() {
        return getFrame().getWidth();
    }

    /**
     * @return the height of the game - this value might change if the game is not in fullscreen mode
     */
    public int getHeight() {
        return getFrame().getHeight();
    }

    /**
     * runs a runnable in the next Tick
     *
     * @param runnable the runnable to run
     */
    public void doNextTick(Runnable runnable) {
        getScheduler().scheduleTask(runnable, 0, true);
    }
    //</editor-fold>
}
