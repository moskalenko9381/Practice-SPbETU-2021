package spbetu.prim.loggers;

public interface ILogger {

    <T> void info(T message);

    void clear();
}