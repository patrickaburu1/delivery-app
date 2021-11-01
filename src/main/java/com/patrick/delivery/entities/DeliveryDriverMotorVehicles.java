package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author patrick on 6/16/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "driver_motor_vehicles")
public class DeliveryDriverMotorVehicles extends AuditableWithId implements Serializable {

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DeliveryMotorVehicles deliveryMotorVehicleAssigned;

    @JsonIgnore
    @JoinColumn(name = "driver_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Drivers driverAssigned;


    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DeliveryMotorVehicles getDeliveryMotorVehicleAssigned() {
        return deliveryMotorVehicleAssigned;
    }

    public void setDeliveryMotorVehicleAssigned(DeliveryMotorVehicles deliveryMotorVehicleAssigned) {
        this.deliveryMotorVehicleAssigned = deliveryMotorVehicleAssigned;
    }

    public Drivers getDriverAssigned() {
        return driverAssigned;
    }

    public void setDriverAssigned(Drivers driverAssigned) {
        this.driverAssigned = driverAssigned;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
