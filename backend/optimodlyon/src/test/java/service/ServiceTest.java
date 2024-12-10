package service;

import java.io.IOException;
import metier.DeliveryRequest;
import metier.Map;
import metier.TourRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}