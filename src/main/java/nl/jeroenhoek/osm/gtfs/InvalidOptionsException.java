package nl.jeroenhoek.osm.gtfs;

import java.util.function.Consumer;

public class InvalidOptionsException extends RuntimeException {
    int exitCode;
    String message;

    private InvalidOptionsException(int exitCode, String message) {
        this.exitCode = exitCode;
        this.message = message;
    }

    public static InvalidOptionsException withGenericError() {
        return new InvalidOptionsException(1, null);
    }

    public static InvalidOptionsException withGenericError(String message) {
        return new InvalidOptionsException(1, message);
    }

    public static InvalidOptionsException noError() {
        return new InvalidOptionsException(0, null);
    }

    public static InvalidOptionsException noError(String message) {
        return new InvalidOptionsException(0, message);
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void ifMessage(Consumer<String> consumer) {
        if (message != null) {
            consumer.accept(message);
        }
    }
}
