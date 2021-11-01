//package com.swiggo.swiggodelivery.cscartEntities;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.List;
//
///**
// * @author patrick on 6/8/20
// * @project sprintel-delivery
// */
//@Entity
//@Table(name = "cscart_orders")
//public class CscartOrders implements Serializable {
//
//    public static final String ORDER_STATUS_PAID="P";
//    public static final String ORDER_STATUS_INTRANSIT="G";
//    public static final String ORDER_STATUS_COMPLETED="C";
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "order_id")
//    private Long orderId;
//
//    @Column(name = "status")
//    private String status;
//
//    @Column(name = "total")
//    private BigDecimal total;
//
//    @Column(name = "timestamp")
//    private Long timestamp;
//
//    @Column(name = "firstname")
//    private String firstName;
//
//    @Column(name = "lastname")
//    private String lastName;
//
//    @Column(name = "phone")
//    private String phoneNumber;
//
//    @Column(name = "s_address")
//    private String deiveryAddress;
//
//    @Column(name = "s_address_2")
//    private String deliveryLocation;
//
//    @Column(name = "storefront_id")
//    private Long storefronId;
//
//    @Column(name = "notes")
//    private String  deliveryNote;
//
//    @JsonIgnore
//    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
//    @OneToMany
//    private List<CscartOrderDetails> cscartOrderDetailsList;
//
//    public List<CscartOrderDetails> getCscartOrderDetailsList() {
//        return cscartOrderDetailsList;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public String getDeiveryAddress() {
//        return deiveryAddress;
//    }
//
//    public void setDeiveryAddress(String deiveryAddress) {
//        this.deiveryAddress = deiveryAddress;
//    }
//
//    public Long getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(Long orderId) {
//        this.orderId = orderId;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Long getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Long timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public BigDecimal getTotal() {
//        return total;
//    }
//
//    public void setTotal(BigDecimal total) {
//        this.total = total;
//    }
//
//    public Long getStorefronId() {
//        return storefronId;
//    }
//
//    public void setStorefronId(Long storefronId) {
//        this.storefronId = storefronId;
//    }
//
//    public String getDeliveryNote() {
//        return deliveryNote;
//    }
//
//    public void setDeliveryNote(String deliveryNote) {
//        this.deliveryNote = deliveryNote;
//    }
//
//    public String getDeliveryLocation() {
//        return deliveryLocation;
//    }
//
//    public void setDeliveryLocation(String deliveryLocation) {
//        this.deliveryLocation = deliveryLocation;
//    }
//}
