/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// Utility class to represent a pair of intersection ID and distance
package util.tsp;

/**
 *
 * @author jnoukam
 */
public class UtilPair {
    
    public Long intersectionId;
    public Double distance;

    public UtilPair(Long intersectionId, Double distance) {
        this.intersectionId = intersectionId;
        this.distance = distance;
    }
        
}
