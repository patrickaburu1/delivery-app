package com.patrick.delivery.models;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
public class ApiUserDetailsRes {
    private String phone,email,firstName, lastName;
    private String motorRegNo;
    private String state;
    private Boolean pendingOrders=false;
    private Long pendingOrdersCount;

    public Long getPendingOrdersCount() {
        return pendingOrdersCount;
    }

    public void setPendingOrdersCount(Long pendingOrdersCount) {
        this.pendingOrdersCount = pendingOrdersCount;
    }

    public Boolean getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Boolean pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMotorRegNo() {
        return motorRegNo;
    }

    public void setMotorRegNo(String motorRegNo) {
        this.motorRegNo = motorRegNo;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
