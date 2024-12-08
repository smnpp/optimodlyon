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
    private Long adresse;
    private LocalTime departureTime;

    public Warehouse(Long adresse, LocalTime departureTime) {
        this.id = adresse;
        this.adresse = adresse;
        this.departureTime = departureTime;
    }

    public void setId(Long id) {
        this.id = id;
    }
 
    public Long getId() {
        return id;
    }

    public Long getAdresse() {
        return adresse;
    }

    public void setAdresse(Long adresse) {
        this.adresse = adresse;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }
    
    @Override
    public String toString() {
        return "Warehouse{" + "id=" + id + ", adresse=" + adresse + '}';
    }
    
}
