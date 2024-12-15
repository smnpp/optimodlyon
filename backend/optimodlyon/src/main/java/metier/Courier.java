/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metier;

/**
 *
 * @author jnoukam
 */
public class Courier {

    private Long id;
    private Boolean isAvailable;
    private TourRequest tourRequest;
    private Tour deliveryPlan;

    // Constructeur par défaut
    public Courier() {
    }

    // Getter pour id
    public Long getId() {
        return id;
    }

    // Setter pour id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter pour isAvailable
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    // Setter pour isAvailable
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // Getter pour tourRequest
    public TourRequest getTourRequest() {
        return tourRequest;
    }

    // Setter pour tourRequest
    public void setTourRequest(TourRequest tourRequest) {
        this.tourRequest = tourRequest;
    }

    // Getter pour deliveryPlan
    public Tour getDeliveryPlan() {
        return deliveryPlan;
    }

    // Setter pour deliveryPlan
    public void setDeliveryPlan(Tour deliveryPlan) {
        this.deliveryPlan = deliveryPlan;
    }
    
    // Méthode pour ajouter une requête au livreur
    public void addRequestToCourier(DeliveryRequest request) {
        if (this.tourRequest == null) {
            this.tourRequest = new TourRequest(null, null);
        }
        this.tourRequest.putDeliveryRequest(request);
    }

    // Méthode toString pour une représentation sous forme de chaîne de caractères
    @Override
    public String toString() {
        return "Courier{" +
                "id=" + id +
                ", isAvailable=" + isAvailable +
                ", tourRequest=" + tourRequest +
                ", deliveryPlan=" + deliveryPlan +
                '}';
    }
}

