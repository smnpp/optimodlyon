package modele;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import metier.Intersection;
import java.time.Duration;
import metier.Coords;

import metier.Tour;
import service.Service;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author jassirhabba
 */
public class SaveTourAction extends Action {

    public SaveTourAction(Service service) {
        super(service);
    }

    /**
     * Processes a JSON request to construct and save a list of tours and sets
     * the success status in the HTTP request.
     *
     * @param request The JSON request containing the necessary data to
     * construct the list of tours.
     * @param request The HTTP request object where the success status will be
     * set. throws SomeException If an error occurs while processing the request
     * or saving the tours.
     */
    @Override
    public void execute(HttpServletRequest request) {

        BufferedReader reader = null;
        try {
            // Lire le corps de la requête
            reader = request.getReader();

            // Utiliser Gson pour convertir le JSON en objet
            Gson gson = new Gson();
            JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

            JsonArray toursArray = jsonRequest.getAsJsonArray("tours");
            JsonArray deliveryRequestArray = jsonRequest.getAsJsonArray("deliveryRequests");
            JsonObject jsonWarehouse = jsonRequest.getAsJsonObject("warehouse");
            // Construire manuellement les objets Tour
            List<Tour> tours = new ArrayList<>();

            String keyWarehouse = jsonWarehouse.get("key").getAsString();
            Long idIntersectionWarehouse = Long.parseLong(keyWarehouse);
            JsonObject locationObjectWarehouse = jsonWarehouse.getAsJsonObject("location");
            double latWarehouse = locationObjectWarehouse.get("lat").getAsDouble();
            double lngWarehouse = locationObjectWarehouse.get("lng").getAsDouble();

            // Créer l'objet Location
            Coords locationWarehouse = new Coords(latWarehouse, lngWarehouse);

            // Créer l'objet Intersection
            Intersection warehouse = new Intersection(idIntersectionWarehouse, locationWarehouse);

            for (JsonElement tourElement : toursArray) {
                JsonObject tourObject = tourElement.getAsJsonObject();

                // Extraire les données du tour
                String id = tourObject.get("id").getAsString();
                int durationInSeconds = tourObject.get("duration").getAsInt();
                Duration duration = Duration.ofSeconds(durationInSeconds);
                JsonArray intersectionsArray = tourObject.getAsJsonArray("intersections");

                // Construire les intersections
                List<Intersection> intersections = new ArrayList<>();
                for (JsonElement intersectionElement : intersectionsArray) {
                    JsonObject intersectionObject = intersectionElement.getAsJsonObject();

                    String key = intersectionObject.get("key").getAsString();
                    Long idIntersection = Long.parseLong(key);
                    JsonObject locationObject = intersectionObject.getAsJsonObject("location");
                    double lat = locationObject.get("lat").getAsDouble();
                    double lng = locationObject.get("lng").getAsDouble();

                    // Créer l'objet Location
                    Coords location = new Coords(lat, lng);

                    // Créer l'objet Intersection
                    Intersection intersection = new Intersection(idIntersection, location);

                    intersections.add(intersection);
                }

                // Créer l'objet Tour
                Tour tour = new Tour();
                tour.setId(id);
                tour.setDuration(duration);
                tour.setPointslist(intersections);

                tours.add(tour);
            }

            Boolean success = service.saveToursToFile(tours, deliveryRequestArray, warehouse);
            request.setAttribute("success", success);

        } catch (IOException ex) {
            Logger.getLogger(SaveTourAction.class.getName()).log(Level.SEVERE, "Erreur de lecture de la requête", ex);
        } catch (Exception ex) {
            Logger.getLogger(SaveTourAction.class.getName()).log(Level.SEVERE, "Erreur lors de la sauvegarde des tours", ex);
        }

    }

}
