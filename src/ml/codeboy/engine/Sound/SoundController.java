package ml.codeboy.engine.Sound;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SoundController {
    private static SoundController instance;
    private List<Sound> queue;
    private SoundStatus status;
    private boolean loop;

    private SoundController() {
        queue = Collections.synchronizedList(new LinkedList<>());
        status = SoundStatus.READY;
    }

    public static SoundController getInstance() {
        if (instance == null)
            instance = new SoundController();
        return instance;
    }

    public void addToQueue(String path) {
        Thread loadingThread = new Thread(() -> queue.add(new Sound(path, () -> SoundController.getInstance().finishedPlaying())));
        loadingThread.start();
    }

    protected void finishedPlaying() {
        if (loop)
            queue.add(queue.get(0));
        queue.remove(0);
        play();
    }

    public void play() {
        if (status == SoundStatus.READY && queue.size() > 0) {
            status = SoundStatus.PLAYING;
            queue.get(0).play();
        }
    }

    public void toggle() {
        if (status == SoundStatus.PLAYING) {
            pause();
        } else if (status == SoundStatus.READY) {
            play();
        }
    }

    public void pause() {
        if (queue.size() > 0) {
            if (queue.get(0).getStatus() == SoundStatus.PLAYING) {
                queue.get(0).pause();
                this.status = SoundStatus.READY;
            }
        }
    }

    public SoundStatus getStatus() {
        return status;
    }

    public void addVolume(float value) {
        if (queue.size() > 0) {
            if (queue.get(0).getStatus() == SoundStatus.PLAYING) {
                queue.get(0).addVolume(value);
            }
        }
    }
}
