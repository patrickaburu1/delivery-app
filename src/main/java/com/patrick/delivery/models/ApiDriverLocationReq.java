package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 8/18/20
 * @project sprintel-delivery
 */
public class ApiDriverLocationReq {
    @NonNull
    private Double lat,lng;

    private Long orderNo;

    @NonNull
    public Double getLat() {
        return lat;
    }

    public void setLat(@NonNull Double lat) {
        this.lat = lat;
    }

    @NonNull
    public Double getLng() {
        return lng;
    }

    public void setLng(@NonNull Double lng) {
        this.lng = lng;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
