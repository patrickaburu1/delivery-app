package com.patrick.delivery.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author patrick on 6/11/20
 * @project sprintel-delivery
 */
public class UpdateOrderRes {
    @JsonProperty(value = "order_id")
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "UpdateOrderRes{" +
                "orderId=" + orderId +
                '}';
    }
}
