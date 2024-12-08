package test;

import metier.Map;
import metier.TourRequest;
import service.Service;

/**
 * Main class for testing the map parsing.
 */
public class main {

    public static void main(String[] args) {

        // Chemin du fichier à lire
	String fileName = "map.xml";
	String fileContent = ""
	+ "<reseau>\n"
	+ "<noeud id=\"25175791\" latitude=\"45.75406\" longitude=\"4.857418\"/>\n" 
	+ "<noeud id=\"2129259178\" latitude=\"45.750404\" longitude=\"4.8744674\"/>"
	+ "<troncon destination=\"25175791\" longueur=\"51.028988\" nomRue=\"Impasse Lafontaine\" origine=\"2129259178\"/>\n" 
	+ "</reseau>";

        String deliveryRequestFileName = "requests.xml";
        String deliveryRequestileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<demandeDeLivraisons>\n"
                + "<entrepot adresse=\"2835339774\" heureDepart=\"8:0:0\"/>\n"
                + "<livraison adresseEnlevement=\"1679901320\" adresseLivraison=\"208769457\" dureeEnlevement=\"420\" dureeLivraison=\"600\"/>\n"
                + "<livraison adresseEnlevement=\"208769120\" adresseLivraison=\"25336179\" dureeEnlevement=\"420\" dureeLivraison=\"480\"/>\n"
                + "</demandeDeLivraisons>";

        String englishDeliveryRequestFileName = "requestsEnglish.xml";
        String englishDeliveryRequestileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<demandeDeLivraisons>\n"
                + "<entrepot adresse=\"2835339774\" heureDepart=\"8:0:0\"/>\n"
                + "<livraison adresseEnlevement=\"1679901320\" adresseLivraison=\"208769457\" dureeEnlevement=\"420\" dureeLivraison=\"600\"/>\n"
                + "<livraison adresseEnlevement=\"208769120\" adresseLivraison=\"25336179\" dureeEnlevement=\"420\" dureeLivraison=\"480\"/>\n"
                + "</demandeDeLivraisons>";

        try {
            // Instanciation du service
            Service service = new Service();

            // Chargement de la carte
            Map map = service.loadMap(fileContent, fileName);

            // Vérification et affichage
            if (map != null) {
                System.out.println("Carte chargée avec succès : \n\n" + map);
            } else {
                System.out.println("Impossible de charger la carte.");
            }

            TourRequest tourRequest = service.loadRequestFile(deliveryRequestileContent, deliveryRequestFileName);

            // Vérification et affichage
            if (tourRequest != null) {
                System.out.println("Demandes de livraison chargése avec succès : \n\n" + tourRequest);
            } else {
                System.out.println("Impossible de charger les demandes de livraison.");
            }

            TourRequest englishTourRequest = service.loadRequestFile(englishDeliveryRequestileContent, englishDeliveryRequestFileName);

            // Vérification et affichage
            if (englishTourRequest != null) {
                System.out.println("Demandes de livraison chargése avec succès : \n\n" + englishTourRequest);
            } else {
                System.out.println("Impossible de charger les demandes de livraison.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
