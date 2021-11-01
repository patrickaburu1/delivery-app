package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author patrick on 6/9/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "order_products")
public class DeliveryOrderProducts extends AuditableWithId implements Serializable {

    @Column(name = "product_id")
    private String productId;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "product_name")
    private String  productName;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "order_id")
    private Long orderId;

    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    private DeliveryOrders deliveryOrders;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public DeliveryOrders getDeliveryOrders() {
        return deliveryOrders;
    }
}
