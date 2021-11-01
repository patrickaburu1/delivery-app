package com.patrick.delivery.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author patrick on 6/9/20
 * @project sprintel-delivery
 */
public class ApiDriverOrdersRes {

    private Long requestId;
    private Long orderNo;
    private BigDecimal total;
    private String orderState;
    private Long orderDate;
    private Date deliveredOn;
    private String deliveryDate;
    private String deliveryTimeFrom;
    private String deliveryTimeTo;
    private String deliveryNote;

    public Date getDeliveredOn() {
        return deliveredOn;
    }

    public void setDeliveredOn(Date deliveredOn) {
        this.deliveredOn = deliveredOn;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    private CustomerRes customerInfo;

    private List<ProductsRes> products;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public CustomerRes getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerRes customerInfo) {
        this.customerInfo = customerInfo;
    }

    public List<ProductsRes> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsRes> products) {
        this.products = products;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryTimeFrom() {
        return deliveryTimeFrom;
    }

    public void setDeliveryTimeFrom(String deliveryTimeFrom) {
        this.deliveryTimeFrom = deliveryTimeFrom;
    }

    public String getDeliveryTimeTo() {
        return deliveryTimeTo;
    }

    public void setDeliveryTimeTo(String deliveryTimeTo) {
        this.deliveryTimeTo = deliveryTimeTo;
    }

    public String getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(String deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public static class CustomerRes{

        private String customerNames;
        private String customerPhone;
        private String address;
        private DeliveryLocRes deliveryAddress;

        public DeliveryLocRes getDeliveryAddress() {
            return deliveryAddress;
        }

        public void setDeliveryAddress(DeliveryLocRes deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
        }

        public String getCustomerNames() {
            return customerNames;
        }

        public void setCustomerNames(String customerNames) {
            this.customerNames = customerNames;
        }

        public String getCustomerPhone() {
            return customerPhone;
        }

        public void setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }


    public static class ProductsRes{
        private String name;
        private Integer quantity;
        private String vendorName;
        private String vendorAddress;
        private String vendorCity;
        private String vendorContact;
        private String vendorImageUrl;

        public String getVendorImageUrl() {
            return vendorImageUrl;
        }

        public void setVendorImageUrl(String vendorImageUrl) {
            this.vendorImageUrl = vendorImageUrl;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getVendorAddress() {
            return vendorAddress;
        }

        public void setVendorAddress(String vendorAddress) {
            this.vendorAddress = vendorAddress;
        }

        public String getVendorCity() {
            return vendorCity;
        }

        public void setVendorCity(String vendorCity) {
            this.vendorCity = vendorCity;
        }

        public String getVendorContact() {
            return vendorContact;
        }

        public void setVendorContact(String vendorContact) {
            this.vendorContact = vendorContact;
        }
    }

    public static class DeliveryLocRes{
        private Double lat, lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }
    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }
}
