/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author simonperret
 */
public class Map {

    private HashMap<Long, Intersection> intersections;

    public Map(HashMap<Long, Intersection> mapData) {
        this.intersections = mapData; 
    }

    public HashMap<Long, Intersection> getIntersections() {
        return intersections;
    }

    public void addIntersection(Intersection intersection) {
        intersections.put(intersection.getId(), intersection);
    }

    public void displayMap() {
        for (Intersection intersection : intersections.values()) {
            System.out.println(intersection);
        }
    }

    @Override
    public String toString() {
            StringBuilder sb = new StringBuilder("Map:\n");
            for (Intersection intersection : intersections.values()) {
            sb.append(intersection).append("\n");
        }
        return sb.toString();
    }

}

