package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author patrick on 7/10/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "vendors")
public class Vendors extends AuditableWithId implements Serializable {

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phoneNumber;

    @Column(name = "platform_id")
    private Long platformId;

    @JsonIgnore
    @JoinColumn(name = "platform_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    private DeliveryPlatforms deliveryPlatformsLink;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public DeliveryPlatforms getDeliveryPlatformsLink() {
        return deliveryPlatformsLink;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
