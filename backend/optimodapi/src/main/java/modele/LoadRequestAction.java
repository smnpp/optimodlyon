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

	public LoadRequestAction(Service service) {
		super(service);
	}
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
		String fileName = jsonRequest.get("file-name").getAsString();
		
		if(fileContent != null && fileName != null) {
			try {
				TourRequest tourRequest = service.loadRequestFile(fileContent, fileName);
				
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
