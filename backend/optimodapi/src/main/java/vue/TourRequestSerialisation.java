/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metier.DeliveryRequest;
import metier.TourRequest;
import metier.Warehouse;

/**
 *
 * @author wockehs
 */
public class TourRequestSerialisation extends Serialisation {
	
	@Override
	public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		TourRequest tourRequest = (TourRequest) request.getAttribute("tour-request");
		Boolean success = (Boolean) request.getAttribute("success");

		JsonObject container = new JsonObject();

		JsonObject warehouseJson = new JsonObject();
		Warehouse warehouse = tourRequest.getWarehouse();
		if (warehouse != null) {
			warehouseJson.addProperty("id", warehouse.getId());
			if (warehouse.getDepartureTime() != null) {
				warehouseJson.addProperty("departureTime", warehouse.getDepartureTime().toString());
			}
		}

		JsonArray deliveryRequestsArray = new JsonArray();
		HashMap<String, DeliveryRequest> requests = (HashMap<String, DeliveryRequest>) tourRequest.getRequests();

		for (String id : requests.keySet()) {
			DeliveryRequest deliveryRequest = requests.get(id);

			JsonObject json = new JsonObject();
			json.addProperty("id", deliveryRequest.getId());
			json.addProperty("pickup-point", deliveryRequest.getPickupPoint());
			json.addProperty("delivery-point", deliveryRequest.getDeliveryPoint());

			if (deliveryRequest.getPickupDuration() != null) {
				json.addProperty("pickup-duration", deliveryRequest.getPickupDuration().toSeconds());
			}

			if (deliveryRequest.getDeliveryDuration() != null) {
				json.addProperty("delivery-duration", deliveryRequest.getDeliveryDuration().toSeconds());
			}

			if (deliveryRequest.getPickupTime() != null) {
				json.addProperty("pickup-time", deliveryRequest.getPickupTime().toString());
			}

			if (deliveryRequest.getDeliveryTime() != null) {
				json.addProperty("delivery-time", deliveryRequest.getDeliveryTime().toString());
			}

			if (deliveryRequest.getDuration() != null) {
				json.addProperty("duration", deliveryRequest.getDuration().toSeconds());
			}

			deliveryRequestsArray.add(json);
		}

		container.addProperty("success", success);
		container.addProperty("id", tourRequest.getId());
		container.add("warehouse", warehouseJson);
		container.add("delivery-requests", deliveryRequestsArray);

		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(container.toString());
		out.close();
	}
}
