/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier;

import java.util.List;

/**
 *
 * @author simonperret
 */
public class Map {

    private List<Intersection> intersections;
    private List<Segment> segments;

    public Map(List<Intersection> intersections, List<Segment> segments) {
        this.intersections = intersections;
        this.segments = segments;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<Intersection> intersections) {
        this.intersections = intersections;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==== Map Details ====\n");

        sb.append("Intersections:\n");
        for (Intersection intersection : intersections) {
            sb.append(intersection).append("\n");
        }

        sb.append("Segments:\n");
        for (Segment segment : segments) {
            sb.append(segment).append("\n");
        }

        return sb.toString();
    }
}
