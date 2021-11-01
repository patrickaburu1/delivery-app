//package com.swiggo.swiggodelivery.cscartEntities;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//
//import javax.persistence.*;
//import java.io.Serializable;
//
///**
// * @author patrick on 6/9/20
// * @project sprintel-delivery
// */
//@Entity
//@Table(name = "cscart_products")
//public class CscartProducts implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "product_id")
//    private Long productId;
//
//    @Column(name = "product_code")
//    private String productCode;
//
//    @Column(name = "company_id")
//    private Long companyId;
//
//    @JsonIgnore
//    @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
//    @OneToOne
//    private CscartProductsDescription cscartProductsDescription;
//
//    @JsonIgnore
//    @JoinColumn(name = "company_id", referencedColumnName = "company_id", insertable = false, updatable = false)
//    @OneToOne
//    private CscartCompanies cscartCompaniesLink;
//
//    public CscartCompanies getCscartCompaniesLink() {
//        return cscartCompaniesLink;
//    }
//
//    public CscartProductsDescription getCscartProductsDescription() {
//        return cscartProductsDescription;
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
//    public Long getCompanyId() {
//        return companyId;
//    }
//
//    public void setCompanyId(Long companyId) {
//        this.companyId = companyId;
//    }
//}
