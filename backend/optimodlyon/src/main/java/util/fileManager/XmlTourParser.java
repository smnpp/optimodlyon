package util.fileManager;

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
            JsonArray deliveryRequestsArray = new JsonArray();
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

                // Parse deliveryRequests
                NodeList deliveryRequestsNode = type.getElementsByTagName("deliveryRequest");
                for (int a = 0; a < deliveryRequestsNode.getLength(); a++) {
                    JsonObject deliveryRequestObject = new JsonObject();
                    Element deliveryRequest = (Element) deliveryRequestsNode.item(a);

                    // Extract deliveryRequest attributes
                    String pickupDuration = deliveryRequest.getAttribute("pickupDuration");
                    String deliveryDuration = deliveryRequest.getAttribute("deliveryDuration");
                    String key = deliveryRequest.getAttribute("key");

                    deliveryRequestObject.addProperty("key", key);
                    deliveryRequestObject.addProperty("deliveryDuration", deliveryDuration);
                    deliveryRequestObject.addProperty("pickupDuration", pickupDuration);

                    // Parse pickupPoint
                    JsonObject pickupObject = new JsonObject();
                    Element pickupPoint = (Element) deliveryRequest.getElementsByTagName("pickupPoint").item(0);
                    if (pickupPoint != null) {
                        String pickupKey = pickupPoint.getAttribute("key");
                        pickupObject.addProperty("key", pickupKey);

                        Element pickLocation = (Element) pickupPoint.getElementsByTagName("location").item(0);
                        if (pickLocation != null) {
                            JsonObject pickLocationObject = new JsonObject();
                            Double latitude = Double.parseDouble(pickLocation.getElementsByTagName("latitude").item(0).getTextContent());
                            Double longitude = Double.parseDouble(pickLocation.getElementsByTagName("longitude").item(0).getTextContent());
                            pickLocationObject.addProperty("latitude", latitude);
                            pickLocationObject.addProperty("longitude", longitude);

                            pickupObject.add("location", pickLocationObject);
                        }
                    }
                    deliveryRequestObject.add("pickupPoint", pickupObject);

                    // Parse deliveryPoint
                    JsonObject deliveryObject = new JsonObject();
                    Element deliveryPoint = (Element) deliveryRequest.getElementsByTagName("deliveryPoint").item(0);
                    if (deliveryPoint != null) {
                        String deliveryKey = deliveryPoint.getAttribute("key");
                        deliveryObject.addProperty("key", deliveryKey);

                        Element deliveryLocation = (Element) deliveryPoint.getElementsByTagName("location").item(0);
                        if (deliveryLocation != null) {
                            JsonObject deliveryLocationObject = new JsonObject();
                            Double latitude = Double.parseDouble(deliveryLocation.getElementsByTagName("latitude").item(0).getTextContent());
                            Double longitude = Double.parseDouble(deliveryLocation.getElementsByTagName("longitude").item(0).getTextContent());
                            deliveryLocationObject.addProperty("latitude", latitude);
                            deliveryLocationObject.addProperty("longitude", longitude);

                            deliveryObject.add("location", deliveryLocationObject);
                        }
                    }
                    deliveryRequestObject.add("deliveryPoint", deliveryObject);

                    // Add the completed deliveryRequestObject to the array
                    deliveryRequestsArray.add(deliveryRequestObject);
                }

                // Parse warehousePoint
                NodeList warehouseNode = type.getElementsByTagName("warehousePoint");
                if (warehouseNode.getLength() > 0) {
                    Element warehouse = (Element) warehouseNode.item(0);
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
            typePoints.add("warehousePoint", warehouseObject);
            typePoints.add("deliveryRequests", deliveryRequestsArray);

            // Add tours and type points to the final result
            result.add("tours", toursArray);
            result.add("typePoints", typePoints);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
