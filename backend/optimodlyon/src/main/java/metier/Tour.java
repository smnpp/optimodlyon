/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier;

import java.time.Duration;
import java.util.List;

/**
 *
 * @author jnoukam
 */
public class Tour {
    
    private Long id;
    private List<Intersection> pointslist;
    private Duration duration;

    public Tour() {
    }

    public Long getId() {
        return id;
    }

    public List<Intersection> getPointslist() {
        return pointslist;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPointslist(List<Intersection> pointslist) {
        this.pointslist = pointslist;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Tour{" + "id=" + id + ", pointslist=" + pointslist + ", duration=" + duration + '}';
    }
    
 }
