package com.webinarnttdata.auth.domain.patterns;

import org.jboss.logging.Logger;

public class AppLogger {
    private static final Logger LOGGER = Logger.getLogger("Auth-Service");

    private AppLogger() {}

    public static Logger getInstance() {
        return LOGGER;
    }

    // Métodos helper para colores
    public static String colorInfo(String message) {
        return "\u001B[36m" + message + "\u001B[0m"; // Cyan
    }

    public static String colorWarning(String message) {
        return "\u001B[33m" + message + "\u001B[0m"; // Amarillo
    }

    public static String colorError(String message) {
        return "\u001B[31m" + message + "\u001B[0m"; // Rojo
    }

    public static String colorSuccess(String message) {
        return "\u001B[32m" + message + "\u001B[0m"; // Verde
    }
}
