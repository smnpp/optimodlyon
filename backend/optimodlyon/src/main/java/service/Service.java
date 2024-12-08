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
import util.tsp.ComputeTourUtilTools;
import util.FileParser;
import util.FileParserFactory;
import util.FileType;
import util.tsp.PathResult;

/**
 *
 * @author jnoukam
 */
public class Service {

    public Map loadMap(String fileContent, String fileName) throws IOException {

        // Déterminer le type de fichier
        File file = File.createTempFile("temp", ".xml");
        file.deleteOnExit();

        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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

    public TourRequest loadRequestFile(String fileContent, String fileName) throws IOException {

        // Déterminer le type de fichier
        File file = File.createTempFile("temp", ".xml");
        file.deleteOnExit();

        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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
    
    
    public static Tour computeTour(List<Long> orderedPoints, Map map) {
        // Liste complète des intersections du tour
        List<Intersection> fullPath = new ArrayList<>();
        ComputeTourUtilTools djikstraUtilTools = new ComputeTourUtilTools();
        Duration totalDuration = Duration.ZERO;
        Long previousPoint = null;

        for (Long currentPoint : orderedPoints) {
            if (previousPoint != null) {
                // Calculer à la demande le chemin entre deux points
                HashMap<Long, PathResult> shortestPaths = djikstraUtilTools.computeShortestPathsFromSourceWithPaths(previousPoint, map);
                List<Long> segmentPath = shortestPaths.get(currentPoint).getPath();

                // Ajouter les intersections de ce segment sans duplications
                for (int i = (fullPath.isEmpty() ? 0 : 1); i < segmentPath.size(); i++) {
                    fullPath.add(map.getIntersections().get(segmentPath.get(i)));
                }

                // Calculer la durée pour ce segment
                double segmentDistance = djikstraUtilTools.calculateSegmentDistance(segmentPath, map);
                totalDuration = totalDuration.plus(djikstraUtilTools.calculateTravelTime(segmentDistance));
            } else {
                // Ajouter le premier point de départ
                fullPath.add(map.getIntersections().get(currentPoint));
            }

            previousPoint = currentPoint;
        }

        Tour tour = new Tour();
        tour.setDuration(totalDuration);
        tour.setPointslist(fullPath);
        return tour;
    }
    
    public DeliveryRequest createDeliveryRequest(Long pickupPoint, Long deliveryPoint, Long pickupDuration, Long deliveryDuration) throws IOException {
        Duration pickupDurationCast = Duration.ofSeconds(pickupDuration);
        Duration deliveryDurationCast = Duration.ofSeconds(deliveryDuration);
        DeliveryRequest deliveryRequest = new DeliveryRequest(pickupPoint, deliveryPoint, pickupDurationCast, deliveryDurationCast);
        
        return deliveryRequest;
    }
    
    public TourRequest addDeliveryRequest(TourRequest tourRequest, Long pickupPoint, Long deliveryPoint, Long pickupDuration, Long deliveryDuration) throws IOException {
        DeliveryRequest deliveryRequest = createDeliveryRequest(pickupPoint, deliveryPoint, pickupDuration, deliveryDuration);
        tourRequest.putDeliveryRequest(deliveryRequest);
        
        return tourRequest;
    }
    
    public TourRequest removeDeliveryRequest(TourRequest tourRequest, DeliveryRequest deliveryRequest) throws IOException {
        tourRequest.removeDeliveryRequest(deliveryRequest);
        
        return tourRequest;
    }
    
    public TourRequest changePickupPoint(TourRequest tourRequest, DeliveryRequest deliveryRequest, Long pickupPoint) throws IOException {
        java.util.Map<String, DeliveryRequest> requests = tourRequest.getRequests();
        requests.get(deliveryRequest.getId()).setPickupPoint(pickupPoint);
        
        /// TODO : RECOMPUTE BEST TOUR !!!
        
        return tourRequest;
    }
    
    public TourRequest changeDeliveryPoint(TourRequest tourRequest, DeliveryRequest deliveryRequest, Long deliveryPoint) throws IOException {
        java.util.Map<String, DeliveryRequest> requests = tourRequest.getRequests();
        requests.get(deliveryRequest.getId()).setPickupPoint(deliveryPoint);
        
        /// TODO : RECOMPUTE BEST TOUR !!!
        
        return tourRequest;
    }
}

