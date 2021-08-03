package ml.codeboy.engine.Sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Sound {

    private SoundStatus status = SoundStatus.LOADING;
    private String path;
    private Clip clip;
    private FloatControl gainControl;
    private Runnable runAfter;
    private Runnable runBefore;
    private float gainStartValue;

    private boolean useCache;

    public Sound(Sound sound){
        this(sound.path, sound.clip,sound.runAfter);
    }

    public Sound(String path, Clip clip) {
        this(path, clip, () -> {
        });
    }

    public Sound(String path, Clip clip, Runnable runAfter) {
        this.path=path;
        this.clip = clip;
        this.runAfter = runAfter;
        File file = new File(path);
        if (file.exists()) {
            runBefore = () -> init(file);
        } else {
            InputStream stream = getClass().getResourceAsStream("/Sounds/" + path);
            if (stream != null) {
                InputStream finalStream = stream;
                runBefore = () -> init(finalStream);
            } else {
                stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    InputStream finalStream1 = stream;
                    runBefore = () -> init(finalStream1);
                } else {
                    throw new IllegalArgumentException("File must exist");
                }
            }
        }
    }

    private void init(File file) {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
            clip.open(inputStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
        postInit();
    }

    private void init(InputStream stream) {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
            clip.stop();
            clip.close();
            clip.open(inputStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
        postInit();
    }

    private void postInit() {
        this.gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainStartValue = gainControl.getValue();
        status = SoundStatus.READY;
    }

    public void play() {
        if (status == SoundStatus.LOADING || status == SoundStatus.PAUSED) {
            if (status == SoundStatus.LOADING)
                runBefore.run();
            status = SoundStatus.PLAYING;
            clip.start();
            clip.addLineListener(event -> {
                System.out.println(event.getType());
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
        float newValue = Math.min(gainControl.getValue() + value, gainControl.getMaximum());
        try {
            gainControl.setValue(newValue);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        clip.setMicrosecondPosition(clip.getMicrosecondLength());
    }

    public void setStatus(SoundStatus status) {
        this.status = status;
    }
}
