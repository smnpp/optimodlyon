package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import metier.DeliveryRequest;
import metier.Intersection;
import metier.Tour;
import metier.TourRequest;
import metier.Warehouse;
import metier.Courier;
// Utilisation explicite de metier.Map
import metier.Map;
import service.Service;

public class main {

    public static void main(String[] args) {
        String fileMapPath = "C:\\Users\\Junior Noukam\\Downloads\\fichiersXMLPickupDelivery\\grandPlan.xml"; // Remplace par le chemin du fichier de carte
        String fileRequestPath = "C:\\Users\\Junior Noukam\\Downloads\\fichiersXMLPickupDelivery\\demandeGrand9.xml"; // Remplace par le chemin du fichier des demandes

        try {
            // Instanciation du service
            Service service = new Service();

            // Chargement de la carte
            String fileMapContent = new String(Files.readAllBytes(Paths.get(fileMapPath)));
            metier.Map map = service.loadMap(fileMapContent);  // Appel de loadMap

            // Vérification et affichage
            if (map != null) {
                System.out.println("Carte chargée avec succès. \n\n");
            } else {
                System.out.println("Impossible de charger la carte.");
            }

            // Chargement des demandes de livraison
            String fileRequestContent = new String(Files.readAllBytes(Paths.get(fileRequestPath)));
            TourRequest tourRequest = service.loadRequestFile(fileRequestContent);  // Appel de loadRequestFile

            // Vérification et affichage
            if (tourRequest != null) {
                System.out.println("Liste des requêtes chargée avec succès. \n Requetes : \n" + tourRequest);
            } else {
                System.out.println("Impossible de charger la liste des requêtes.");
            }

            // Nombre de livreurs à tester
            int numCouriers = 4; // Ajuste en fonction du nombre de livreurs

            // Appeler la méthode computeAndAssignTour pour attribuer les requêtes aux livreurs
            HashMap<Long, Courier> couriers = service.computeAndAssignTour(tourRequest, map, numCouriers);
            
            // Initialiser une liste pour stocker les objets Tour
            List<Tour> tours = new ArrayList<>();

            // Parcourir les couriers et collecter les deliveryPlans
            for (Courier courier : couriers.values()) {
                if (courier.getDeliveryPlan() != null) { // Vérifiez si le deliveryPlan n'est pas null
                    tours.add(courier.getDeliveryPlan());
                }
            }
            System.out.println("Nombre total de livreurs : " + couriers.size());
            
            Boolean ispdf = service.saveToursToPdf(tours);
            // Afficher les informations pour chaque livreur
            for (Courier courier : couriers.values()) {
                System.out.println("\n=== Informations du livreur " + courier.getId() + " ===");
                //System.out.println("Plan de livraison : " + courier.getDeliveryPlan());
                System.out.println("Liste des requêtes : " + courier.getTourRequest().getRequests().keySet());

                Tour tour = courier.getDeliveryPlan();
                // Affichage des statistiques finales
                System.out.println("========= Statistiques ==========");
                System.out.println("Nombre total d'intersections dans le tour : " + tour.getPointslist().size());
                System.out.println("=================================\n");
            }

            
            System.out.println("Nombre total d'intersections dans la carte : " + map.getIntersections().size());

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture des fichiers : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
