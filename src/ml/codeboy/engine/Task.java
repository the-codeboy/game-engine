package ml.codeboy.engine;

public abstract class Task implements Runnable {

    protected double period = 1;
    double timeSinceLast = 0;
    boolean runOnce = false;
    boolean useRealTime = false;
    boolean isCanceled = false,
            started = false;
    TaskScheduler scheduler;

    public Task(TaskScheduler scheduler) {
        this.scheduler = scheduler;
        initialise();
    }

    public abstract void run();

    protected void initialise() {

    }

    public boolean isCanceled() {
        return isCanceled;
    }

    void tick() {
        timeSinceLast += Game.deltaTime(useRealTime);
        while (!isCanceled && timeSinceLast > period) {
            timeSinceLast -= period;
            run();
            if (runOnce || period == 0)
                cancel();
        }
    }

    public void cancel() {
        isCanceled = true;
    }

    protected void start() {
        if (!started && !isCanceled)
            scheduler.scheduleTask(this);
        started = true;
    }

}
