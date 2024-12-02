/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author jassirhabba
 */


import metier.Map;
import metier.Intersection;
import metier.Segment;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XmlMapParser implements FileParser<Map> {

    @Override
    public Map parse(String filePath) {
        List<Intersection> intersections = new ArrayList<>();
        List<Segment> segments = new ArrayList<>();
        HashMap<Long, Intersection> intersectionMap = new HashMap<>();

        try {


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            document.getDocumentElement().normalize();

            // Parsing intersections
            NodeList noeuds = document.getElementsByTagName("noeud");

            for (int i = 0; i < noeuds.getLength(); i++) {
                Element noeud = (Element) noeuds.item(i);
                Long id = Long.parseLong(noeud.getAttribute("id"));
                Double latitude = Double.parseDouble(noeud.getAttribute("latitude"));
                Double longitude = Double.parseDouble(noeud.getAttribute("longitude"));

                Intersection intersection = new Intersection(id, latitude, longitude);
                intersections.add(intersection);
                intersectionMap.put(id, intersection); // Stocker dans la HashMap

            }

            // Parsing segments
            NodeList troncons = document.getElementsByTagName("troncon");
            
            for (int i = 0; i < troncons.getLength(); i++) {
                Element troncon = (Element) troncons.item(i);
                Long origine = Long.parseLong(troncon.getAttribute("origine"));
                Long destination = Long.parseLong(troncon.getAttribute("destination"));
                Double longueur = Double.parseDouble(troncon.getAttribute("longueur"));
                String nomRue = troncon.getAttribute("nomRue");

                // Recherche dans la HashMap pour les intersections
                Intersection origin = intersectionMap.get(origine);
                Intersection dest = intersectionMap.get(destination);

                // Vérification d'existence
                if (origin == null || dest == null) {
                    System.err.println("Skipping troncon with missing intersections: origine=" + origine + ", destination=" + destination);
                    continue;
                }

                Segment segment = new Segment(origin, dest, nomRue, longueur);
                segments.add(segment);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Map(intersections, segments);
    }
}