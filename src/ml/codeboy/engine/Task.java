package ml.codeboy.engine;

public abstract class Task{

    protected abstract void run();
    protected double period=1;
    double timeSinceLast=0;
    boolean runOnce=false;

    boolean isCanceled=false,
    started=false;

    public Task(TaskScheduler scheduler) {
        this.scheduler = scheduler;
        onCreation();
    }

    protected void onCreation() {

    }

    TaskScheduler scheduler;

    public boolean isCanceled() {
        return isCanceled;
    }

    void tick(){
        timeSinceLast+=Game.deltaTime(true);
        while(!isCanceled&&timeSinceLast>period){
            timeSinceLast-=period;
            run();
            if(runOnce||period==0)
                cancel();
        }
    }

    public void cancel(){
        isCanceled=true;
    }

    protected void start(){
        if(!started&&!isCanceled)
        scheduler.scheduleTask(this);
        started=true;
    }

}
