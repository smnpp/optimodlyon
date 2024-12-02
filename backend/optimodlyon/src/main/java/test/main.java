package test;

import metier.Map;
import service.Service;

/**
 * Main class for testing the map parsing.
 */
public class main {

    public static void main(String[] args) {

        // Chemin du fichier à lire
        String filePath = "data\\moyenPlan.xml";

        try {
            // Instanciation du service
            Service service = new Service();

            // Chargement de la carte
            Map map = service.loadMap(filePath);

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
