package nl.jeroenhoek.osm.gtfs.osm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import nl.jeroenhoek.osm.gtfs.model.Stop;
import nl.jeroenhoek.osm.gtfs.model.type.Coordinate;

public class OSMDrawing {
    final Document document;
    final Element root;
    int idPos = 0;

    public OSMDrawing() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        document = docBuilder.newDocument();
        root = document.createElement("osm");
        root.setAttribute("version", "0.6");
        root.setAttribute("generator", "gtfs-converter 0.1");
        document.appendChild(root);
    }

    public void addStops(Collection<Stop> stops) {
        for (Stop stop : stops) {
            Element node = document.createElement("node");
            node.setAttribute("id", getId());
            node.setAttribute("lat", stop.getLatitude().toString());
            node.setAttribute("lon", stop.getLongitude().toString());
            addTag(node, "name", stop.getName());
            addTag(node, "highway", "bus_stop");
            addTag(node, "public_transport", "platform");
            addTag(node, "ref", stop.getId());
            root.appendChild(node);
        }
    }

    public void addShapes(Map<List<Coordinate>, Set<String>> values) {
        values.forEach((coordinates, strings) -> {
            List<String> nodeIds = new ArrayList<>();
            for (Coordinate coordinate : coordinates) {
                String id = getId();
                nodeIds.add(id);
                Element node = document.createElement("node");
                node.setAttribute("id", id);
                node.setAttribute("lat", coordinate.getLatitude().toString());
                node.setAttribute("lon", coordinate.getLongitude().toString());
                root.appendChild(node);
            }

            Element way = document.createElement("way");
            way.setAttribute("id", getId());
            for (String nodeId : nodeIds) {
                Element nodeRef = document.createElement("nd");
                nodeRef.setAttribute("ref", nodeId);
                way.appendChild(nodeRef);
            }
            addTag(way, "ref", strings.stream().collect(Collectors.joining(",")));

            root.appendChild(way);
        });
    }

    public void save(File file) {
        System.out.println("Saving to " + file.toString());

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    void addTag(Element node, String key, String value) {
        Element tag = document.createElement("tag");
        tag.setAttribute("k", key);
        tag.setAttribute("v", value);
        node.appendChild(tag);
    }

    String getId() {
        int id = --idPos;
        return String.valueOf(id);
    }
}
