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
        Option stops = new Option("s", "stops", true, "Stops to filter on.");
        stops.setArgName("stops");

        Option northBound = new Option("N", "lat-max", true, "Maximum latitude.");
        northBound.setArgName("lat");
        Option southBound = new Option("S", "lat-min", true, "Minimum latitude.");
        southBound.setArgName("lat");
        Option westBound = new Option("W", "lon-min", true, "Minimum latitude.");
        westBound.setArgName("lon");
        Option eastBound = new Option("E", "lon-max", true, "Maximum latitude.");
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
        options.addOption(stops);
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

    public static Predicate<Agency> agencyFilterFromOptions(CommandLine arguments) {
        List<String> agencyFilterList = GtfsConverterCliOptions.listFromArguments(arguments, "agencies");

        Predicate<Agency> filter;
        if (agencyFilterList != null) {
            filter = agency -> agencyFilterList.contains(agency.getId());
            filter.or(agency -> agencyFilterList.contains(agency.getName()));
        } else {
            filter = agency -> true;
        }

        return filter;
    }

    public static Predicate<Route> routeFilterFromOptions(CommandLine arguments) {
        List<String> routeFilterList = GtfsConverterCliOptions.listFromArguments(arguments, "routes");

        Predicate<Route> filter = route -> route.getAgency() != null;
        if (routeFilterList != null) {
            filter.and(route -> routeFilterList.contains(route.getId()));
        }

        return filter;
    }

    public static Predicate<Stop> stopFilterFromOptions(CommandLine arguments) {
        Optional<BigDecimal> latMin = bigDecimalFromOption(arguments, "lat-min");
        Optional<BigDecimal> latMax = bigDecimalFromOption(arguments, "lat-max");
        Optional<BigDecimal> lonMin = bigDecimalFromOption(arguments, "lon-min");
        Optional<BigDecimal> lonMax = bigDecimalFromOption(arguments, "lon-max");

        List<String> stopFilterList = GtfsConverterCliOptions.listFromArguments(arguments, "stops");
        Predicate<Stop> filter = stopFilterList != null
                ? stop -> stopFilterList.contains(stop.getId())
                : stop -> true;

        String minHigherThanMax = "Maximum %1$s cannot be higher than the minimum %1$s (%2$s, %3$s)";
        latMin.ifPresent(l -> latMax.ifPresent(h -> {
            if (l.compareTo(h) > 0) {
                throw InvalidOptionsException.withGenericError(String.format(minHigherThanMax, "latitude", l, h));
            }
        }));
        lonMin.ifPresent(l -> lonMax.ifPresent(h -> {
            if (l.compareTo(h) > 0) {
                throw InvalidOptionsException.withGenericError(String.format(minHigherThanMax, "longitude", l, h));
            }
        }));

        if (latMin.isPresent()) {
            filter = filter.and(stop -> stop.getLatitude().compareTo(latMin.get()) > 0);
        }
        if (latMax.isPresent()) {
            filter = filter.and(stop -> stop.getLatitude().compareTo(latMax.get()) < 0);
        }
        if (lonMin.isPresent()) {
            filter = filter.and(stop -> stop.getLongitude().compareTo(lonMin.get()) > 0);
        }
        if (lonMax.isPresent()) {
            filter = filter.and(stop -> stop.getLongitude().compareTo(lonMax.get()) < 0);
        }

        return filter;
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