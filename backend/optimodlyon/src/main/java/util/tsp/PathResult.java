/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.tsp;

import java.util.List;

/**
 *
 * @author jnoukam
 */
public class PathResult {
    
    public double distance;
    public List<Long> path;

    public PathResult(double distance, List<Long> path) {
        this.distance = distance;
        this.path = path;
    }

    public double getDistance() {
        return distance;
    }

    public List<Long> getPath() {
        return path;
    }
}
