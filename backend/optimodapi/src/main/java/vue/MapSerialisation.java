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
import metier.Intersection;
import metier.Map;

/**
 *
 * @author wockehs
 */
public class MapSerialisation extends Serialisation {

	@Override
	public void appliquer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map map = (Map) request.getAttribute("map");
		Boolean success = (Boolean) request.getAttribute("success");

		JsonObject container = new JsonObject();
		
		JsonArray intersectionsArray = new JsonArray();
		HashMap<Long, Intersection> intersections = map.getIntersections();

		for (Long id : intersections.keySet()) {
			Intersection intersection = intersections.get(id);

			// Create a JsonObject for each intersection
			JsonObject intersectionObject = new JsonObject();
			intersectionObject.addProperty("id", id);

			JsonObject location = new JsonObject();
			location.addProperty("latitude", intersection.getLocation().getLatitude());
			location.addProperty("longitude", intersection.getLocation().getLongitude());

			intersectionObject.add("location", location);
			intersectionsArray.add(intersectionObject);
		}

		// Add the success property and the intersections array to the container
		container.addProperty("success", success);
		container.add("map", intersectionsArray);

		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(container.toString());
		out.close();
	}
	
}
