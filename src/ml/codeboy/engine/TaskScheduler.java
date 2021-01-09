package ml.codeboy.engine;

import java.util.ArrayList;

public class TaskScheduler {
    private ArrayList<Task>tasks=new ArrayList<>();
    TaskScheduler(Game game){

    }

    public Task scheduleTask(Runnable toRun, double delay, double period){
        return scheduleTask(toRun, delay, period,false);
    }

    public Task scheduleTask(Runnable toRun, double delay, double period,boolean useRealTime){
        Task task=new Task(this) {
            @Override
            protected void run() {
                toRun.run();
            }
        };
        task.period=period;
        task.timeSinceLast=period-delay;
        task.useRealTime=useRealTime;
        if(period==-1)
            task.runOnce=true;
        tasks.add(task);
        return task;
    }

    void scheduleTask(Task task){
        tasks.add(task);
    }

    public Task scheduleTask(Runnable toRun,double delay){
       return scheduleTask(toRun, delay,false);
    }

    public Task scheduleTask(Runnable toRun,double delay,boolean useRealTime){
        return scheduleTask(toRun,delay,-1,useRealTime);
    }

    void doTick(){
        for (int i = 0, tasksSize = tasks.size(); i < tasksSize; i++) {
            Task task = tasks.get(i);
            task.tick();
            if(task.isCanceled) {
                tasks.remove(i++);
                tasksSize--;
            }
        }
    }
}
