package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.model.Agency;
import nl.jeroenhoek.osm.gtfs.model.Route;
import nl.jeroenhoek.osm.gtfs.model.Stop;
import org.apache.commons.cli.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

public class GtfsConverterCliOptions {
    public static Options options() {
        Options options = new Options();
        Option agencies = new Option("a", "agencies", true, "Agencies to filter on.");
        agencies.setArgName("agencies");
        Option routes = new Option("r", "routes", true, "Routes to filter on.");
        routes.setArgName("routes");
        Option northBound = new Option("n", "lat-max", true, "Maximum latitude.");
        northBound.setArgName("lat");
        Option southBound = new Option("s", "lat-min", true, "Minimum latitude.");
        southBound.setArgName("lat");
        Option westBound = new Option("w", "lon-min", true, "Minimum latitude.");
        westBound.setArgName("lon");
        Option eastBound = new Option("e", "lon-max", true, "Maximum latitude.");
        eastBound.setArgName("lon");

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
        options.addOption(northBound);
        options.addOption(southBound);
        options.addOption(westBound);
        options.addOption(eastBound);
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

    public static List<Predicate<Agency>> agencyFilterFromOptions(CommandLine arguments) {
        List<String> agencyFilterList = GtfsConverterCliOptions.listFromArguments(arguments, "agencies");
        List<Predicate<Agency>> filters;
        if (agencyFilterList != null) {
            filters = Collections.singletonList(agency -> {
                for (String inList : agencyFilterList) {
                    if (inList.equals(agency.getId()) || inList.equals(agency.getName())) return true;
                }
                return false;
            });
        } else {
            filters = Collections.emptyList();
        }

        return filters;
    }

    public static List<Predicate<Route>> routeFilterFromOptions(CommandLine arguments) {
        List<String> routeFilterList = GtfsConverterCliOptions.listFromArguments(arguments, "routes");
        List<Predicate<Route>> filters = new ArrayList<>();

        filters.add(route -> route.getAgency() != null);
        if (routeFilterList != null) {
            filters.add(route -> {
                for (String inList : routeFilterList) {
                    if (inList.equals(route.getId())) return true;
                }
                return false;
            });
        }

        return filters;
    }


    public static List<Predicate<Stop>> stopFilterFromOptions(CommandLine arguments) {
        Optional<BigDecimal> latMin = bigDecimalFromOption(arguments, "lat-min");
        Optional<BigDecimal> latMax = bigDecimalFromOption(arguments, "lat-max");
        Optional<BigDecimal> lonMin = bigDecimalFromOption(arguments, "lon-min");
        Optional<BigDecimal> lonMax = bigDecimalFromOption(arguments, "lon-max");

        List<Predicate<Stop>> filters = new ArrayList<>();
        latMin.ifPresent(v -> filters.add(stop -> stop.getLatitude().compareTo(v) > 0));
        latMax.ifPresent(v -> filters.add(stop -> stop.getLatitude().compareTo(v) < 0));
        lonMin.ifPresent(v -> filters.add(stop -> stop.getLongitude().compareTo(v) > 0));
        lonMax.ifPresent(v -> filters.add(stop -> stop.getLongitude().compareTo(v) < 0));

        return filters;
    }

    public static List<String> listFromArguments(CommandLine arguments, String key) {
        String[] args = arguments.getOptionValues(key);
        if (args != null) {
            return Arrays.asList(args);
        }

        return null;
    }

    public static Optional<BigDecimal> bigDecimalFromOption(CommandLine arguments, String key) {
        String value = arguments.getOptionValue(key);
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new BigDecimal(value));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Input value for argument " + key + " is not a decimal value: " + value);
        }
    }
}