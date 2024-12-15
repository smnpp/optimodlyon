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
import metier.DeliveryRequest;
import metier.Map;
import metier.Tour;
import metier.Coords;
import metier.TourRequest;
import service.Service;

/**
 *
 * @author simonperret
 */
public class ModifyPickupPointAction extends Action {

    public ModifyPickupPointAction(Service service) {
        super(service);
    }

    @Override
    public void execute(HttpServletRequest request) {
        BufferedReader reader = null;
        try {
            reader = request.getReader();
        } catch (IOException ex) {
            Logger.getLogger(ModifyPickupPointAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        Gson gson = new Gson();
        JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

        
        String mapFile = jsonRequest.get("map-file").getAsString();
        String tourRequestId = jsonRequest.get("tour-request-id").getAsString();
        String deliveryRequestId = jsonRequest.get("delivery-request-id").getAsString();
        Double newPickupLatitude = jsonRequest.get("new-pickup-latitude").getAsDouble();
        Double newPickupLongitude = jsonRequest.get("new-pickup-longitude").getAsDouble();

        try {
            Map map = service.loadMap(mapFile);
            TourRequest tourRequest = (TourRequest) request.getSession().getAttribute(tourRequestId);
            DeliveryRequest deliveryRequest = tourRequest.getRequests().get(deliveryRequestId);
            
            Long closestIntersectionId = service.findClosestIntersection(newPickupLatitude, newPickupLongitude, map);
            
            Tour updatedTour = service.changePickupPoint(tourRequest, map, deliveryRequest, closestIntersectionId);

            request.setAttribute("success", true);
	    request.setAttribute("tour", updatedTour);

        } catch (Exception ex) {
            Logger.getLogger(ModifyPickupPointAction.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("success", false);
        }
    }
}