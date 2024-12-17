package util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parses an XML file containing tour data and returns a JSON representation.
 *
 * @author jassir
 */
public class XmlTourParser implements FileParser<JsonObject> {

    @Override
    public JsonObject parse(File file) {
        JsonObject result = new JsonObject();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            // Main JSON objects
            JsonArray toursArray = new JsonArray();
            JsonObject typePoints = new JsonObject();
            JsonArray pickupPointsArray = new JsonArray();
            JsonArray deliveryPointsArray = new JsonArray();
            JsonObject warehouseObject = new JsonObject();

            // Parse tours
            NodeList toursNode = document.getElementsByTagName("tour");
            for (int i = 0; i < toursNode.getLength(); i++) {
                Element tour = (Element) toursNode.item(i);
                JsonObject tourObject = new JsonObject();
                JsonArray intersectionsArray = new JsonArray();

                // Add tour attributes
                String id = tour.getAttribute("id");
                Long duration = Long.parseLong(tour.getAttribute("duration"));
                tourObject.addProperty("id", id);
                tourObject.addProperty("duration", duration);

                // Parse intersections in the tour
                NodeList intersectionsNode = tour.getElementsByTagName("intersection");
                for (int j = 0; j < intersectionsNode.getLength(); j++) {
                    Element intersection = (Element) intersectionsNode.item(j);
                    JsonObject intersectionObject = new JsonObject();

                    String intersectionId = intersection.getAttribute("id");
                    Double latitude = Double.parseDouble(intersection.getElementsByTagName("latitude").item(0).getTextContent());
                    Double longitude = Double.parseDouble(intersection.getElementsByTagName("longitude").item(0).getTextContent());
                    intersectionObject.addProperty("latitude", latitude);
                    intersectionObject.addProperty("longitude", longitude);
                    intersectionObject.addProperty("id", intersectionId);
                    intersectionsArray.add(intersectionObject);
                }

                tourObject.add("intersections", intersectionsArray);
                toursArray.add(tourObject);
            }

            // Parse type points (pickup, delivery, warehouse)
            NodeList typeNode = document.getElementsByTagName("typePoints");
            for (int i = 0; i < typeNode.getLength(); i++) {
                Element type = (Element) typeNode.item(i);

                // Parse pickup points
                NodeList pickupNode = type.getElementsByTagName("pickupPoints");
                for (int j = 0; j < pickupNode.getLength(); j++) {
                    Element pickup = (Element) pickupNode.item(j);
                    NodeList intersectionsNode = pickup.getElementsByTagName("intersection");
                    for (int k = 0; k < intersectionsNode.getLength(); k++) {
                        Element intersection = (Element) intersectionsNode.item(k);
                        JsonObject intersectionObject = new JsonObject();

                        String intersectionId = intersection.getAttribute("id");
                        Double latitude = Double.parseDouble(intersection.getElementsByTagName("latitude").item(0).getTextContent());
                        Double longitude = Double.parseDouble(intersection.getElementsByTagName("longitude").item(0).getTextContent());
                        intersectionObject.addProperty("latitude", latitude);
                        intersectionObject.addProperty("longitude", longitude);
                        intersectionObject.addProperty("id", intersectionId);

                        pickupPointsArray.add(intersectionObject);
                    }
                }

                // Parse delivery points
                NodeList deliveryNode = type.getElementsByTagName("deliveryPoints");
                for (int j = 0; j < deliveryNode.getLength(); j++) {
                    Element delivery = (Element) deliveryNode.item(j);
                    NodeList intersectionsNode = delivery.getElementsByTagName("intersection");
                    for (int k = 0; k < intersectionsNode.getLength(); k++) {
                        Element intersection = (Element) intersectionsNode.item(k);
                        JsonObject intersectionObject = new JsonObject();

                        String intersectionId = intersection.getAttribute("id");
                        Double latitude = Double.parseDouble(intersection.getElementsByTagName("latitude").item(0).getTextContent());
                        Double longitude = Double.parseDouble(intersection.getElementsByTagName("longitude").item(0).getTextContent());
                        intersectionObject.addProperty("latitude", latitude);
                        intersectionObject.addProperty("longitude", longitude);
                        intersectionObject.addProperty("id", intersectionId);

                        deliveryPointsArray.add(intersectionObject);
                    }
                }

                // Parse warehouse points
                NodeList warehouseNode = type.getElementsByTagName("warehousePoint");
                if (warehouseNode.getLength() > 0) {
                    Element warehouse = (Element) warehouseNode.item(0); // Prend le premier élément
                    NodeList intersectionsNode = warehouse.getElementsByTagName("intersection");
                    if (intersectionsNode.getLength() > 0) {
                        Element intersection = (Element) intersectionsNode.item(0);
                        String intersectionId = intersection.getAttribute("id");
                        Double latitude = Double.parseDouble(intersection.getElementsByTagName("latitude").item(0).getTextContent());
                        Double longitude = Double.parseDouble(intersection.getElementsByTagName("longitude").item(0).getTextContent());

                        warehouseObject.addProperty("id", intersectionId);
                        warehouseObject.addProperty("latitude", latitude);
                        warehouseObject.addProperty("longitude", longitude);
                    }
                }
            }

            // Add type points to the result
            typePoints.add("pickupPoints", pickupPointsArray);
            typePoints.add("deliveryPoints", deliveryPointsArray);
            typePoints.add("warehousePoint", warehouseObject);

            // Add tours and type points to the final result
            result.add("tours", toursArray);
            result.add("typePoints", typePoints);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
