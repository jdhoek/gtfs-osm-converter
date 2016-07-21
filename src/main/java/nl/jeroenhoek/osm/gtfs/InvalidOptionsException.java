package nl.jeroenhoek.osm.gtfs;

public class InvalidOptionsException extends RuntimeException {
    int exitCode;

    private InvalidOptionsException(int exitCode) {
        this.exitCode = exitCode;
    }

    public static InvalidOptionsException withGenericError() {
        return new InvalidOptionsException(1);
    }

    public static InvalidOptionsException noError() {
        return new InvalidOptionsException(0);
    }

    public int getExitCode() {
        return exitCode;
    }
}
