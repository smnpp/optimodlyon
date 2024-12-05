/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author jassirhabba
 */


import java.io.BufferedWriter;
import metier.Intersection;
import metier.Adjacent;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import metier.Coords;

public class XmlMapParser implements FileParser<HashMap<Long, Intersection>> {

    @Override
    public HashMap<Long, Intersection> parse(File file) {
        HashMap<Long, Intersection> intersectionMap = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();	   
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            // Parsing intersections
            NodeList noeuds = document.getElementsByTagName("noeud");

            for (int i = 0; i < noeuds.getLength(); i++) {
                Element noeud = (Element) noeuds.item(i);
                Long id = Long.parseLong(noeud.getAttribute("id"));
                Double latitude = Double.parseDouble(noeud.getAttribute("latitude"));
                Double longitude = Double.parseDouble(noeud.getAttribute("longitude"));
                Coords location = new Coords(latitude, longitude);
                
                Intersection intersection = new Intersection(id, location);
                intersectionMap.put(id, intersection); // Stocker dans la HashMap

            }

            // Parsing adjacents
            NodeList troncons = document.getElementsByTagName("troncon");
            
            for (int i = 0; i < troncons.getLength(); i++) {
                Element troncon = (Element) troncons.item(i);
                Long origineId = Long.parseLong(troncon.getAttribute("origine"));
                Long destinationId = Long.parseLong(troncon.getAttribute("destination"));
                Double longueur = Double.parseDouble(troncon.getAttribute("longueur"));
                String nomRue = troncon.getAttribute("nomRue");

                // Recherche dans la HashMap pour les intersections
                Intersection origin = intersectionMap.get(origineId);
                Intersection dest = intersectionMap.get(destinationId);

                // Vérification d'existence
                if (origin == null || dest == null) {
                    System.err.println("Skipping troncon with missing intersections: origine=" + origineId + ", destination=" + destinationId);
                    continue;
                }

                Adjacent adjacent = new Adjacent( dest, nomRue, longueur);
                intersectionMap.get(origineId).addAdjacent(destinationId, adjacent);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return intersectionMap;
    }
}