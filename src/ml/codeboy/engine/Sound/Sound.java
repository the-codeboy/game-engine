package ml.codeboy.engine.Sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Sound {

    private SoundStatus status = SoundStatus.LOADING;
    private Clip clip;
    private FloatControl gainControl;

    protected Sound(String path) {
        if (new File(path).exists()) {
            init(new File(path));
            return;
        } else {
            InputStream stream = getClass().getResourceAsStream("/Sounds/" + path);
            if (stream != null) {
                init(stream);
                return;
            } else {
                stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    init(stream);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("File must exist");
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
        status = SoundStatus.READY;
    }

    public void play() {
        if (status == SoundStatus.READY || status == SoundStatus.PAUSED) {
            status = SoundStatus.PLAYING;
            clip.start();
            clip.addLineListener(event -> {
                if(event.getType()==LineEvent.Type.STOP){
                    if(clip.getMicrosecondPosition()==clip.getMicrosecondLength()) {
                        status = SoundStatus.STOPPED;
                        SoundController.getInstance().finishedPlaying(this);
                    }else{
                        status = SoundStatus.PAUSED;
                    }
                }
            });
        }
    }

    public void pause() {
        if(status==SoundStatus.PLAYING){
            clip.stop();
        }
    }

    public SoundStatus getStatus(){
        return status;
    }
}
