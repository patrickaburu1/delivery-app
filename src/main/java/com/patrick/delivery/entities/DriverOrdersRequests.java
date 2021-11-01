package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author patrick on 6/9/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "driver_orders_requests")
public class DriverOrdersRequests extends AuditableWithId implements Serializable {

    @Column(name = "order_no")
    private Long orderNo;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "state")
    private String state;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "delivery_order_id")
    private Long deliveryOrderId;

    @JsonIgnore
    @JoinColumn(name = "driver_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    private Users assignedDriver;

    @JsonIgnore
    @JoinColumn(name = "delivery_order_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    private DeliveryOrders deliveryOrders;


    public Users getAssignedDriver() {
        return assignedDriver;
    }


    public Long getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public DeliveryOrders getDeliveryOrders() {
        return deliveryOrders;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDeliveryOrderId(Long deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
