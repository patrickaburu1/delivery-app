package com.patrick.delivery.models;

/**
 * @author patrick on 7/30/20
 * @project sprintel-delivery
 */
public class DriverRes {
    private Long id;
    private Long vehicleId=0L;
    private String status, phone, email,lastName,firstName, driverState;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getDriverState() {
        return driverState;
    }

    public void setDriverState(String driverState) {
        this.driverState = driverState;
    }
}
