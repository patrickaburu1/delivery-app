//package com.swiggo.swiggodelivery.cscartEntities;
//
//import javax.persistence.*;
//import java.io.Serializable;
//
///**
// * @author patrick on 6/11/20
// * @project sprintel-delivery
// */
//@Entity
//@Table(name = "cscart_companies")
//public class CscartCompanies implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "company_id")
//    private Long companyId;
//
//    @Column(name = "company")
//    private String company;
//
//    @Column(name = "address")
//    private String address;
//
//    @Column(name = "city")
//    private String city;
//
//    @Column(name = "phone")
//    private String phone;
//
//    @Column(name = "email")
//    private String email;
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public Long getCompanyId() {
//        return companyId;
//    }
//
//    public void setCompanyId(Long companyId) {
//        this.companyId = companyId;
//    }
//
//    public String getCompany() {
//        return company;
//    }
//
//    public void setCompany(String company) {
//        this.company = company;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }
//}
