package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 7/27/20
 * @project sprintel-delivery
 */
public class NewVehicleReq {
    @NonNull
    private String regNo;
    public String vehicleType;
    public Long driver;
    @NonNull
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(@NonNull String regNo) {
        this.regNo = regNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Long getDriver() {
        return driver;
    }

    public void setDriver(Long driver) {
        this.driver = driver;
    }
}
