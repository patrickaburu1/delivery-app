//package com.swiggo.swiggodelivery.cscartEntities;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.math.BigDecimal;
//
///**
// * @author patrick on 6/9/20
// * @project sprintel-delivery
// */
//@Entity
//@Table(name = "cscart_order_details")
//public class CscartOrderDetails implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "item_id")
//    private Long itemId;
//
//    @Column(name = "order_id")
//    private Long orderId;
//
//    @Column(name = "product_id")
//    private Long productId;
//
//    @Column(name = "product_code")
//    private String productCode;
//
//    @Column(name = "price")
//    private BigDecimal price;
//
//    @Column(name = "amount")
//    private Integer amount;
//
//
//    @JsonIgnore
//    @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
//    @OneToOne
//    private CscartProducts product;
//
//    public CscartProducts getProduct() {
//        return product;
//    }
//
//    public Long getItemId() {
//        return itemId;
//    }
//
//    public void setItemId(Long itemId) {
//        this.itemId = itemId;
//    }
//
//    public Long getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(Long orderId) {
//        this.orderId = orderId;
//    }
//
//    public Long getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Long productId) {
//        this.productId = productId;
//    }
//
//    public String getProductCode() {
//        return productCode;
//    }
//
//    public void setProductCode(String productCode) {
//        this.productCode = productCode;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }
//
//    public Integer getAmount() {
//        return amount;
//    }
//
//    public void setAmount(Integer amount) {
//        this.amount = amount;
//    }
//}
