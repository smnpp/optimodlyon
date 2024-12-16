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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import javafx.util.Pair;
import metier.Adjacent;
import metier.DeliveryRequest;
import metier.Intersection;
import metier.Map;
import metier.Warehouse;
import metier.TourRequest;
import metier.Coords;
import metier.Tour;

public class ComputeTourUtilTools {
    
    // Vitesse constante des livreurs en kilomètres par heure
    public static final double COURIER_SPEED_KM_PER_HOUR = 15.0;
    // Coefficient de la sous map de calcul
    public static final double RADIUS_COEF = 20.0;
    
    // Fonction pour calculer la distance geographique entre deux intersections à partir des cooredonnées
    public static double calculateDistance(Intersection i1, Intersection i2) {
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
    public static double calculateSegmentDistance(List<Long> segmentPath, Map map) {
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
    public static Duration calculateTravelTime(double distance) {
        double timeInSeconds = distance / (COURIER_SPEED_KM_PER_HOUR / 3.6); // Utilise la vitesse globale
        return Duration.ofSeconds((long) (timeInSeconds)); // Convertit en secondes
    }

    public List<DeliveryRequest> sortRequestsByProximityToWarehouse(Map map, TourRequest tourRequest) {
        // Récupérer l'entrepôt du TourRequest
        Warehouse warehouse = tourRequest.getWarehouse();  // Warehouse devrait être correctement importé

        // Convertir les requêtes de livraison dans une liste (si elles sont dans une Map)
        List<DeliveryRequest> requests = new ArrayList<>(tourRequest.getRequests().values());  // Si tourRequest.getRequests() renvoie un Map, on le convertit en List

        // Trier les requêtes en fonction de la distance entre leur pickup et l'entrepôt
        requests.sort((request1, request2) -> {
            Long pickupPointId1 = request1.getPickupPoint();
            Long pickupPointId2 = request2.getPickupPoint();

            Intersection pickupIntersection1 = map.getIntersections().get(pickupPointId1);
            Intersection pickupIntersection2 = map.getIntersections().get(pickupPointId2);

            if (pickupIntersection1 == null || pickupIntersection2 == null) {
                throw new IllegalArgumentException("Intersection with id not found in map.");
            }

            // Calculer la distance de chaque pickup à l'entrepôt
            double distance1 = calculateDistance(pickupIntersection1, map.getIntersections().get(warehouse.getId()));
            double distance2 = calculateDistance(pickupIntersection2, map.getIntersections().get(warehouse.getId()));

            // Comparer les distances
            return Double.compare(distance1, distance2);
        });

        return requests;
    }
    
    // Fonction pour trouver le point le plus proche d'un point de vue géographique respectant les contraintes
    public static Long findClosestPoint(Long currentPoint, List<Long> candidates, Map map, HashMap<Long, Boolean> visited, HashMap<String, DeliveryRequest> requests) {
        double minDistance = Double.MAX_VALUE;
        Long closestPoint = null;

        for (Long candidate : candidates) {
            // Vérification des contraintes (pickup avant delivery)
            if (isValidPoint(candidate, visited, requests)) {
                double distance = calculateDistance(map.getIntersections().get(currentPoint), map.getIntersections().get(candidate));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPoint = candidate;
                }
            }
        }

        return closestPoint;
    }
    
    // Fonction pour trouver le point le plus proche d'un point de vue des segments respectant les contraintes
    public static Long findClosestPoint(Long currentPoint, List<Long> candidates, Map map, HashMap<Long, Boolean> visited, HashMap<String, DeliveryRequest> requests, HashMap<Pair<Long, Long>, List<Long>> shortestPaths) {
        double minDistance = Double.MAX_VALUE;
        Long closestPoint = null;

        for (Long candidate : candidates) {
            // Vérification des contraintes (pickup avant delivery)
            if (isValidPoint(candidate, visited, requests)) {
                
                List<Long> segmentPath = shortestPaths.get(new Pair<>(currentPoint, candidate));
                double distance = calculateSegmentDistance(segmentPath, map);
                //double distance = calculateDistance(map.getIntersections().get(currentPoint), map.getIntersections().get(candidate));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPoint = candidate;
                }
            }
        }

        return closestPoint;
    }
    
    // Fonction principale pour ordonnancer les requêtes d'un point de vue géographique 
    public static List<Long> scheduleOptimizedDeliveryRequests(TourRequest tourRequest, Map map) {
        // Liste pour l'ordre final des points à visiter
        List<Long> orderedPoints = new ArrayList<>();

        // Récupération des requêtes de livraison et initialisation
        HashMap<String, DeliveryRequest> requests = (HashMap<String, DeliveryRequest>) tourRequest.getRequests();
        HashMap<Long, Boolean> visited = new HashMap<>();
        Long warehouseId = tourRequest.getWarehouse().getId();

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
    public static List<Long> scheduleOptimizedDeliveryRequests(TourRequest tourRequest, Map map, HashMap<Pair<Long, Long>, List<Long>> shortestPaths) {
        // Liste pour l'ordre final des points à visiter
        List<Long> orderedPoints = new ArrayList<>();

        // Récupération des requêtes de livraison et initialisation
        HashMap<String, DeliveryRequest> requests = (HashMap<String, DeliveryRequest>) tourRequest.getRequests();
        HashMap<Long, Boolean> visited = new HashMap<>();
        Long warehouseId = tourRequest.getWarehouse().getId();

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
    public static Map filterMapByZone(Map originalMap, Intersection center, double radius) {
        HashMap<Long, Intersection> filteredIntersections = new HashMap<>();
        HashSet<Long> toAdd = new HashSet<>(); // Ensemble temporaire pour les ajouts

        // Étape 1 : Ajouter les intersections dans le rayon
        for (Intersection intersection : originalMap.getIntersections().values()) {
            double distance = calculateDistance(center, intersection);
            if (distance <= radius) {
                filteredIntersections.put(intersection.getId(), intersection);
            }
        }

        // Étape 2 : Inclure les adjacents des intersections filtrées
        for (Intersection intersection : filteredIntersections.values()) {
            HashMap<Long, Adjacent> filteredAdjacents = new HashMap<>();
            for (Adjacent adjacent : intersection.getAdjacents().values()) {
                Long neighborId = adjacent.getDestination().getId();

                if (filteredIntersections.containsKey(neighborId)) {
                    // Si l'adjacent est déjà dans filteredMap, on le conserve
                    filteredAdjacents.put(neighborId, adjacent);
                } else {
                    // Étape 3 : Vérifier si l'adjacent externe a d'autres connexions internes
                    Intersection externalIntersection = originalMap.getIntersections().get(neighborId);
                    if (externalIntersection != null) {
                        int internalConnections = 0;
                        HashMap<Long, Adjacent> internalAdjacents = new HashMap<>();
                        for (Adjacent externalAdjacent : externalIntersection.getAdjacents().values()) {
                            if (filteredIntersections.containsKey(externalAdjacent.getDestination().getId()) &&
                                !externalAdjacent.getDestination().getId().equals(intersection.getId())) {
                                internalAdjacents.put(externalAdjacent.getDestination().getId(), externalAdjacent);
                                internalConnections++;
                            }

                            if (internalConnections > 0) {
                                // Ajouter l'externalIntersection à l'ensemble temporaire
                                externalIntersection.setAdjacents(internalAdjacents);
                                toAdd.add(neighborId);
                                filteredAdjacents.put(neighborId, adjacent);
                                break; // Une seule connexion interne supplémentaire suffit
                            }
                        }
                    }
                }
            }
            intersection.setAdjacents(filteredAdjacents);
        }

        // Ajouter les intersections collectées à la map filtrée
        for (Long id : toAdd) {
            filteredIntersections.put(id, originalMap.getIntersections().get(id));
        }

        return new Map(filteredIntersections);
    }

    // Dijkstra modifié pour inclure les chemins complets
    public static HashMap<Long, PathResult> computeShortestPathsFromSourceWithPaths(Long sourceId, Map map) {
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
    public static HashMap<Pair<Long, Long>, List<Long>> computeAllShortestPathsWithPaths(Map map) {
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
    
    
    public static Tour constructTourWithGeographicZones(List<Long> orderedPoints, Map map) {
       
        List<Intersection> fullPath = new ArrayList<>();
        Duration totalDuration = Duration.ZERO;
        Long previousPoint = null;

        for (Long currentPoint : orderedPoints) {
            try {
                if (previousPoint != null) {
                    // Récupérer les intersections pour les points successifs
                    Intersection prevIntersection = map.getIntersections().get(previousPoint);
                    Intersection currIntersection = map.getIntersections().get(currentPoint);

                    if (prevIntersection == null || currIntersection == null) {
                        throw new IllegalArgumentException("Intersection manquante : " +
                                "previousPoint = " + previousPoint + ", currentPoint = " + currentPoint);
                    }

                    // Calculer le centre de la zone
                    double midLat = (prevIntersection.getLocation().getLatitude() + currIntersection.getLocation().getLatitude()) / 2.0;
                    double midLon = (prevIntersection.getLocation().getLongitude() + currIntersection.getLocation().getLongitude()) / 2.0;
                    Intersection center = new Intersection(-1L, new Coords(midLat, midLon));

                    // Calculer le rayon de la zone
                    double radius = RADIUS_COEF * calculateDistance(prevIntersection, currIntersection);

                    // Filtrer la carte pour créer une zone restreinte
                    Map filteredMap = filterMapByZone(map, center, radius);
                    
                    System.out.println("\n\nRayon utilisé pour le filtrage : " + radius);

                    
                    if (filteredMap.getIntersections().isEmpty()) {
                        throw new IllegalArgumentException("La zone filtrée est vide : " +
                                "previousPoint = " + previousPoint + ", currentPoint = " + currentPoint);
                    }
                    
                    System.out.println("Vérification du sous-graphe pour previousPoint et currentPoint :");
                    System.out.println("PreviousPoint (" + previousPoint + ") présent : " + filteredMap.getIntersections().containsKey(previousPoint));
                    System.out.println("CurrentPoint (" + currentPoint + ") présent : " + filteredMap.getIntersections().containsKey(currentPoint));

                    System.out.println("Adjacents de previousPoint (" + previousPoint + ") :");
                    for (Adjacent adjacent : filteredMap.getIntersections().get(previousPoint).getAdjacents().values()) {
                        System.out.println("-> Vers " + adjacent.getDestination().getId() + " (Longueur : " + adjacent.getLength() + ")");
                    }

                    System.out.println("Adjacents de currentPoint (" + currentPoint + ") :");
                    for (Adjacent adjacent : filteredMap.getIntersections().get(currentPoint).getAdjacents().values()) {
                        System.out.println("-> Vers " + adjacent.getDestination().getId() + " (Longueur : " + adjacent.getLength() + ")");
                    }

                    Set<Long> visited = new HashSet<>();
                    Queue<Long> queue = new LinkedList<>();
                    queue.add(previousPoint);

                    while (!queue.isEmpty()) {
                        Long current = queue.poll();
                        if (!visited.contains(current)) {
                            visited.add(current);
                            for (Adjacent adjacent : filteredMap.getIntersections().get(current).getAdjacents().values()) {
                                if (!visited.contains(adjacent.getDestination().getId())) {
                                    queue.add(adjacent.getDestination().getId());
                                }
                            }
                        }
                    }

                    System.out.println("Nombre de noeuds atteignables depuis previousPoint : " + visited.size());
                    if (!visited.contains(currentPoint)) {
                        System.err.println("currentPoint n'est pas atteignable depuis previousPoint dans le sous-graphe.");
                    }

                    
                    

                    // Calculer le plus court chemin dans la zone
                    HashMap<Long, PathResult> shortestPaths = computeShortestPathsFromSourceWithPaths(previousPoint, filteredMap);
                    //System.out.println("Chemins disponibles : " + shortestPaths.keySet());
                    if (!shortestPaths.containsKey(currentPoint)) {
                        throw new IllegalArgumentException("Aucun chemin trouvé pour : " +
                                "previousPoint = " + previousPoint + ", currentPoint = " + currentPoint);
                    }


                    List<Long> segmentPath = shortestPaths.get(currentPoint).getPath();
                    System.out.println("Chemin pour " + currentPoint + ": " + segmentPath);
                    if (segmentPath == null || segmentPath.isEmpty()) {
                        System.err.println("Chemin vide pour previousPoint = " + previousPoint + ", currentPoint = " + currentPoint);
                        System.err.println("Sous-graphe généré contient previousPoint : " + filteredMap.getIntersections().containsKey(previousPoint));
                        System.err.println("Sous-graphe généré contient currentPoint : " + filteredMap.getIntersections().containsKey(currentPoint));
                        throw new IllegalArgumentException("Segment vide pour : previousPoint = " + previousPoint + ", currentPoint = " + currentPoint);
                    }


                    // Ajouter les points au chemin
                    for (int i = (fullPath.isEmpty() ? 0 : 1); i < segmentPath.size(); i++) {
                        fullPath.add(map.getIntersections().get(segmentPath.get(i)));
                    }

                    // Calculer la durée pour ce segment
                    double segmentDistance = calculateSegmentDistance(segmentPath, filteredMap);
                    totalDuration = totalDuration.plus(calculateTravelTime(segmentDistance));
                } else {
                    // Ajouter le premier point de départ
                    fullPath.add(map.getIntersections().get(currentPoint));
                }

                previousPoint = currentPoint;

            } catch (Exception e) {
                System.err.println("\nErreur lors du traitement du segment : previousPoint = " + previousPoint +
                        ", currentPoint = " + currentPoint);
                e.printStackTrace();
                throw e; // Propager l'erreur après log
            }
        }

        // Création de l'objet Tour
        Tour tour = new Tour();
        tour.setDuration(totalDuration);
        tour.setPointslist(fullPath);
        return tour;
    }
  
    public static Tour constructTourWithSpecificShortestPaths(List<Long> orderedPoints, Map map) {
        // Liste complète des intersections du tour
        List<Intersection> fullPath = new ArrayList<>();
        Duration totalDuration = Duration.ZERO;
        Long previousPoint = null;

        for (Long currentPoint : orderedPoints) {
            if (previousPoint != null) {
                // Calculer à la demande le chemin entre deux points
                HashMap<Long, PathResult> shortestPaths = computeShortestPathsFromSourceWithPaths(previousPoint, map);
                List<Long> segmentPath = shortestPaths.get(currentPoint).getPath();

                // Ajouter les intersections de ce segment sans duplications
                for (int i = (fullPath.isEmpty() ? 0 : 1); i < segmentPath.size(); i++) {
                    fullPath.add(map.getIntersections().get(segmentPath.get(i)));
                }

                // Calculer la durée pour ce segment
                double segmentDistance = calculateSegmentDistance(segmentPath, map);
                totalDuration = totalDuration.plus(calculateTravelTime(segmentDistance));
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
     
    public static Tour constructTourWithAllShortestPaths(List<Long> orderedPoints, Map map, HashMap<Pair<Long, Long>, List<Long>> shortestPaths) {
        // Liste complète des intersections du tour
        List<Intersection> fullPath = new ArrayList<>();
        

        // Calcul de la durée totale
        Duration totalDuration = Duration.ZERO;
        Long previousPoint = null;

        for (Long currentPoint : orderedPoints) {
            if (previousPoint != null) {
                // Récupérer le chemin complet entre deux points
                List<Long> segmentPath = shortestPaths.get(new Pair<>(previousPoint, currentPoint));
                            
                // Ajouter les intersections de ce chemin, sauf le premier point s'il est déjà dans fullPath
                for (int i = (fullPath.isEmpty() ? 0 : 1); i < segmentPath.size(); i++) {
                    fullPath.add(map.getIntersections().get(segmentPath.get(i)));
                }

                // Calculer la durée pour ce segment
                double segmentDistance = calculateSegmentDistance(segmentPath, map);
                totalDuration = totalDuration.plus(calculateTravelTime(segmentDistance));
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
    
}
