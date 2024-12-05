package test;

import metier.Map;
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

        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
