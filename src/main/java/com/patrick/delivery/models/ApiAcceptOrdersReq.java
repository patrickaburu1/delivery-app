package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/11/20
 * @project sprintel-delivery
 */
public class ApiAcceptOrdersReq {
    @NonNull
    private Long requestId;
    @NonNull
    private Boolean accept;

    private String reason;

    @NonNull
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(@NonNull Long requestId) {
        this.requestId = requestId;
    }

    @NonNull
    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(@NonNull Boolean accept) {
        this.accept = accept;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
