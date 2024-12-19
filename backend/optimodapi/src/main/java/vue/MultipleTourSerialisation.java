package vue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import com.google.gson.JsonSyntaxException; // Pour les erreurs liées à Gson
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metier.Warehouse;
import metier.DeliveryRequest;
import metier.Intersection;
import metier.Courier;
import metier.Tour;
import metier.TourRequest;  // Si nécessaire, inclure TourRequest pour sa sérialisation

/**
 *
 * @author jnoukam
 */
public class MultipleTourSerialisation extends Serialisation {

    @Override
    public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Récupération des données de l'attribut de la requête
        HashMap<Long, Courier> couriers = (HashMap<Long, Courier>) request.getAttribute("couriers");
        Boolean success = (Boolean) request.getAttribute("success");

        JsonObject container = new JsonObject();
        JsonArray couriersJsonArray = new JsonArray();

        if (couriers != null) {
            // Parcourir tous les couriers pour les sérialiser en JSON
            for (Map.Entry<Long, Courier> entry : couriers.entrySet()) {
                Courier courier = entry.getValue();
                JsonObject courierJson = new JsonObject();
                courierJson.addProperty("id", courier.getId());  // Assurez-vous que Courier a un getId() défini
                courierJson.addProperty("isAvailable", courier.getIsAvailable());  // Sérialisation de la disponibilité

                // Sérialisation du TourRequest (si nécessaire)
                TourRequest tourRequest = (TourRequest)courier.getTourRequest();
                JsonObject tourRequestJson = serializeTourRequest(tourRequest);

                // Sérialisation du deliveryPlan (Tour)
                Tour tour = (Tour)courier.getDeliveryPlan();  
                JsonObject tourJson = serializeTour(tour);

                courierJson.add("tourRequest", tourRequestJson);
                courierJson.add("tour", tourJson);
                couriersJsonArray.add(courierJson);
            }
        }

        // Ajouter le succès et les couriers au container
        container.addProperty("success", success);
        container.add("couriers", couriersJsonArray);

        // Envoyer la réponse JSON
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(container.toString());
        out.close();
    }

    private JsonObject serializeTourRequest(TourRequest tourRequest) {
        JsonObject tourRequestJson = new JsonObject();
        if ( tourRequest!= null) {
            // Ajouter les propriétés de TourRequest, par exemple un id ou des détails supplémentaires
            JsonObject warehouseJson = new JsonObject();
            Warehouse warehouse = tourRequest.getWarehouse();
            if (warehouse != null) {
                    warehouseJson.addProperty("id", warehouse.getId());
                    if (warehouse.getDepartureTime() != null) {
                            warehouseJson.addProperty("departureTime", warehouse.getDepartureTime().toString());
                    }
            }

            JsonArray deliveryRequestsArray = new JsonArray();
            Map<String, DeliveryRequest> requests = (Map<String, DeliveryRequest>) tourRequest.getRequests();

            for (String id : requests.keySet()) {
                DeliveryRequest deliveryRequest = requests.get(id);

                JsonObject json = new JsonObject();
                json.addProperty("id", deliveryRequest.getId());
                json.addProperty("pickup-point", deliveryRequest.getPickupPoint());
                json.addProperty("delivery-point", deliveryRequest.getDeliveryPoint());

                if (deliveryRequest.getPickupDuration() != null) {
                        json.addProperty("pickup-duration", deliveryRequest.getPickupDuration().getSeconds());
                }

                if (deliveryRequest.getDeliveryDuration() != null) {
                        json.addProperty("delivery-duration", deliveryRequest.getDeliveryDuration().getSeconds());
                }

                if (deliveryRequest.getPickupTime() != null) {
                        json.addProperty("pickup-time", deliveryRequest.getPickupTime().toString());
                }

                if (deliveryRequest.getDeliveryTime() != null) {
                        json.addProperty("delivery-time", deliveryRequest.getDeliveryTime().toString());
                }

                if (deliveryRequest.getDuration() != null) {
                        json.addProperty("duration", deliveryRequest.getDuration().getSeconds());
                }

                deliveryRequestsArray.add(json);
            }

            tourRequestJson.addProperty("id", tourRequest.getId());
            tourRequestJson.add("warehouse", warehouseJson);
            tourRequestJson.add("deliveryRequests", deliveryRequestsArray);
        }
        return tourRequestJson;
    }

    private JsonObject serializeTour(Tour tour) {
        JsonObject tourJson = new JsonObject();
        if (tour != null) {
            if (tour.getId() != null) {
                    tourJson.addProperty("id", tour.getId());
            }

            JsonArray intersectionJsonArray = new JsonArray();
            List<Intersection> intersections = tour.getPointslist();
            if (intersections != null) {
                for (Intersection intersection : intersections) {
                    JsonObject intersectionJson = new JsonObject();
                    intersectionJson.addProperty("id", intersection.getId());

                    JsonObject location = new JsonObject();
                    location.addProperty("latitude", intersection.getLocation().getLatitude());
                    location.addProperty("longitude", intersection.getLocation().getLongitude());
                    intersectionJson.add("location", location);

                    intersectionJsonArray.add(intersectionJson);
                }
            }
            tourJson.add("intersections", intersectionJsonArray);

            // Convert duration
            if (tour.getDuration() != null) {
                    tourJson.addProperty("duration", tour.getDuration().getSeconds());
            }
        }
        return tourJson;
    }
}
