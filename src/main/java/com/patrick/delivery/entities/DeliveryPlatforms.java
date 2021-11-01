package com.patrick.delivery.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author patrick on 7/9/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "platforms")
public class DeliveryPlatforms extends AuditableWithId implements Serializable {

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "api_user_name")
    private String apiUserName;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "storefront_no")
    private Long storefrontNo;

    public Long getStorefrontNo() {
        return storefrontNo;
    }

    public void setStorefrontNo(Long storefrontNo) {
        this.storefrontNo = storefrontNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiUserName() {
        return apiUserName;
    }

    public void setApiUserName(String apiUserName) {
        this.apiUserName = apiUserName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
