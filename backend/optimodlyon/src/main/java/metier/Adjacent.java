/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier;

/**
 *
 * @author simonperret
 */
public class Adjacent {

    private Intersection destination;
    private String segmentName;
    private Double length;

    public Adjacent(Intersection destination, String name, Double length) {
        this.destination = destination;
        this.segmentName = name;
        this.length = length;
    }

    public Intersection getDestination() {
        return destination;
    }

    public void setDestination(Intersection destination) {
        this.destination = destination;
    }

    public String getName() {
        return segmentName;
    }

    public void setName(String name) {
        this.segmentName = name;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Adjacent{" 
                + " destination=" + destination
                + ", segmentName='" + segmentName + '\''
                + ", length=" + length
                + '}';
    }
}
