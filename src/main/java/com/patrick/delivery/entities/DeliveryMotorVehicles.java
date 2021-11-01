package com.patrick.delivery.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author patrick on 6/16/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "motor_vehicles")
public class DeliveryMotorVehicles extends AuditableWithId implements Serializable {

    @Column(name = "reg_no")
    private String regNo;

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Transient
    private String assignedTo;

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
