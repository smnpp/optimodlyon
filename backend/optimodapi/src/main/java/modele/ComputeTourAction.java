/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modele;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import metier.DeliveryRequest;
import metier.Map;
import metier.Tour;
import metier.TourRequest;
import metier.Warehouse;
import service.Service;

/**
 *
 * @author wockehs
 */
public class ComputeTourAction extends Action {

	
	public ComputeTourAction(Service service) {
		super(service);
	}
	
	public void execute(HttpServletRequest request) {
		BufferedReader reader = null;
		try {
			reader = request.getReader();
		} catch (IOException ex) {
			Logger.getLogger(ChargerMapAction.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}

		Gson gson = new Gson();
		JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

		try {
			// Extract map data from JSON
			String mapFile = jsonRequest.get("map-file").getAsString();
			Map map = service.loadMap(mapFile);

			// Extract request object
			JsonObject requestObject = jsonRequest.getAsJsonObject("request");

			// Parse warehouse
			JsonObject warehouseJson = requestObject.getAsJsonObject("warehouse");
			Long warehouseId = warehouseJson.get("key").getAsLong();
			LocalTime departureTime = LocalTime.now(); // Default or configurable
			Warehouse warehouse = new Warehouse(warehouseId, departureTime);

			// Parse requests
			JsonArray requestsJson = requestObject.getAsJsonArray("request");
			HashMap<String, DeliveryRequest> requestsMap = new HashMap<>();

			for (JsonElement reqElement : requestsJson) {
				JsonObject reqObject = reqElement.getAsJsonObject();

				// Extract pickup and delivery points
				Long pickupPoint = reqObject.getAsJsonObject("pickupPoint").get("key").getAsLong();
				Long deliveryPoint = reqObject.getAsJsonObject("deliveryPoint").get("key").getAsLong();

				// Extract durations
				int pickupDurationSeconds = reqObject.get("pickupDuration").getAsInt();
				int deliveryDurationSeconds = reqObject.get("deliveryDuration").getAsInt();

				Duration pickupDuration = Duration.ofSeconds(pickupDurationSeconds);
				Duration deliveryDuration = Duration.ofSeconds(deliveryDurationSeconds);

				// Create and store DeliveryRequest
				DeliveryRequest deliveryRequest = new DeliveryRequest(pickupPoint, deliveryPoint, pickupDuration, deliveryDuration);
				requestsMap.put(deliveryRequest.getId(), deliveryRequest);
			}

			// Create TourRequest with parsed warehouse and delivery requests
			TourRequest tourRequest = new TourRequest(requestsMap, warehouse);

			// Compute the tour using the service
			Tour tour = service.computeTour(tourRequest, map);

			// Set attributes for the response
			request.setAttribute("success", true);
			request.setAttribute("tour", tour);

		} catch (IOException ex) {
			Logger.getLogger(ComputeTourAction.class.getName()).log(Level.SEVERE, null, ex);
			request.setAttribute("success", false);
			request.setAttribute("error", "Failed to process request.");
		} catch (Exception ex) {
			Logger.getLogger(ComputeTourAction.class.getName()).log(Level.SEVERE, null, ex);
			request.setAttribute("success", false);
			request.setAttribute("error", "Unexpected error occurred.");
		}
	}


	
}
