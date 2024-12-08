package test;

import java.util.HashMap;
import java.util.List;
import metier.DeliveryRequest;
import metier.Intersection;
import metier.Map;
import metier.Tour;
import metier.TourRequest;
import metier.Warehouse;
import service.Service;
import util.tsp.ComputeTourUtilTools;

/**
 * Main class for testing the map parsing.
 */
public class main {

    public static void main(String[] args) {
        
        String fileMapContent = ""
	+ "<reseau>\n"
	+ "<noeud id=\"2835339774\" latitude=\"45.75406\" longitude=\"4.857418\"/>\n" 
	+ "<noeud id=\"1679901320\" latitude=\"45.750404\" longitude=\"4.8744674\"/>"
	+ "<troncon destination=\"1679901320\" longueur=\"51.028988\" nomRue=\"Impasse Lafontaine\" origine=\"2835339774\"/>\n" 
        + "<troncon destination=\"2835339774\" longueur=\"51.028988\" nomRue=\"Impasse Lafontaine\" origine=\"1679901320\"/>\n" 
	+ "</reseau>";
        String fileMapName = "map.xml";
        
        String fileDemandeContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<demandeDeLivraisons>\n"
                + "<entrepot adresse=\"2835339774\" heureDepart=\"8:0:0\"/>\n"
                + "<livraison adresseEnlevement=\"1679901320\" adresseLivraison=\"2835339774\" dureeEnlevement=\"420\" dureeLivraison=\"600\"/>\n"
                + "</demandeDeLivraisons>";
        String fileDemandeName = "requests.xml";

        
        try {
            // Instanciation du service
            Service service = new Service();
            ComputeTourUtilTools ComputeTourUtil = new ComputeTourUtilTools();

            // Chargement de la carte
            Map map = service.loadMap(fileMapContent, fileMapName);
            
            // Vérification et affichage
            if (map != null) {
                System.out.println("Carte chargée avec succès. \n\n");
            } else {
                System.out.println("Impossible de charger la carte.");
            }

            // Chargement de Tour request
            TourRequest tourRequest = service.loadRequestFile(fileDemandeContent, fileDemandeName);
            
            // Vérification et affichage
            if (tourRequest != null) {
                System.out.println("Liste des requêtes chargée avec succès. \n Requetes : \n" + tourRequest);
            } else {
                System.out.println("Impossible de charger la liste des requêtes.");
            }

            // Construire la matrice des plus courts chemins si point de vue des segments
            // HashMap<Pair<Long, Long>, List<Long>> shortestPaths = ComputeTourUtil.computeAllShortestPathsWithPaths(map);
            
            
            // Ordonnancer les requêtes
            List<Long> orderedPoints = ComputeTourUtil.scheduleOptimizedDeliveryRequests(tourRequest, map);
            
            System.out.println("\nOrdered deliveryRequest points : " + orderedPoints);
            
            // Construire le tour complet
            Tour tour = service.computeTour(orderedPoints, map);
            
            //Ne pas tester pour l'instant
            //Tour tour = service.constructTourWithGeographicZones(orderedPoints, map);
            
            // Afficher les informations du tour
            System.out.println("=== Informations du Tour ===");
            System.out.println("Tour ID : " + tour.getId());
            System.out.println("Durée totale : " + tour.getDuration().toMinutes() + " minutes");
            System.out.println("Itinéraire complet :");

            // Affichage des intersections avec détails des pickup/delivery
            HashMap<String, DeliveryRequest> requests = (HashMap<String, DeliveryRequest>) tourRequest.getRequests();
            Warehouse wareHouse = tourRequest.getWarehouse();
            for (Intersection point : tour.getPointslist()) {
                boolean isPickup = false;
                boolean isDelivery = false;
                String requestInfo = "";
                
                for (DeliveryRequest request : requests.values()) {
                    if (request.getPickupPoint().equals(point.getId())) {
                        isPickup = true;
                        requestInfo = " (Pickup for DeliveryRequest " + request.getId() + ")";
                        break;
                    } else if (request.getDeliveryPoint().equals(point.getId())) {
                        isDelivery = true;
                        requestInfo = " (Delivery for DeliveryRequest " + request.getId() + ")";
                        break;
                    }
                }

                String type =(isPickup && wareHouse.getId().equals(point.getId()))? " Warehouse & Pickup" : 
                             (isPickup ? "Pickup" : 
                             ((isDelivery && wareHouse.getId().equals(point.getId()))? " Warehouse & Delivery" :
                             (isDelivery ? "Delivery" : 
                             (wareHouse.getId().equals(point.getId()) ? "Warehouse" : "Intermediate"))));

                System.out.println("Intersection ID : " + point.getId() + " - " + type + requestInfo);
            }

            // Affichage des statistiques finales
            System.out.println("\n=== Statistiques ===");
            System.out.println("Nombre total d'intersections dans le tour : " + tour.getPointslist().size());
            System.out.println("Nombre total d'intersections dans la carte : " + map.getIntersections().size());

        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
