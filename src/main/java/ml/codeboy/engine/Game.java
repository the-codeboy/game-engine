package ml.codeboy.engine;

import ml.codeboy.engine.Saving.GameVariables;
import ml.codeboy.engine.UI.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Optional;

public abstract class Game implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

    private static boolean gui=!GraphicsEnvironment.isHeadless();

    public static void setGui(boolean gui){
        Game.gui=gui;
    }

    private static JFrame frame;
    private static Game instance;
    private final String name;
    private final boolean fullScreen;
    private final long start = System.currentTimeMillis() / 1000;
    private final TaskScheduler scheduler = new TaskScheduler(this);
    protected UITheme theme = UITheme.DEFAULT;
    protected Color defaultColor = Color.WHITE;
    private BufferedImage screen;
    private Dimension preferredDimension = new Dimension(1000, 1000);
    private Graphics2D graphics;
    private double lastStatsUpdate;
    private long lastFrame = System.nanoTime();
    private int FPS;
    private boolean closed = false;
    private Camera camera;
    private BufferedImage backgroundImage;
    private double speed = 1;
    private Runnable exitAction = () -> {
    };
    private long tickStartTime, gameLogic, render, schedulerTime, earlyTick, tick, lateTick, internalTick, fullTick;
    private double average_gameLogic, average_fps, average_render, average_schedulerTime, average_earlyTick, average_tick, average_lateTick, average_internalTick, average_fullTick;
    private double numberOfCycles = 1;
    private String[] stats = {};
    private double deltaSeconds;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean initialised;
    private GameVariables variables;
    private int maxFPS=Integer.MAX_VALUE;
    private long requiredDeltaTime=0;

    private boolean closeGame = false;

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
     * @param name               the name of the new Game
     * @param preferredDimension the preferred dimensions of the Game´s window
     * @deprecated there´s problems with this please use fullscreen
     */
    @Deprecated
    public Game(String name, Dimension preferredDimension) {
        if (instance != null)
            throw new IllegalStateException("can not create second instance of game!");
        instance = this;
        this.name = name;
        this.fullScreen = false;
        this.preferredDimension = preferredDimension;
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

    private static void newFrame() {
        frame = new JFrame();
        frame.addKeyListener(Input.getInstance());
        frame.addMouseListener(Input.getInstance());
        frame.addMouseWheelListener(Input.getInstance());
        frame.addMouseMotionListener(Input.getInstance());
    }

    /**
     * @return the current Game singleton
     */
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

    public int getMaxFPS() {
        return maxFPS;
    }

    public void setMaxFPS(int maxFPS) {
        this.maxFPS = maxFPS;
        if(maxFPS>0)
            requiredDeltaTime=1000000000/maxFPS;
    }

    /**
     * @return the time since the last frame
     */
    public static double deltaTime() {
        return deltaTime(false);
    }

    public static void doNext(Runnable runnable) {
        if (get() != null)
            get().getScheduler().scheduleTask(runnable, 0, true);
        else System.err.println("there is no game");
    }

    /**
     * @param ignorePaused if paused should be ignored - see {@link Game#getDeltaTime(boolean)}
     * @return the time in seconds that passed since the last frame or 0 if there is no Game
     */
    public static double deltaTime(boolean ignorePaused) {
        if (get() == null)
            return 0;
        return get().getDeltaTime(ignorePaused);
    }

    /**
     * @param ignorePaused if paused should be ignored - see Game#getDeltaTime
     * @return the real time (not effected by game speed) in seconds
     * that passed since the last frame or 0 if there is no Game
     */
    public static double realDeltaTime(boolean ignorePaused) {
        if (get() == null)
            return 0;
        return get().getRealDeltaTime(ignorePaused);
    }

    public void componentResized(ComponentEvent ce) {
        screen = new BufferedImage(getFrame().getWidth(), getFrame().getHeight(), 1);
        graphics = screen.createGraphics();

        doNext(() -> {
                    for (Layer layer : Layer.values())
                        for (Sprite sprite : layer.getSprites())
                            sprite.recalculate();
                }
        );
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector getCameraPosition() {
        return camera != null ? camera.getPosition() : new Vector();
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

    public Vector getMiddleOfWindow() {
        return new Vector(getWidth() / 2d, getHeight() / 2d);
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

    public GameVariables getVariables() {
        return variables;
    }

    public void setVariables(GameVariables variables) {
        if (variables == null)
            throw new IllegalArgumentException("GameVariables can not be null");
        this.variables = variables;
    }

    /**
     * creates the window for the Game and initialises listeners - also starts the gameLoop
     */
    private void init() {
        if(gui)
            initGui();
        camera = new Camera(this);
        camera.setLayer(Layer.INVISIBLE);
        variables = initVariables();
        initialised = false;
        initialise();
        if (!initialised) {
            if(gui)
                getFrame().dispose();
            throw new IllegalStateException("Game not initialised");
        }
        doNext(() -> {
                    for (Layer layer : Layer.values())
                        for (Sprite sprite : layer.getSprites())
                            sprite.recalculate();
                }
        );
        Thread gameThread = new Thread(this::startGameLoop);
        gameThread.start();
    }

    private void initGui(){
        JFrame old=frame;
        newFrame();
        frame.getContentPane().addComponentListener(this);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeGame = true;
                exit();
            }
        };
        frame.addWindowListener(exitListener);
        if (fullScreen) {
            if (!getFrame().isDisplayable())
                getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            getFrame().setSize(preferredDimension);
        }
        getFrame().setUndecorated(fullScreen);
        if (getFrame().getWidth() > 0 && getFrame().getHeight() > 0) {
            screen = new BufferedImage(getFrame().getWidth(), getFrame().getHeight(), 1);
        } else {
            screen = new BufferedImage(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, 1);
        }
        getFrame().setLocationRelativeTo(null);
        getFrame().setVisible(true);

        graphics = screen.createGraphics();
        if (old != null)
            old.dispose();
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
        if(getVariables()!=null){
            for (GameObject gameObject : GameObject.getGameObjects()) {
                getVariables().saveVariables(gameObject);
            }
            getVariables().save();
        }
        GameObject.getGameObjects().clear();
        exitAction.run();
        if (closeGame) {
            frame.dispose();
            System.exit(0);
        }
    }

    protected void launchGame(Class<? extends Game> game) {
        Constructor<? extends Game> constructor;
        try {
            constructor = game.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(game.getName() + " does not have the right constructor!");
        }
        setExitAction(() -> {
            try {
                constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        exit();
    }

    protected GameVariables initVariables(){
        return null;
    }

    /**
     * Will get called when the Game is initialised right before the gameLoop gets started (before the first game tick)
     * You have to implement this in your Game for initialization and after the initialization is done call {@link Game#setInitialised()},
     * otherwise the Game will crash.
     */
    abstract protected void initialise();

    /**
     * Call this function inside {@link Game#initialise()}.
     *
     * @throws IllegalStateException when Game is already initialised.
     */
    protected void setInitialised() {
        if (initialised) {
            if(gui)
                getFrame().dispose();
            throw new IllegalStateException("Already initialised");
        }
        initialised = true;
    }

    protected final boolean isInitialised(){
        return initialised;
    }

    /**
     * starts the gameLoop
     * this is synchronised so that you wont have problems with multithreading
     */
    private synchronized void startGameLoop() {
        while (!closed) {
            tickStartTime = System.nanoTime();
            double deltaTime = tickStartTime - lastFrame;
            if(deltaTime<requiredDeltaTime){
                try {
                    long waitTime= (long) (requiredDeltaTime-deltaTime)/1000000;
                    Thread.sleep(waitTime);
                    tickStartTime = System.nanoTime();
                    deltaTime = tickStartTime - lastFrame;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lastFrame = tickStartTime;
            deltaSeconds = deltaTime / 1000000000;
            FPS = (int) (1000000000 / deltaTime);
            if ((tickStartTime - lastStatsUpdate) > 1000000000) {
                lastStatsUpdate = tickStartTime;
                average_fps /= numberOfCycles;
                average_gameLogic /= numberOfCycles;
                average_render /= numberOfCycles;
                average_schedulerTime /= numberOfCycles;
                average_earlyTick /= numberOfCycles;
                average_tick /= numberOfCycles;
                average_lateTick /= numberOfCycles;
                average_internalTick /= numberOfCycles;
                average_fullTick /= numberOfCycles;
                stats = new String[]{formatStatNumbers("FPS", average_fps),
                        formatStatNumbers("full tick", average_fullTick),
                        formatStatNumbersPercentage("render time", average_render, average_fullTick),
                        formatStatNumbersPercentage("scheduler time", average_schedulerTime, average_fullTick),
                        formatStatNumbersPercentage("early tick", average_earlyTick, average_fullTick),
                        formatStatNumbersPercentage("tick", average_tick, average_fullTick),
                        formatStatNumbersPercentage("internal tick", average_internalTick, average_fullTick),
                        formatStatNumbersPercentage("late tick", average_lateTick, average_fullTick)};
                average_fps = 0;
                average_gameLogic = 0;
                average_render = 0;
                average_schedulerTime = 0;
                average_earlyTick = 0;
                average_tick = 0;
                average_lateTick = 0;
                average_internalTick = 0;
                average_fullTick = 0;
                numberOfCycles = 1;
            }
            numberOfCycles++;
            average_fps += FPS;
            average_gameLogic += gameLogic;
            average_render += render;
            average_schedulerTime += schedulerTime;
            average_earlyTick += earlyTick;
            average_tick += tick;
            average_lateTick += lateTick;
            average_internalTick += internalTick;
            average_fullTick += fullTick;
            try {
                Input.getInstance().tick();
                tickStartTime = System.currentTimeMillis();
                executeGameLogic();
                gameLogic = System.currentTimeMillis() - tickStartTime;
                if(gui)
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

    private String formatStatNumbersPercentage(String before, Double part, Double full) {
        double percentage = (part / full) * 100;
        if (percentage < 0.01) {
            return String.format("%s: %.0f%%", before, percentage);
        }
        return String.format("%s: %.1f%%", before, percentage);
    }

    private String formatStatNumbers(String before, Double number) {
        if (number < 0.01) {
            return String.format("%s: %.0f", before, number);
        }
        return String.format("%s: %.2f", before, number);
    }

    /**
     * calls the tick functions on the GameObjects and on this Game´s scheduler
     */
    private void executeGameLogic() {
        scheduler.tick();
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
            ArrayList<Sprite> list = currentLayer.getSprites();
            for (int i = list.size() - 1; i >= 0; i--) {
                list.get(i).render(graphics);
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
        return getDeltaTime(false);
    }

    /**
     * @param ignorePaused if paused should be ignored for the return value - this can be useful
     *                     if you want to only pause GameObjects but not UI animations
     * @return the current deltaTime or #getDeltaTime
     */
    public double getDeltaTime(boolean ignorePaused) {
        return getDeltaTime(ignorePaused, false);
    }

    /**
     * @param ignorePaused if paused should be ignored for the return value - this can be useful
     *                     if you want to only pause GameObjects but not UI animations
     * @return the current deltaTime or #getDeltaTime
     */
    public double getRealDeltaTime(boolean ignorePaused) {
        return getDeltaTime(ignorePaused, true);
    }

    public double getRealDeltaTime() {
        return getRealDeltaTime(false);
    }

    private double getDeltaTime(boolean ignorePaused, boolean ignoreSpeed) {
        if (ignorePaused || !isPaused) {
            return ignoreSpeed ? deltaSeconds : deltaSeconds * speed;
        }
        return 0;
    }

    protected void setCursor(Cursor cursor) {
        if(getFrame()!=null)
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
        if(gui)
            return getFrame().getWidth();
        return preferredDimension.width;
    }

    /**
     * @return the height of the game - this value might change if the game is not in fullscreen mode
     */
    public int getHeight() {
        if(gui)
            return getFrame().getHeight();
        return preferredDimension.height;
    }

    public double getAverageFPS() {
        return ((int)(100*average_fps/numberOfCycles))/100d;
    }

    /**
     * @return the preferred width of the game - this value might not be the same as {@link Game#getWidth()}
     */
    public int getPreferredWidth() {
        return preferredDimension.width;
    }

    /**
     * @return the preferred height of the game - this value might not be the same as {@link Game#getHeight()} ()}
     */
    public int getPreferredHeight() {
        return preferredDimension.height;
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
