/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modele;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import metier.Map;
import metier.TourRequest;
import service.Service;

/**
 *
 * @author wockehs
 */
public class LoadRequestAction extends Action {
	/**
	 * 
	 * @param service 
	 */
	public LoadRequestAction(Service service) {
		super(service);
	}
	/**
	 * Processes a JSON request to load a delivery request file, parses its content, 
 * and sets the result or failure status on the HTTP request
	 * @param request 
	 * throws JsonSyntaxException If the JSON request is malformed or contains invalid data.
	 * throws IllegalArgumentException If the required JSON parameter is missing or invalid.
	 */
	@Override
	public void execute(HttpServletRequest request) {
		BufferedReader reader = null;
		try {
			reader = request.getReader();
		} catch (IOException ex) {
			Logger.getLogger(LoadRequestAction.class.getName()).log(Level.SEVERE, null, ex);
		}
		Gson gson = new Gson();
		JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
		String fileContent = jsonRequest.get("file-content").getAsString();
		
		if(fileContent != null) {
			try {
				TourRequest tourRequest = service.loadRequestFile(fileContent);
				
				request.setAttribute("success", true);
				request.setAttribute("tour-request", tourRequest);
			} catch (IOException ex) {
				Logger.getLogger(LoadRequestAction.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			request.setAttribute("success", false);
		}
	}
	
}
