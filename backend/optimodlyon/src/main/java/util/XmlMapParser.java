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
    /**
    * Parses the given XML file to create and populate a map of {@link Intersection} objects. 
    * The XML file is expected to contain data about intersections (nodes) and adjacent street segments (troncons).
    * The intersections are identified by their unique IDs, and the adjacent street segments 
    * define connections between intersections with additional information like street name and length.
    * 
    * @param file The XML file containing intersection and street segment data. The file should contain 
    *             elements with the tags "noeud" for intersections and "troncon" for adjacent street segments.
    * @return A {@link HashMap} where the key is the intersection ID (Long) and the value is the {@link Intersection} object.
    * @throws RuntimeException If any error occurs during the XML parsing process, such as invalid format or missing data.
    */
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

                Adjacent adjacent_to_dest = new Adjacent( dest, nomRue, longueur);
                Adjacent adjacent_to_origin = new Adjacent( origin, nomRue, longueur);
                intersectionMap.get(origineId).addAdjacent(destinationId, adjacent_to_dest);
                intersectionMap.get(destinationId).addAdjacent(origineId, adjacent_to_origin);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return intersectionMap;
    }
}