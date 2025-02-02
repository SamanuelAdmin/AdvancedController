package org.controller.codetemplates;

public interface LoggerInterface {
    public default void log(String... args) {};
    public default void error(String message, Object... args) {};
}
