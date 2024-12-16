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
import service.Service;

/**
 *
 * @author Hazim Asri
 */
public class ChargerMapAction extends Action {

	/**
         * @param service 
         */
	public ChargerMapAction(Service service) {
		super(service);
	}
        
        /**
         * Processes a JSON, uploads a file using a service, and marks success 
         * or failure on the request.

         * @param file The XML file containing intersection and street segment data. 
         * The file should contain elements with the tags "noeud" for intersections
         * and "troncon" for adjacent street segments.
         * @return A {@link HashMap} where the key is the intersection ID (Long) 
         * and the value is the {@link Intersection} object.
         * @throws RuntimeException If any error occurs during the XML parsing process, 
         * such as invalid format or missing data.
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
		String fileContent = jsonRequest.get("file-content").getAsString();
		
		if(fileContent != null) {
			try {
				Map map = service.loadMap(fileContent);
				
				request.setAttribute("success", true);
				request.setAttribute("map", map);
			} catch (IOException ex) {
				Logger.getLogger(ChargerMapAction.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			request.setAttribute("success", false);
		}
	}
	



}
