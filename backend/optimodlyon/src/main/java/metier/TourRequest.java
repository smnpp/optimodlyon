/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier;


import java.util.Map;
import java.util.UUID;

/**
 *
 * @author simonperret
 */
public class TourRequest {
    private final String id;
    private Map<String, DeliveryRequest> requests;
    private Warehouse warehouse;

    public TourRequest(Map<String, DeliveryRequest> requests, Warehouse warehouse) {
        this.id = UUID.randomUUID().toString();
        this.requests = requests;
        this.warehouse = warehouse;
    }

    public String getId() {
        return id;
    }

    public Map<String, DeliveryRequest> getRequests() {
        return requests;
    }

    public void setRequests(Map<String, DeliveryRequest> requests) {
        this.requests = requests;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    
    public void putDeliveryRequest(DeliveryRequest deliveryRequest) {
        this.requests.put(deliveryRequest.getId(), deliveryRequest);
    }
    
    public void removeDeliveryRequest(DeliveryRequest deliveryRequest) {
        this.requests.remove(deliveryRequest.getId());
    }

    @Override
    public String toString() {
        return "TourRequest{" + "id=" + id + ", requests=" + requests + ", warehouse=" + warehouse + '}';
    }   
}