/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modele;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import com.google.gson.JsonSyntaxException; // Pour les erreurs liées à Gson
import javax.servlet.http.HttpServletRequest;
import metier.Map;
import metier.TourRequest;
import metier.Courier;
import service.Service;
/**
 *
 * @author jnoukam
 */
public class ComputeMultipleTourAction extends Action {
    
    public ComputeMultipleTourAction(Service service) {
        super(service);
    }

    @Override
    public void execute(HttpServletRequest request) {
        // Initialiser les valeurs par défaut
        String fileMapContent = null;
        String fileRequestsContent = null;
        int numCouriers = 1; // Valeur par défaut

        try {
            // Récupération du contenu JSON
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

            // Vérification de la présence des clés et récupération des valeurs
            if (jsonRequest.has("file-map-content") && jsonRequest.has("file-requests-content") && jsonRequest.has("nb-couriers")) {
                fileMapContent = jsonRequest.get("file-map-content").getAsString();
                fileRequestsContent = jsonRequest.get("file-requests-content").getAsString();
                numCouriers = jsonRequest.get("nb-couriers").getAsInt();

                if (numCouriers <= 0) {
                    numCouriers = 1; // Si le nombre de livreurs est 0 ou négatif, mettre à 1
                }
            } else {
                request.setAttribute("success", false);
                request.setAttribute("message", "Missing required parameters.");
                return; // Arrêter l'exécution si des paramètres sont manquants
            }

            // Charger la carte et les requêtes
            Map map = service.loadMap(fileMapContent);
            TourRequest tourRequest = service.loadRequestFile(fileRequestsContent);

            // Calculer et assigner les tournées
            HashMap<Long, Courier> couriers = service.computeAndAssignTour(tourRequest, map, numCouriers);

            // Définir les attributs pour la réponse
            request.setAttribute("success", true);
            request.setAttribute("couriers", couriers);

        } catch (IOException | JsonSyntaxException ex) {
            // Gestion des erreurs de lecture ou de parsing
            Logger.getLogger(ComputeMultipleTourAction.class.getName()).log(Level.SEVERE, "Error processing request.", ex);
            request.setAttribute("success", false);
            request.setAttribute("message", "Error processing request.");
        }
    }


}