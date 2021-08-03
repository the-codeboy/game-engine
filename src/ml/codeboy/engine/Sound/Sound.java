package ml.codeboy.engine.Sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Sound {

    private static final HashMap<String, Sound> cache = new HashMap<>();

    private SoundStatus status = SoundStatus.LOADING;
    private Clip clip;
    private FloatControl gainControl;
    private Runnable runAfter;
    private float gainStartValue;

    public static Sound createSound(String path) {
        return createSound(path, () -> {
        });
    }

    public static Sound createSound(String path, Runnable runAfter) {
        return createSound(path, runAfter, true);
    }

    public static Sound createSound(String path, Runnable runAfter, boolean useCache) {
        if (cache.containsKey(path)) {
            Sound sound = cache.get(path);
            if (sound.status == SoundStatus.STOPPED || sound.status == SoundStatus.READY) {
                sound.reset();
                sound.runAfter = runAfter;
                return sound;
            }
        }
        return new Sound(path, runAfter, useCache);
    }

    private Sound reset() {
        clip.setMicrosecondPosition(0);
        gainControl.setValue(gainStartValue);
        return this;
    }

    private Sound(String path, Runnable runAfter, boolean useCache) {
        this.runAfter = runAfter;
        File file = new File(path);
        if (file.exists()) {
            init(file);
        } else {
            InputStream stream = getClass().getResourceAsStream("/Sounds/" + path);
            if (stream != null) {
                init(stream);
            } else {
                stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    init(stream);
                } else {
                    throw new IllegalArgumentException("File must exist");
                }
            }
        }
        if (useCache) {
            cache.put(path, this);
        }
    }


    private void init(File file) {
        init();
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
            clip.open(inputStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
        postInit();
    }

    private void init(InputStream stream) {
        init();
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
            clip.open(inputStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
        postInit();
    }

    private void init() {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void postInit() {
        this.gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainStartValue = gainControl.getValue();
        status = SoundStatus.READY;
    }

    public void play() {
        if (status == SoundStatus.READY || status == SoundStatus.PAUSED) {
            status = SoundStatus.PLAYING;
            clip.start();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                        status = SoundStatus.STOPPED;
                        runAfter.run();
                    } else {
                        status = SoundStatus.PAUSED;
                    }
                }
            });
        }
    }

    public void pause() {
        if (status == SoundStatus.PLAYING) {
            clip.stop();
        }
    }

    public SoundStatus getStatus() {
        return status;
    }

    public void addVolume(float value) {
        float newValue = gainControl.getValue() + value;
        try {
            gainControl.setValue(newValue);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
