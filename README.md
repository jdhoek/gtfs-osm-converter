# GTFS to OSM Converter

Converts [GTFS](https://developers.google.com/transit/gtfs/) data to OpenStreetMap XML.

## Status

Alpha — this is just an experiment. Documentation is lacking, command line output is verbose 
and messy, and functionality is limited.

## Build

Clone this repo and run `mvn clean install`. This will create an executable jar here: 
`target/gtfs-converter-1.0-SNAPSHOT-full-jar-with-dependencies.jar`.

Requires Maven and JDK 1.8+.

## Run

Download a set of GTFS files, and from that directory run:

```bash
# Show some help. Don't expect too much at this stage. :)
java -jar PATH-TO-JAR

# Generate OSM XML with all routes in the GTFS set. This may be a lot of data!
java -jar PATH-TO-JAR routes-in-area -o test.osm

# Generate OSM XML with all routes that have at least one stop within this bounding area,
# results are limited to lines operated by 'ARR' (Arriva).
java -jar PATH-TO-JAR routes-in-area -W 5.79 -S 53.194 -E 5.795 -N 53.197 -o test.osm -a ARR
```

Replace `PATH-TO-JAR` with the fill path to the jar file build earlier.

## Bugs

Probably.
