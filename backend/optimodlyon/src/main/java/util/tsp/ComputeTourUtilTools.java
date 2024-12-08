/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.tsp;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import javafx.util.Pair;
import metier.Adjacent;
import metier.DeliveryRequest;
import metier.Intersection;
import metier.Map;
import metier.TourRequest;

/**
 *Classe d'outils permettant de calculer le tour pour un DeliveryRequest
 * @author jnoukam
 */
public class ComputeTourUtilTools {
    
    // Vitesse constante des livreurs en kilomètres par heure
    public static final double COURIER_SPEED_KM_PER_HOUR = 15.0;
    // Coefficient de la sous map de calcul
    public final double RADIUS_COEF = 3.0;
    
    // Fonction pour calculer la distance geographique entre deux intersections à partir des cooredonnées
    public double calculateDistance(Intersection i1, Intersection i2) {
        // Exemple simplifié : distance Euclidienne
        double dx = i1.getLocation().getLatitude() - i2.getLocation().getLatitude();
        double dy = i1.getLocation().getLongitude() - i2.getLocation().getLongitude();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    // Vérifie si un point peut être visité (pickup avant delivery)
    public static boolean isValidPoint(Long point, HashMap<Long, Boolean> visited, HashMap<String, DeliveryRequest> requests) {
        for (DeliveryRequest request : requests.values()) {
            if (request.getDeliveryPoint().equals(point) && !visited.getOrDefault(request.getPickupPoint(), false)) {
                return false; // Ne peut pas visiter le delivery avant le pickup
            }
        }
        return true;
    }  
    
    // Fonction pour calculer la distance entre deux intersections en sommant les longueurs de chaque segment
    public double calculateSegmentDistance(List<Long> segmentPath, Map map) {
        double totalDistance = 0.0;
        for (int i = 0; i < segmentPath.size() - 1; i++) {
            Long fromId = segmentPath.get(i);
            Long toId = segmentPath.get(i + 1);
            Intersection from = map.getIntersections().get(fromId);
            Intersection to = map.getIntersections().get(toId);
            
            // System.out.println("From : "+ from); 
            
            // Vérifier si un segment existe entre les deux intersections
            if (from.getAdjacents().get(toId) != null) {
                totalDistance += from.getAdjacents().get(toId).getLength();
            } else {
                throw new IllegalArgumentException("No segment exists between Intersection " + fromId + " and " + toId);
            }
        }
        return totalDistance;
    }
    
    // Fonction pour calculer la durée pour parcourir une distance
    public Duration calculateTravelTime(double distance) {
        double timeInSeconds = distance / (COURIER_SPEED_KM_PER_HOUR / 3.6); // Utilise la vitesse globale
        return Duration.ofMinutes((long) (timeInSeconds / 60)); // Convertit en minutes
    }
    
    // Fonction pour trouver le point le plus proche d'un point de vue géographique respectant les contraintes
    public Long findClosestPoint(Long currentPoint, List<Long> candidates, Map map, HashMap<Long, Boolean> visited, HashMap<String, DeliveryRequest> requests) {
        double minDistance = Double.MAX_VALUE;
        Long closestPoint = null;

        // Calculer les chemins dynamiquement depuis currentPoint
        HashMap<Long, PathResult> shortestPaths = computeShortestPathsFromSourceWithPaths(currentPoint, map);

        for (Long candidate : candidates) {
            // Vérification des contraintes (pickup avant delivery)
            if (isValidPoint(candidate, visited, requests)) {
                //PathResult pathResult = shortestPaths.get(candidate);
                //if (pathResult != null) {
                    //double distance = pathResult.getDistance(); // Distance depuis currentPoint jusqu'à candidate
                    double distance = calculateDistance(map.getIntersections().get(currentPoint), map.getIntersections().get(candidate));
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestPoint = candidate;
                    }
                //}
            }
        }

        return closestPoint;
    }
    
    // Fonction pour trouver le point le plus proche d'un point de vue des segments respectant les contraintes
    public Long findClosestPoint(Long currentPoint, List<Long> candidates, Map map, HashMap<Long, Boolean> visited, HashMap<String, DeliveryRequest> requests, HashMap<Pair<Long, Long>, List<Long>> shortestPaths) {
        double minDistance = Double.MAX_VALUE;
        Long closestPoint = null;

        for (Long candidate : candidates) {
            // Vérification des contraintes (pickup avant delivery)
            if (isValidPoint(candidate, visited, requests)) {
                
                //List<Long> segmentPath = shortestPaths.get(new Pair<>(currentPoint, candidate));
                //double distance = calculateSegmentDistance(segmentPath, map);
                double distance = calculateDistance(map.getIntersections().get(currentPoint), map.getIntersections().get(candidate));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPoint = candidate;
                }
            }
        }

        return closestPoint;
    }
    
    // Fonction principale pour ordonnancer les requêtes d'un point de vue géographique 
    public List<Long> scheduleOptimizedDeliveryRequests(TourRequest tourRequest, Map map) {
        // Liste pour l'ordre final des points à visiter
        List<Long> orderedPoints = new ArrayList<>();

        // Récupération des requêtes de livraison et initialisation
        HashMap<String, DeliveryRequest> requests = (HashMap<String, DeliveryRequest>) tourRequest.getRequests();
        HashMap<Long, Boolean> visited = new HashMap<>();
        Long warehouseId = tourRequest.getWarehouse().getAdresse();

        // Initialisation des points à visiter (pickup et delivery)
        List<Long> allPoints = new ArrayList<>();
        for (DeliveryRequest request : requests.values()) {
            allPoints.add(request.getPickupPoint());
            allPoints.add(request.getDeliveryPoint());
        }

        // Ajouter le warehouse comme point de départ
        orderedPoints.add(warehouseId);
        Long currentPoint = warehouseId;

        // Ordonnancement basé sur les distances
        while (!allPoints.isEmpty()) {
            Long nextPoint = findClosestPoint(currentPoint, allPoints, map, visited, requests);
            if (nextPoint != null) {
                orderedPoints.add(nextPoint);
                allPoints.remove(nextPoint);
                visited.put(nextPoint, true);
            }
            currentPoint = nextPoint;
        }

        // Retourner au warehouse à la fin
        orderedPoints.add(warehouseId);

        return orderedPoints;
    }
    
    // Fonction principale pour ordonnancer les requêtes d'un point de vue des segments
    public List<Long> scheduleOptimizedDeliveryRequests(TourRequest tourRequest, Map map, HashMap<Pair<Long, Long>, List<Long>> shortestPaths) {
        // Liste pour l'ordre final des points à visiter
        List<Long> orderedPoints = new ArrayList<>();

        // Récupération des requêtes de livraison et initialisation
        HashMap<String, DeliveryRequest> requests = (HashMap<String, DeliveryRequest>) tourRequest.getRequests();
        HashMap<Long, Boolean> visited = new HashMap<>();
        Long warehouseId = tourRequest.getWarehouse().getAdresse();

        // Initialisation des points à visiter (pickup et delivery)
        List<Long> allPoints = new ArrayList<>();
        for (DeliveryRequest request : requests.values()) {
            allPoints.add(request.getPickupPoint());
            allPoints.add(request.getDeliveryPoint());
        }

        // Ajouter le warehouse comme point de départ
        orderedPoints.add(warehouseId);
        Long currentPoint = warehouseId;

        // Ordonnancement basé sur les distances
        while (!allPoints.isEmpty()) {
            Long nextPoint = findClosestPoint(currentPoint, allPoints, map, visited, requests, shortestPaths);
            if (nextPoint != null) {
                orderedPoints.add(nextPoint);
                allPoints.remove(nextPoint);
                visited.put(nextPoint, true);
            }
            currentPoint = nextPoint;
        }

        // Retourner au warehouse à la fin
        orderedPoints.add(warehouseId);

        return orderedPoints;
    }
    
    // Fonction pour réduire la map à une zone circulaire spécifique d'un point de vue géographique
    public Map filterMapByZone(Map originalMap, Intersection center, double radius) {
        HashMap<Long, Intersection> filteredIntersections = new HashMap<>();

        for (Intersection intersection : originalMap.getIntersections().values()) {
            double distance = calculateDistance(center, intersection);
            if (distance <= radius) {
                // Ajouter l'intersection dans la nouvelle map
                filteredIntersections.put(intersection.getId(), intersection);
            }
        }

        // Construire les segments adjacents pour les intersections filtrées
        for (Intersection intersection : filteredIntersections.values()) {
            HashMap<Long, Adjacent> filteredAdjacents = new HashMap<>();
            for (Adjacent adjacent : intersection.getAdjacents().values()) {
                if (filteredIntersections.containsKey(adjacent.getDestination().getId())) {
                    filteredAdjacents.put(adjacent.getDestination().getId(), adjacent);
                }
            }
            intersection.setAdjacents(filteredAdjacents);
        }

        return new Map(filteredIntersections);
    }
    
    // Dijkstra modifié pour inclure les chemins complets
    public HashMap<Long, util.tsp.PathResult> computeShortestPathsFromSourceWithPaths(Long sourceId, Map map) {
        PriorityQueue<util.tsp.UtilPair> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(pair -> pair.distance));
        HashMap<Long, Double> distances = new HashMap<>();
        HashMap<Long, List<Long>> paths = new HashMap<>();

        // Initialisation
        for (Long id : map.getIntersections().keySet()) {
            distances.put(id, Double.POSITIVE_INFINITY);
            paths.put(id, new ArrayList<>());
        }
        distances.put(sourceId, 0.0);
        paths.get(sourceId).add(sourceId);
        priorityQueue.add(new util.tsp.UtilPair(sourceId, 0.0));

        // Exploration
        while (!priorityQueue.isEmpty()) {
            util.tsp.UtilPair current = priorityQueue.poll();
            Long currentId = current.intersectionId;
            double currentDistance = current.distance;

            for (Adjacent adjacent : map.getIntersections().get(currentId).getAdjacents().values()) {
                Long neighborId = adjacent.getDestination().getId();
                double newDistance = currentDistance + adjacent.getLength();

                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);

                    // Mettre à jour le chemin
                    List<Long> newPath = new ArrayList<>(paths.get(currentId));
                    newPath.add(neighborId);
                    paths.put(neighborId, newPath);

                    priorityQueue.add(new util.tsp.UtilPair(neighborId, newDistance));
                }
            }
        }

        // Construction du résultat
        HashMap<Long, util.tsp.PathResult> results = new HashMap<>();
        for (HashMap.Entry<Long, Double> entry : distances.entrySet()) {
            results.put(entry.getKey(), new util.tsp.PathResult(entry.getValue(), paths.get(entry.getKey())));
        }
        return results;
    }
    
    // Fonction permettant de calculer tous les plus court chemins en utilisant l'algo de Dijkstra précédent
    public HashMap<Pair<Long, Long>, List<Long>> computeAllShortestPathsWithPaths(Map map) {
        HashMap<Pair<Long, Long>, List<Long>> allPaths = new HashMap<>();

        for (Long sourceId : map.getIntersections().keySet()) {
            // Appel de Dijkstra modifié pour inclure les chemins complets
            HashMap<Long, util.tsp.PathResult> shortestPathsFromSource = computeShortestPathsFromSourceWithPaths(sourceId, map);

            for (HashMap.Entry<Long, util.tsp.PathResult> entry : shortestPathsFromSource.entrySet()) {
                allPaths.put(
                    new Pair<>(sourceId, entry.getKey()), 
                    entry.getValue().getPath() // Séquence complète d'intersections
                );
            }
        }

        return allPaths;
    }
    
}
