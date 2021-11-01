package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/11/20
 * @project sprintel-delivery
 */
public class ApiOrderStateReq {
    @NonNull
    private Long requestId;

    @NonNull
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(@NonNull Long requestId) {
        this.requestId = requestId;
    }
}
