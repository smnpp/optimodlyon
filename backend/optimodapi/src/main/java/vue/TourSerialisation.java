/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vue;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metier.DeliveryRequest;
import metier.Intersection;
import metier.Tour;
import metier.TourRequest;
import metier.Warehouse;

/**
 *
 * @author wockehs
 */
public class TourSerialisation extends Serialisation {

	@Override
	public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Tour tour = (Tour) request.getAttribute("tour");
		Boolean success = (Boolean) request.getAttribute("success");

		JsonObject container = new JsonObject();

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

		TourRequest tourRequest = (TourRequest)request.getAttribute("tourRequest");
		JsonObject tourRequestJson = serializeTourRequest(tourRequest);
                
		// Add success and tour to the container
		container.addProperty("success", success);
		container.add("tour", tourJson);
		container.add("tourRequest", tourRequestJson);

		// Send the JSON response
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

}
