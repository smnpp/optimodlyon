/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import metier.Intersection;
import metier.Map;
import metier.Tour;
import metier.TourRequest;
import metier.DeliveryRequest;
import metier.Courier;
import util.tsp.ComputeTourUtilTools;
import util.FileParser;
import util.FileParserFactory;
import util.FileType;
import util.tsp.PathResult;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.time.LocalDate;
import java.io.File;

/**
 *
 * @author jnoukam
 * Service class that provides methods for loading maps, handling delivery requests,
 * computing delivery tours, and saving results in XML format.
 */
public class Service {

     /**
     * Loads a map from an XML string content.
     * 
     * @param fileContent The XML content representing the map.
     * @return A Map object containing the loaded intersections.
     * @throws IOException If there is an issue during file creation or reading.
     */


    public Map loadMap(String fileContent) throws IOException {


        // Déterminer le type de fichier
        File file = File.createTempFile("temp", ".xml");
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(fileContent);
        }

        FileType fileType = FileParserFactory.determineFileType(file);
        // Vérifier si le type de fichier est bien XmlMap
        if (fileType != FileType.XMLMAP) {
            throw new IllegalArgumentException("Invalid file type for loading a map: " + fileType);
        }

        // Récupérer le parser approprié via la factory
        FileParser<HashMap<Long, Intersection>> parser = (FileParser<HashMap<Long, Intersection>>) FileParserFactory.getParser(fileType);

        // Parse le fichier et récupère la HashMap d'intersections
        HashMap<Long, Intersection> intersectionMap = parser.parse(file);

        // Construire l'objet Map à partir de la HashMap d'intersections
        Map map = new Map(intersectionMap);

        return map;
    }
    
     /**
     * Loads a delivery request from an XML string content.
     * 
     * @param fileContent The XML content representing the delivery request.
     * @return A TourRequest object containing the parsed delivery requests.
     * @throws IOException If there is an issue during file creation or reading.
     */


    public TourRequest loadRequestFile(String fileContent) throws IOException {

        // Déterminer le type de fichier
        File file = File.createTempFile("temp", ".xml");
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(fileContent);
        }

        FileType fileType = FileParserFactory.determineFileType(file);
        // Vérifier si le type de fichier est bien XmlMap
        if (fileType != FileType.XMLDEMANDE) {
            throw new IllegalArgumentException("Invalid file type for loading delivery request: " + fileType);
        }

        // Récupérer le parser approprié via la factory
        FileParser<TourRequest> parser = (FileParser<TourRequest>) FileParserFactory.getParser(fileType);

        TourRequest tourRequest = parser.parse(file);

        return tourRequest;

    }  

     /**
     * Computes the optimal delivery tour based on a TourRequest and Map.
     * 
     * @param tourRequest The TourRequest containing the delivery requests.
     * @param map The map containing all the intersections.
     * @return A Tour object representing the optimal delivery tour.
     * @throws IllegalArgumentException If any of the pickup or delivery points are not present in the map.
     */
    
    public Tour computeTour(TourRequest tourRequest, Map map) {
        // Vérification que tous les points sont dans la Map
        for (DeliveryRequest request : tourRequest.getRequests().values()) {
            Long pickupPointId = request.getPickupPoint();
            Long deliveryPointId = request.getDeliveryPoint();

            // Vérifier si les points de pickup et de delivery existent dans la Map
            if (!map.getIntersections().containsKey(pickupPointId)) {
                throw new IllegalArgumentException("Point de pickup manquant dans la map : " + pickupPointId);
            }
            if (!map.getIntersections().containsKey(deliveryPointId)) {
                throw new IllegalArgumentException("Point de delivery manquant dans la map : " + deliveryPointId);
            }
        }

        ComputeTourUtilTools computeTourUtil = new ComputeTourUtilTools();

        // Ordonnancer les requêtes géographiquement
        List<Long> orderedPoints = computeTourUtil.scheduleOptimizedDeliveryRequests(tourRequest, map);

        // Durée totale initiale en secondes
        Duration totalDuration = Duration.ZERO;

        // Calculer la durée des arrêts (pickup et delivery) en secondes
        for (DeliveryRequest request : tourRequest.getRequests().values()) {
            totalDuration = totalDuration.plus(request.getPickupDuration());
            totalDuration = totalDuration.plus(request.getDeliveryDuration());
        }

        // Construire la tournée
        //Tour tour = ComputeTourUtilTools.constructTourWithSpecificShortestPaths(orderedPoints, map);
        Tour tour = ComputeTourUtilTools.constructTourWithSpecificShortestPaths(orderedPoints, map);

        // Ajouter la durée de la tournée (en secondes)
        totalDuration = totalDuration.plus(tour.getDuration());

        // Convertir la durée totale en minutes pour l'objet Tour
        Duration totalDurationInMinutes = Duration.ofMinutes(totalDuration.getSeconds() / 60);

        // Mettre à jour la durée totale dans l'objet Tour
        tour.setDuration(totalDurationInMinutes);

        return tour;
    }
    
     /**
     * Creates a new DeliveryRequest based on the provided parameters.
     * 
     * @param pickupPoint The ID of the pickup point.
     * @param deliveryPoint The ID of the delivery point.
     * @param pickupDuration The duration in seconds for pickup.
     * @param deliveryDuration The duration in seconds for delivery.
     * @return A new DeliveryRequest object.
     * @throws IOException If there is an issue during the creation process.
     */

    public DeliveryRequest createDeliveryRequest(Long pickupPoint, Long deliveryPoint, Long pickupDuration, Long deliveryDuration) throws IOException {
        Duration pickupDurationCast = Duration.ofSeconds(pickupDuration);
        Duration deliveryDurationCast = Duration.ofSeconds(deliveryDuration);
        DeliveryRequest deliveryRequest = new DeliveryRequest(pickupPoint, deliveryPoint, pickupDurationCast, deliveryDurationCast);

        return deliveryRequest;
    }
    
    
    /**
     * Adds a DeliveryRequest to a TourRequest.
     * 
     * @param tourRequest The TourRequest to which the delivery request will be added.
     * @param pickupPoint The ID of the pickup point.
     * @param deliveryPoint The ID of the delivery point.
     * @param pickupDuration The duration in seconds for pickup.
     * @param deliveryDuration The duration in seconds for delivery.
     * @return The updated TourRequest with the new delivery request added.
     * @throws IOException If there is an issue during the addition process.
     */

    public TourRequest addDeliveryRequest(TourRequest tourRequest, Long pickupPoint, Long deliveryPoint, Long pickupDuration, Long deliveryDuration) throws IOException {
        DeliveryRequest deliveryRequest = createDeliveryRequest(pickupPoint, deliveryPoint, pickupDuration, deliveryDuration);
        tourRequest.putDeliveryRequest(deliveryRequest);

        return tourRequest;
    }
    
     /**
     * Removes a DeliveryRequest from a TourRequest.
     * 
     * @param tourRequest The TourRequest from which the delivery request will be removed.
     * @param deliveryRequest The DeliveryRequest to remove.
     * @return The updated TourRequest with the delivery request removed.
     * @throws IOException If there is an issue during the removal process.
     */
    public TourRequest removeDeliveryRequest(TourRequest tourRequest, DeliveryRequest deliveryRequest) throws IOException {
        tourRequest.removeDeliveryRequest(deliveryRequest);

        return tourRequest;
    }
    
     /**
     * Changes the pickup point for a DeliveryRequest within a TourRequest.
     * 
     * @param tourRequest The TourRequest containing the delivery request.
     * @param deliveryRequest The DeliveryRequest to update.
     * @param pickupPoint The new pickup point ID.
     * @return The updated TourRequest with the modified pickup point.
     * @throws IOException If there is an issue during the update process.
     */
    public TourRequest changePickupPoint(TourRequest tourRequest, DeliveryRequest deliveryRequest, Long pickupPoint) throws IOException {
        java.util.Map<String, DeliveryRequest> requests = tourRequest.getRequests();
        requests.get(deliveryRequest.getId()).setPickupPoint(pickupPoint);

        /// TODO : RECOMPUTE BEST TOUR !!!
        return tourRequest;
    }
    
    /**
     * Changes the delivery point for a DeliveryRequest within a TourRequest.
     * 
     * @param tourRequest The TourRequest containing the delivery request.
     * @param deliveryRequest The DeliveryRequest to update.
     * @param deliveryPoint The new delivery point ID.
     * @return The updated TourRequest with the modified delivery point.
     * @throws IOException If there is an issue during the update process.
     */
    public TourRequest changeDeliveryPoint(TourRequest tourRequest, DeliveryRequest deliveryRequest, Long deliveryPoint) throws IOException {
        java.util.Map<String, DeliveryRequest> requests = tourRequest.getRequests();
        requests.get(deliveryRequest.getId()).setPickupPoint(deliveryPoint);

        /// TODO : RECOMPUTE BEST TOUR !!!
        return tourRequest;
    }
    
    /**
     * Retrieves the absolute path of the project's root directory.
     * 
     * @return The absolute path of the project directory.
     */
    private String getProjectDirectory() {
        // Obtenir le chemin absolu du projet à partir de la classe
        String projectDir = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        // Remonter au répertoire racine du projet
        File projectPath = new File(projectDir).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile(); // Remonte deux niveaux

        return projectPath.getAbsolutePath(); // Retourne uniquement la racine
    }
    
    /**
     * Saves a list of tours to a file in XML format.
     * 
     * @param tours The list of tours to save.
     * @return A boolean indicating whether the save operation was successful.
     */
    public Boolean saveToursToFile(List<Tour> tours) {
        Boolean resultat = false;
        try {
            // Initialiser le constructeur de document XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Créer le document XML
            Document doc = builder.newDocument();

            // Élément racine <tours>
            Element root = doc.createElement("tours");
            String todayDate = LocalDate.now().toString(); // Obtenir la date au format AAAA-MM-JJ
            root.setAttribute("date", todayDate); // Ajouter l'attribut "date"
            doc.appendChild(root);

            // Parcourir les tours
            for (Tour tour : tours) {
                Element paraTour = doc.createElement("tour");
                paraTour.setAttribute("id", tour.getId());
                paraTour.setAttribute("duration", String.valueOf(tour.getDuration().getSeconds()));
                root.appendChild(paraTour);

                // Parcourir les intersections
                for (Intersection intersection : tour.getPointslist()) {
                    Element parIntersection = doc.createElement("intersection");
                    parIntersection.setAttribute("id", intersection.getId().toString());
                    paraTour.appendChild(parIntersection);

                    // Ajouter latitude et longitude
                    Element lat = doc.createElement("latitude");
                    lat.appendChild(doc.createTextNode(intersection.getLocation().getLatitude().toString()));

                    Element lon = doc.createElement("longitude");
                    lon.appendChild(doc.createTextNode(intersection.getLocation().getLongitude().toString()));

                    parIntersection.appendChild(lat);
                    parIntersection.appendChild(lon);
                }
            }

            // Transformer le document en fichier XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);

            // Obtenir le chemin absolu du projet
            String filePath = getProjectDirectory() + "/data/" + todayDate + ".xml"; // Correctement structuré
            File file = new File(filePath);

            // Vérifier si le répertoire parent existe et le créer si nécessaire
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            // Sauvegarder le fichier
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            System.out.println("Fichier XML enregistré avec succès dans : " + file.getAbsolutePath());
            resultat = true;

        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Erreur lors de la création du fichier XML : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
        }

        return resultat;
    }

   // Fonction pour calculer et attribuer les tours aux livreurs
    
    /**
     * This method calculates and assigns tours to couriers based on delivery requests.
     * 
     * The process involves several steps:
     * 1. Sorts the delivery requests by proximity to the warehouse.
     * 2. Creates a given number of couriers and initializes them with empty tour requests.
     * 3. Assigns the sorted delivery requests to each courier in a round-robin fashion.
     * 4. Calculates the optimal route for each courier based on the requests assigned to them.
     * 5. Returns a map containing the couriers and their respective calculated tours.
     * 
     * @param tourRequest The object representing the tour request, which contains information about the warehouse and delivery requests.
     * @param map The object representing the map used to determine proximity and calculate routes.
     * @param numCouriers The number of couriers to create and assign to handle the deliveries.
     * @return A map where the keys are the courier IDs and the values are the Courier objects with their assigned tour.
     */
    public HashMap<Long, Courier> computeAndAssignTour(TourRequest tourRequest, Map map, int numCouriers) {

        ComputeTourUtilTools computeTourUtil = new ComputeTourUtilTools();

        // 1. Trier les requêtes par proximité au warehouse
        List<DeliveryRequest> sortedRequests = computeTourUtil.sortRequestsByProximityToWarehouse(map, tourRequest);

        // 2. Créer les livreurs
        HashMap<Long, Courier> couriers = new HashMap<>();
        for (long i = 0; i < numCouriers; i++) {
            Courier courier = new Courier();
            // Initialiser le TourRequest avec l'entrepôt
            TourRequest requests = new TourRequest(null, tourRequest.getWarehouse());
            courier.setTourRequest(requests);
            courier.setId(i + 1); // Les IDs commencent à 1
            courier.setIsAvailable(true);
            couriers.put(i + 1, courier);
        }

        // 3. Distribuer les requêtes entre les livreurs
        int courierIndex = 0;
        for (DeliveryRequest request : sortedRequests) {
            // Assigner la requête au livreur correspondant
            Courier courier = couriers.get((long) (courierIndex + 1)); // Les IDs des courriers commencent à 1
            courier.addRequestToCourier(request);

            // Passer au livreur suivant
            courierIndex = (courierIndex + 1) % numCouriers; // Répartition circulaire
        }

        // 4. Calculer l'itinéraire pour chaque livreur
        for (Courier courier : couriers.values()) {
            // Calculer le tour du livreur avec ses requêtes
            Tour courierTour = computeTour(courier.getTourRequest(), map);
            courier.setDeliveryPlan(courierTour);
        }

        // 5. Retourner la map avec les livreurs
        return couriers;
    }

}
