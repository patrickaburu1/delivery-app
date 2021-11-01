package com.patrick.delivery.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author patrick on 6/11/20
 * @project sprintel-delivery
 */
public class UpdateOrderReq {

    private  String status;
    @JsonProperty("notify_user")
    private Boolean  notifyUser;
    @JsonProperty("notify_department")
    private Boolean notifyDepartment=false;
    @JsonProperty("notify_vendor")
    private Boolean notifyVendor=false;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(Boolean notifyUser) {
        this.notifyUser = notifyUser;
    }

    public Boolean getNotifyDepartment() {
        return notifyDepartment;
    }

    public void setNotifyDepartment(Boolean notifyDepartment) {
        this.notifyDepartment = notifyDepartment;
    }

    public Boolean getNotifyVendor() {
        return notifyVendor;
    }

    public void setNotifyVendor(Boolean notifyVendor) {
        this.notifyVendor = notifyVendor;
    }
}
