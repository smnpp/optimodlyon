/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier;

import java.time.LocalTime;

/**
 *
 * @author jnoukam
 */
public class Warehouse {
    
    private Long id;
    private LocalTime departureTime;

    public Warehouse(Long id, LocalTime departureTime) {
        this.id = id;
        this.departureTime = departureTime;
    }
 
    public Long getId() {
        return id;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }
    
    @Override
    public String toString() {
        return "Warehouse{" + "id=" + id + '}';
    }
    
}
