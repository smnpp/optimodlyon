/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier;

import java.time.LocalTime;
import java.util.UUID;

/**
 *
 * @author simonperret
 */
public class Warehouse {
    private final String id;
    private Long address;
    private LocalTime departureTime;

    public Warehouse(Long address, LocalTime departureTime) {
        this.id = UUID.randomUUID().toString();
        this.address = address;
        this.departureTime = departureTime;
    }

    public String getId() {
        return id;
    }

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "Warehouse{" + "id=" + id + ", address=" + address + ", departureTime=" + departureTime + '}';
    }
}