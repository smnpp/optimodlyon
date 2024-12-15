package service;

import java.io.IOException;
import java.util.List;
import metier.DeliveryRequest;
import metier.Intersection;
import metier.Map;
import metier.Tour;
import metier.TourRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import util.tsp.ComputeTourUtilTools;

class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
    }

    @Test
    void testLoadMap() {
        String fileContent = ""
                + "<reseau>\n"
                + "<noeud id=\"25175791\" latitude=\"45.75406\" longitude=\"4.857418\"/>\n"
                + "<noeud id=\"2129259178\" latitude=\"45.750404\" longitude=\"4.8744674\"/>"
                + "<troncon destination=\"25175791\" longueur=\"51.028988\" nomRue=\"Impasse Lafontaine\" origine=\"2129259178\"/>\n"
                + "</reseau>";

        try {
            Map map = service.loadMap(fileContent);
            assertNotNull(map, "The map must not be null.");
            assertTrue(map.getIntersections().containsKey(25175791L), "Intersection 25175791 must exist.");
            assertTrue(map.getIntersections().containsKey(2129259178L), "Intersection 2129259178 must exist");
            assertTrue(map.getIntersections().get(2129259178L).getAdjacents().containsKey(25175791L), "Intersection 25175791 must be an adjacent of 2129259178");
        } catch (Exception e) {
            fail("Loading map failed: " + e.getMessage());
        }
    }

    @Test
    void testLoadRequestFile() {
        String fileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<demandeDeLivraisons>\n"
                + "<entrepot adresse=\"2835339774\" heureDepart=\"8:0:0\"/>\n"
                + "<livraison adresseEnlevement=\"1679901320\" adresseLivraison=\"208769457\" dureeEnlevement=\"420\" dureeLivraison=\"600\"/>\n"
                + "<livraison adresseEnlevement=\"208769120\" adresseLivraison=\"25336179\" dureeEnlevement=\"420\" dureeLivraison=\"480\"/>\n"
                + "</demandeDeLivraisons>";

        try {
            TourRequest tourRequest = service.loadRequestFile(fileContent);
            assertNotNull(tourRequest, "The delivery request must not be null.");
            assertEquals(2, tourRequest.getRequests().size(), "There must be 2 delivery requests.");
        } catch (Exception e) {
            fail("Loading delivery request failed: " + e.getMessage());
        }
    }

    @Test
    void testCreateDeliveryRequest() {
        Long pickupPoint = 123L;
        Long deliveryPoint = 456L;
        Long pickupDuration = 300L;
        Long deliveryDuration = 600L;

        try {
            DeliveryRequest deliveryRequest = service.createDeliveryRequest(pickupPoint, deliveryPoint, pickupDuration, deliveryDuration);
            assertNotNull(deliveryRequest, "The delivery request must not be null.");
            assertEquals(pickupPoint, deliveryRequest.getPickupPoint(), "The pickup point should be 123.");
            assertEquals(deliveryPoint, deliveryRequest.getDeliveryPoint(), "The delivery point should be 456.");
        } catch (IOException e) {
            fail("Creation of delivery request failed: " + e.getMessage());
        }
    }

    @Test
    void testComputeTourWithMoreNodesAndConditions() {

        String fileMapContent = ""
                + "<reseau>\n"
                + "<noeud id=\"2835339774\" latitude=\"45.75406\" longitude=\"4.857418\"/>\n" // Entrepôt
                + "<noeud id=\"1679901320\" latitude=\"45.750404\" longitude=\"4.8744674\"/>\n" // Pickup 1
                + "<noeud id=\"208769457\" latitude=\"45.7585\" longitude=\"4.8356\"/>\n" // Delivery 1
                + "<noeud id=\"223344556\" latitude=\"45.7590\" longitude=\"4.8500\"/>\n" // Pickup 2
                + "<noeud id=\"556677889\" latitude=\"45.7600\" longitude=\"4.8400\"/>\n" // Delivery 2
                + "<noeud id=\"123456789\" latitude=\"45.7560\" longitude=\"4.8600\"/>\n" // point intermédiaire
                + "<noeud id=\"987654321\" latitude=\"45.7520\" longitude=\"4.8700\"/>\n" // point intermédiaire
                + "<noeud id=\"999999999\" latitude=\"45.8000\" longitude=\"4.9000\"/>\n"
                + "<troncon destination=\"1679901320\" longueur=\"51.028988\" nomRue=\"Rue A\" origine=\"2835339774\"/>\n" // Entrepôt -> Pickup 1
                + "<troncon destination=\"208769457\" longueur=\"75.0\" nomRue=\"Rue B\" origine=\"1679901320\"/>\n" // Pickup 1 -> Delivery 1
                + "<troncon destination=\"223344556\" longueur=\"30.0\" nomRue=\"Rue C\" origine=\"208769457\"/>\n" // Delivery 1 -> Pickup 2
                + "<troncon destination=\"556677889\" longueur=\"40.0\" nomRue=\"Rue D\" origine=\"223344556\"/>\n" // Pickup 2 -> Delivery 2
                + "<troncon destination=\"2835339774\" longueur=\"100.0\" nomRue=\"Rue E\" origine=\"556677889\"/>\n" // Delivery 2 -> Entrepôt
                + "<troncon destination=\"123456789\" longueur=\"30.0\" nomRue=\"Rue F\" origine=\"2835339774\"/>\n" // Entrepôt -> Intermédiaire 1
                + "<troncon destination=\"987654321\" longueur=\"40.0\" nomRue=\"Rue G\" origine=\"123456789\"/>\n" // Intermédiaire 1 -> Intermédiaire 2
                + "<troncon destination=\"1679901320\" longueur=\"60.0\" nomRue=\"Rue H\" origine=\"987654321\"/>\n" // Intermédiaire 2 -> Pickup 1
                + "</reseau>";

        // Contenu XML des requêtes avec au moins 2 requêtes
        String fileRequestContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<demandeDeLivraisons>\n"
                + "<entrepot adresse=\"2835339774\" heureDepart=\"8:0:0\"/>\n"
                + "<livraison adresseEnlevement=\"1679901320\" adresseLivraison=\"208769457\" dureeEnlevement=\"300\" dureeLivraison=\"400\"/>\n"
                + "<livraison adresseEnlevement=\"223344556\" adresseLivraison=\"556677889\" dureeEnlevement=\"300\" dureeLivraison=\"400\"/>\n"
                + "</demandeDeLivraisons>";

        try {
            // Charger la carte
            Map map = service.loadMap(fileMapContent);
            assertNotNull(map, "The map must not be null.");

            // Charger la demande de livraison
            TourRequest tourRequest = service.loadRequestFile(fileRequestContent);
            assertNotNull(tourRequest, "The tour request must not be null.");

            // Calculer le tour
            Tour tour = service.computeTour(tourRequest, map);
            assertNotNull(tour, "The computed tour must not be null.");

            // Vérifier que le tour commence et finit par l'entrepôt
            List<Intersection> tourPoints = tour.getPointslist();
            assertEquals(Long.valueOf(2835339774L), tourPoints.get(0).getId(), "The tour must start at the warehouse.");
            assertEquals(Long.valueOf(2835339774L), tourPoints.get(tourPoints.size() - 1).getId(), "The tour must end at the warehouse.");

            // Vérifications sur les points du tour
            int requestPointsCount = tourRequest.getRequests().size() * 2 + 1; // Chaque requête a pickup + delivery + depot
            assertTrue(tourPoints.size() >= requestPointsCount,
                    "The number of points in the tour must be greater than or equal to the number of points in the tour request.");

            
            // Vérifier que tous les pickup et delivery sont présent
            for (DeliveryRequest request : tourRequest.getRequests().values()) {
                Long pickupPoint = request.getPickupPoint();
                Long deliveryPoint = request.getDeliveryPoint();

                int pickupIndex = -1;
                int deliveryIndex = -1;

                for (int i = 0; i < tourPoints.size(); i++) {
                    if (tourPoints.get(i).getId().equals(pickupPoint)) {
                        pickupIndex = i;
                    } else if (tourPoints.get(i).getId().equals(deliveryPoint)) {
                        deliveryIndex = i;
                    }
                }

                assertTrue(pickupIndex >= 0 && deliveryIndex >= 0,
                        "Both pickup and delivery points must be present in the tour.");
            }
            // Vérification de la durée totale
            assertFalse(tour.getDuration().isZero(), "The total duration of the tour must not be null.");
            assertFalse(tour.getDuration().isNegative(), "The total duration must be positive.");

        } catch (Exception e) {
            fail("Compute tour failed: " + e.getMessage());
        }
    }
}
