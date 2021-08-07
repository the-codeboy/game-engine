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

    private AudioFormat format;

    public static Sound play(String path){
        try {
            Sound sound=new Sound(path,AudioSystem.getClip());
            sound.play();
            return sound;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Sound(Sound sound) {
        this(sound.path, sound.clip, sound.runAfter);
    }

    public Sound(String path, Clip clip) {
        this(path, clip, () -> {
        });
    }

    public Sound(String path, Clip clip, Runnable runAfter) {
        this.path = path;
        this.clip = clip;
        this.runAfter = runAfter;
        File file = new File(path);
        if (file.exists()) {
            runBefore = () -> init(getInputStream(file));
        } else {
            InputStream stream = getClass().getResourceAsStream("/Sounds/" + path);
            if (stream != null) {
                InputStream finalStream = stream;
                runBefore = () -> init(getInputStream(finalStream));
            } else {
                stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    InputStream finalStream1 = stream;
                    runBefore = () -> init(getInputStream(finalStream1));
                } else {
                    throw new IllegalArgumentException("File must exist");
                }
            }
        }
    }

    private AudioInputStream getInputStream(File file) {
        try {
            return AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AudioInputStream getInputStream(InputStream stream) {
        try {
            return AudioSystem.getAudioInputStream(stream);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void init(AudioInputStream audioStream) {
        try {
            if (audioStream == null) {
                status = SoundStatus.FAILED;
                System.err.println("Error");
                runAfter.run();
                return;
            }
            if (clip.isRunning()) {
                clip.stop();
            }
            if (clip.isOpen()) {
                clip.close();
            }
            clip.open(audioStream);
            format = audioStream.getFormat();
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainStartValue = gainControl.getValue();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (status == SoundStatus.FAILED) {
            throw new IllegalStateException("Can not run song which was not loaded properly");
        }
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
        float newValue = Math.max(gainControl.getMinimum(), Math.min(gainControl.getValue() + value, gainControl.getMaximum()));
        try {
            gainControl.setValue(newValue);
        } catch (IllegalArgumentException ignored) { // Should not happen thanks to Math.max(Math.min())
        }
    }

    public void stop() {
        clip.setMicrosecondPosition(clip.getMicrosecondLength());
    }

    public AudioFormat getFormat() {
        return format;
    }

    public void setStatus(SoundStatus status) {
        this.status = status;
    }
}
