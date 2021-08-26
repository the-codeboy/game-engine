package ml.codeboy.engine.Sound;

public enum SoundStatus {
    LOADING, READY, PLAYING, PAUSED, FAILED, STOPPED;

    public boolean isPlayable() {
        return this == LOADING || this == READY || this == PAUSED;
    }
}
