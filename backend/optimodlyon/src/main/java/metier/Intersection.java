/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier;

/**
 *
 * @author simonperret
 */
public class Intersection {

    private Long id;
    private Coords location;

    public Intersection(Long id, Coords location) {
        this.id = id;
        this.location = location;
    }

    public Coords getLocation() {
        return location;
    }

    public void setLocation(Coords location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Intersection{" + "id=" + id + ", location=" + location + '}';
    }
    

}
