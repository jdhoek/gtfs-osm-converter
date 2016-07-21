package nl.jeroenhoek.osm.gtfs;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.List;

public class GtfsConverterCliOptions {
    public static Options options() {
        Options options = new Options();
        Option agencies = new Option("a", "agencies", true, "Agencies to filter on.");
        agencies.setArgName("agencies");
        Option routes = new Option("r", "routes", true, "Routes to filter on.");
        routes.setArgName("routes");

//        Option exceptions = new Option(null, "no-exceptions", false,
//                "do not attach exceptions to ERROR and FATAL exceptions");
//
//        Option minLevel = new Option("m", "min-level", true, "minimum log-level");
//        minLevel.setArgName("level");
//        Option maxLevel = new Option("M", "max-level", true, "maximum log-level");
//        maxLevel.setArgName("level");
//
//        Option logTypes = new Option("t", "types", true, "log types to generate");
//        logTypes.setArgName("types");
//
//        // One of these three options should be supplied, so they are grouped in an OptionGroup,
//        // and that group is made required.
//        Option help = new Option("h", "help", false, "print this message");
//        Option duration = new Option("d", "duration", true,
//                "generate logs for this long; `time' is a duration in seconds, minutes, or hours (e.g., 20s, 10m, 1h)");
//        duration.setArgName("time");
//        Option indefinite = new Option("g", "keep-going", false, "keep sending log-events indefinitely");
//        OptionGroup time = new OptionGroup();
//
//        time.addOption(help);
//        time.addOption(duration);
//        time.addOption(indefinite);
//        time.setRequired(true);
//
//        options.addOption(help);
        options.addOption(agencies);
        options.addOption(routes);
//        options.addOption(exceptions);
//        options.addOption(minLevel);
//        options.addOption(maxLevel);
//        options.addOption(logTypes);
//        options.addOptionGroup(time);

        return options;
    }

    public static CommandLine parseOptions(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        CommandLine arguments;

        // Show help when run without arguments.
        // One args string (empty) can happen when mvn exec:java is called with -Dexec.args="".
        if (args.length == 0 || (args.length == 1 && args[0].isEmpty())) {
            // Show help and exit successfully.
            throw InvalidOptionsException.noError();
        }

        try {
            arguments = parser.parse(options, args);
        } catch (ParseException e) {
            // These exceptions have very descriptive and user-friendly explanations of what
            // went wrong, so we use them directly.
            System.err.println(e.getMessage());
            System.err.println();
            throw InvalidOptionsException.withGenericError();
        }

        if (arguments.hasOption("help")) {
            // Show help and exit successfully.
            throw InvalidOptionsException.noError();
        }

        return arguments;
    }

    public static List<String> listFromArguments(CommandLine arguments, String key) {
        String[] args = arguments.getOptionValues(key);
        if (args != null) {
            return Arrays.asList(args);
        }

        return null;
    }
}