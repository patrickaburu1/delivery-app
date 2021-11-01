package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "orders")
public class DeliveryOrders extends AuditableWithId implements Serializable {

    public static final String ORDER_STATUS_PAID="P";
    public static final String ORDER_STATUS_INTRANSIT="G";
    public static final String ORDER_STATUS_COMPLETED="C";

    public static final String STATUS_PENDING="Pending";
    public static final String STATUS_ASSIGNED="Assigned";
    public static final String STATUS_ACCEPTED="Accepted";
    public static final String STATUS_REJECTED="Rejected";
    public static final String STATUS_INTRANSIT="In-transit";
    public static final String STATUS_COMPLETED="Completed";

    @Column(name = "order_no")
    private Long orderNo;

    @Column(name = "order_state")
    private String orderState;

    @Column(name = "delivery_date")
    private Long deliveryDate;

    @Column(name = "delivery_time_from")
    private Long deliveryTimeFrom;

    @Column(name = "delivery_time_to")
    private Long deliveryTimeTo;

    @Column(name = "delivery_note")
    private String  deliveryNote;

    @Column(name = "customer_first_name")
    private String customerFirstName;

    @Column(name = "customer_last_name")
    private String customerLastName;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "customer_phone_number")
    private String customerPhoneNumber;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "platform_id")
    private Long platformId;

    @JsonIgnore
    @JoinColumn(name = "platform_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    private DeliveryPlatforms deliveryPlatformsLink;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }


    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }


    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getDeliveryTimeFrom() {
        return deliveryTimeFrom;
    }

    public void setDeliveryTimeFrom(Long deliveryTimeFrom) {
        this.deliveryTimeFrom = deliveryTimeFrom;
    }

    public Long getDeliveryTimeTo() {
        return deliveryTimeTo;
    }

    public void setDeliveryTimeTo(Long deliveryTimeTo) {
        this.deliveryTimeTo = deliveryTimeTo;
    }

    public String getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(String deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
