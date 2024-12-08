/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package metier;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

/**
 *
 * @author simonperret
 */

public class DeliveryRequest {
    private final String id;
    private Long pickupPoint;
    private Long deliveryPoint;
    private Duration pickupDuration;
    private Duration deliveryDuration;
    private LocalTime pickupTime;
    private LocalTime deliveryTime;
    private Duration duration;

    public DeliveryRequest(Long pickupPoint, Long deliveryPoint, Duration pickupDuration, Duration deliveryDuration) {
        this.id = UUID.randomUUID().toString();
        this.pickupPoint = pickupPoint;
        this.deliveryPoint = deliveryPoint;
        this.pickupDuration = pickupDuration;
        this.deliveryDuration = deliveryDuration;
    }

    public String getId() {
        return id;
    }

    public Long getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(Long pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public Long getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(Long deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public Duration getPickupDuration() {
        return pickupDuration;
    }

    public void setPickupDuration(Duration pickupDuration) {
        this.pickupDuration = pickupDuration;
    }

    public Duration getDeliveryDuration() {
        return deliveryDuration;
    }

    public void setDeliveryDuration(Duration deliveryDuration) {
        this.deliveryDuration = deliveryDuration;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public LocalTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "DeliveryRequest{" + "id=" + id + ", pickupPoint=" + pickupPoint + ", deliveryPoint=" + deliveryPoint + ", pickupDuration=" + pickupDuration + ", deliveryDuration=" + deliveryDuration + ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime + ", duration=" + duration + '}';
    }
}