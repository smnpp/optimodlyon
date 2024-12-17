/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modele;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
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
    
        
	/**
	 * @param service 
	 */
	public ComputeTourAction(Service service) {
		super(service);
	}
	
	/**
	 *  Processes a JSON request to load a map file and a delivery request file, computes a delivery tour, 
 * and sets the result or failure status on the request.
	 * @param request 
	 * throws JsonSyntaxException If the JSON request is malformed or contains invalid data.
	 * throws IllegalArgumentException If required JSON parameters are missing or invalid.
	 */
	@Override
	public void execute(HttpServletRequest request) {
		BufferedReader reader = null;
		try {
			reader = request.getReader();
		} catch (IOException ex) {
			Logger.getLogger(ChargerMapAction.class.getName()).log(Level.SEVERE, null, ex);
		}
		Gson gson = new Gson();
		JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
		
		String mapFile = jsonRequest.get("map-file").getAsString();
		String requestFile = jsonRequest.get("request-file").getAsString();
		
		try {
			Map map = service.loadMap(mapFile);
			TourRequest tourRequest = service.loadRequestFile(requestFile);
			
			Tour tour = service.computeTour(tourRequest, map);
			
			request.setAttribute("success", true);
			request.setAttribute("tour", tour);
                        request.setAttribute("tourRequest", tourRequest);
		} catch (IOException ex) {
			Logger.getLogger(ComputeTourAction.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
