/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modele;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import com.google.gson.JsonSyntaxException; // Pour les erreurs liées à Gson
import java.time.Duration;
import java.time.LocalTime;
import javax.servlet.http.HttpServletRequest;
import metier.Map;
import metier.TourRequest;
import metier.Courier;
import metier.DeliveryRequest;
import metier.Warehouse;
import service.Service;
/**
 *
 * @author jnoukam
 */
public class ComputeMultipleTourAction extends Action {
    
	/**
	 * @param service 
	 */
	public ComputeMultipleTourAction(Service service) {
		super(service);
	}
	/**
	 * Processes a JSON request to compute and assign delivery tours for multiple couriers.
	 * @param request 
	 * @throws IllegalArgumentException if any required parameter is missing or invalid.
	 */
	@Override
	public void execute(HttpServletRequest request) {
		// Initialiser les valeurs par défaut
		String fileMapContent = null;
		String fileRequestsContent = null;
		int numCouriers = 1; // Valeur par défaut

		try {
			// Récupération du contenu JSON
			BufferedReader reader = request.getReader();
			Gson gson = new Gson();
			JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

			// Vérification de la présence des clés et récupération des valeurs
			if (jsonRequest.has("map-file") && jsonRequest.has("request") && jsonRequest.has("num-couriers")) {
				fileMapContent = jsonRequest.get("map-file").getAsString();
				numCouriers = jsonRequest.get("num-couriers").getAsInt();

				if (numCouriers <= 0) {
					numCouriers = 1; // Si le nombre de livreurs est 0 ou négatif, mettre à 1
				}
				
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
			
				// Charger la carte et les requêtes
				Map map = service.loadMap(fileMapContent);
				// Calculer et assigner les tournées
				HashMap<Long, Courier> couriers = service.computeAndAssignTour(tourRequest, map, numCouriers);
				
				// Définir les attributs pour la réponse
				request.setAttribute("success", true);
				request.setAttribute("couriers", couriers);

			} else {
				request.setAttribute("success", false);
				request.setAttribute("message", "Missing required parameters.");
				return; // Arrêter l'exécution si des paramètres sont manquants
			}

		} catch (IOException | JsonSyntaxException ex) {
			// Gestion des erreurs de lecture ou de parsing
			Logger.getLogger(ComputeMultipleTourAction.class.getName()).log(Level.SEVERE, "Error processing request.", ex);
			request.setAttribute("success", false);
			request.setAttribute("message", "Error processing request.");
		}
	}


}
