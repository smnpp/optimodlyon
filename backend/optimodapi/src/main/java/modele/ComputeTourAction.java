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
	
	public ComputeTourAction(Service service) {
		super(service);
	}
	
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
		} catch (IOException ex) {
			Logger.getLogger(ComputeTourAction.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
