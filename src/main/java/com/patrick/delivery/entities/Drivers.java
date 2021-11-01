package com.patrick.delivery.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "drivers")
public class Drivers extends AuditableWithId implements Serializable {

    // Rider not accepting any
    public static final String STATE_OFF="Off";
    // Available to accept orders
    public static final String STATE_AVAILABLE="Available";
    // Rider has been assigned an order but has not accepted yet
    public static final String STATUS_ASSIGNED="Assigned";
    // Rider has accepted the order and is enroute to pick the goods
    public static final String STATUS_ENROUTE="Enroute";
    // Rider has picked the goods and is headed to customer
    public static final String STATUS_ENGAGED="Engaged";

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status")
    private String status;

    @Column(name = "driver_state")
    private String driverState;

    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    private Users userLink;

    public Users getUserLink() {
        return userLink;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverState() {
        return driverState;
    }

    public void setDriverState(String driverState) {
        this.driverState = driverState;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
