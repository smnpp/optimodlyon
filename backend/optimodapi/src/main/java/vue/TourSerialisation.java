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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import metier.Intersection;
import metier.Tour;

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
				tourJson.addProperty("duration", tour.getDuration().toSeconds());
			}
		}

		// Add success and tour to the container
		container.addProperty("success", success);
		container.add("tour", tourJson);

		// Send the JSON response
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(container.toString());
		out.close();
	}
	
}
