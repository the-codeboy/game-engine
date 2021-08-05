package ml.codeboy.engine.Sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SoundController {
    private static SoundController instance;
    private List<Sound> queue;
    private SoundStatus status;
    private boolean loop;
    private Clip clip;

    private Thread jobThread;

    private SoundController() {
        queue = Collections.synchronizedList(new LinkedList<>());
        jobThread = new Thread(() -> {
            try {
                clip = AudioSystem.getClip();
                status = SoundStatus.READY;
                System.out.println("Audio ready");
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                status = SoundStatus.FAILED;
            }
        });
        jobThread.start();
    }

    /**
     * Can be used to wait for inner task to complete:
     * - initialisation of the SoundController {@link SoundController#SoundController()}
     * - adding song to the queue {@link SoundController#addToQueue(String)}
     *
     * @throws InterruptedException if any thread has interrupted the current thread. The
     *                              <i>interrupted status</i> of the current thread is
     *                              cleared when this exception is thrown.
     */
    public void joinThread() throws InterruptedException {
        jobThread.join();
    }

    public static SoundController getInstance() {
        if (instance == null)
            instance = new SoundController();
        if (instance.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        return instance;
    }

    public void addToQueue(String path) {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        try {
            jobThread.join();
            System.out.println("Adding song:" + path);
            jobThread = new Thread(() -> queue.add(new Sound(path, clip, () -> SoundController.getInstance().finishedPlaying())));
            jobThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addToQueue(String path, Runnable runAfter) {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        try {
            jobThread.join();
            System.out.println("Adding song:" + path);
            jobThread = new Thread(() -> queue.add(new Sound(path, clip, () -> {
                runAfter.run();
                SoundController.getInstance().finishedPlaying();
            })));
            jobThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void skipSound() {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        if (queue.size() > 0) {
            Sound sound = queue.get(0);
            if (sound.getStatus() == SoundStatus.PLAYING || sound.getStatus() == SoundStatus.PAUSED) {
                sound.stop();
            }
        }
    }

    protected void finishedPlaying() {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        Sound finished = queue.remove(0);
        if (loop && finished.getStatus()!=SoundStatus.FAILED) {
            queue.add(new Sound(finished));
        }
        status = SoundStatus.READY;
        play();
    }

    public void play() {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        if (status == SoundStatus.READY && queue.size() > 0) {
            Sound sound=queue.get(0);
            if(sound.getStatus().isPlayable()) {
                status = SoundStatus.PLAYING;
                queue.get(0).play();
            }else if(sound.getStatus()==SoundStatus.PLAYING){
                status=SoundStatus.PLAYING;
            }
        }
    }

    public void toggle() {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        if (status == SoundStatus.PLAYING) {
            pause();
        } else if (status == SoundStatus.READY) {
            play();
        }
    }

    public void pause() {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        if (queue.size() > 0) {
            Sound sound = queue.get(0);
            if (sound.getStatus() == SoundStatus.PLAYING) {
                sound.pause();
                this.status = SoundStatus.READY;
            }
        }
    }

    public SoundStatus getStatus() {
        return status;
    }

    public void addVolume(float value) {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        if (queue.size() > 0) {
            if (queue.get(0).getStatus() == SoundStatus.PLAYING) {
                queue.get(0).addVolume(value);
            }
        }
    }

    public void setLoop(boolean loop) {
        if (this.status == SoundStatus.FAILED)
            throw new IllegalStateException("Could not initialise SoundController");
        this.loop = loop;
    }
}
