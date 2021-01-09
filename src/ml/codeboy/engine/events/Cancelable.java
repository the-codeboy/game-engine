package ml.codeboy.engine.events;

public interface Cancelable {
    boolean isCanceled();
    void setCanceled(boolean canceled);
}
