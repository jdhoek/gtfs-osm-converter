package nl.jeroenhoek.osm.gtfs;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GtfsConverter {
    static ServiceLoader<Action> actionLoader = ServiceLoader.load(Action.class);

    public static void main(String[] args) {

        Options options = GtfsConverterCliOptions.options();

        try {
            Action action = parseAction(args);
            CommandLine arguments = GtfsConverterCliOptions.parseOptions(args, options);
            Map<GtfsTable, Path> gtfsPaths = listFiles(Paths.get("."));
            TransportModel transportModel = new TransportModel();
            CsvReader csvReader = new CsvReader(gtfsPaths, transportModel);
            action.perform(arguments, transportModel, csvReader);
        } catch (InvalidOptionsException e) {
            showHelp(options, e.getExitCode());
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    static void showHelp(Options options, int exitCode) {
        // Automatically generate the help statement.
        HelpFormatter formatter = new HelpFormatter();
        StringBuilder builder = new StringBuilder();
        builder.append("Supported actions:\n\n");
        for (Action action : actionLoader) {
            builder.append(action.name()).append("\n");
        }
        builder.append("\n");

        formatter.printHelp("gtfs-converter", builder.toString(), options, null, true);

        System.exit(exitCode);
    }

    static Map<GtfsTable, Path> listFiles(Path path) throws IOException {
        Map<GtfsTable, Path> gtfsPaths = new HashMap<>();
        Files.list(path)
                .filter(p -> p.getFileName().toString().endsWith(".txt"))
                .forEach(p -> addIfGtfsTableSource(p, gtfsPaths));
        return gtfsPaths;
    }

    static void addIfGtfsTableSource(Path path, Map<GtfsTable, Path> gtfsPaths) {
        Optional<GtfsTable> optionalGtfsTable = GtfsTable.fromPath(path);
        if (optionalGtfsTable.isPresent()) {
            gtfsPaths.put(optionalGtfsTable.get(), path);
        }
    }

    private static Action parseAction(String[] args) {
        if (args.length == 0) throw InvalidOptionsException.withGenericError();
        String name = args[0];
        for (Action action : actionLoader) {
            if (action.name().equals(name)) return action;
        }

        throw InvalidOptionsException.withGenericError();
    }
}
