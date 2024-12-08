/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier;

/**
 *
 * @author simonperret
 */
import java.util.HashMap;

public class Intersection {

    private Long id;
    private Coords location;
    private HashMap<Long, Adjacent> adjacents;

    public Intersection(Long id, Coords location) {
        this.id = id;
        this.location = location;
        this.adjacents = new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public Coords getLocation() {
        return location;
    }

    public void setLocation(Coords location) {
        this.location = location;
    }
    
    public HashMap<Long, Adjacent> getAdjacents() {
        return adjacents;
    }

    public void setAdjacents(HashMap<Long, Adjacent> adjacents) {
        this.adjacents = adjacents;
    }

    public void addAdjacent(Long id, Adjacent adj) {
        adjacents.put(id, adj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Intersection{ id=").append(id)
          .append(", location = ").append(location)
          .append(", adjacents = [ ");
        for (Adjacent adjacent : adjacents.values()) {
            sb.append(adjacent).append(", ");
        }
        if (!adjacents.isEmpty()) {
            sb.setLength(sb.length() - 2); // Retirer la dernière virgule et espace
        }
        sb.append(" ] }");
        return sb.toString();
    }
}

