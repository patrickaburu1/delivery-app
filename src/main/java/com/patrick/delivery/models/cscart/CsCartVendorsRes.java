package com.patrick.delivery.models.cscart;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author patrick on 7/17/20
 * @project sprintel-delivery
 */
public class CsCartVendorsRes {

    private List<VendorsRes> vendors;


    public static class VendorsRes {
        @JsonProperty("company_id")
        private Long companyId;
        @JsonProperty("email")
        private String email;
        @JsonProperty("company")
        private String company;
        @JsonProperty("timestamp")
        private Long timestamp;
        @JsonProperty("status")
        private String status;

        public Long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "VendorsRes{" +
                    "companyId:" + companyId +
                    ", email:'" + email + '\'' +
                    ", company :" + company + '\'' +
                    ", timestamp:" + timestamp +
                    ", status:'" + status + '\'' +
                    '}';
        }
    }

    public List<VendorsRes> getVendors() {
        return vendors;
    }

    public void setVendors(List<VendorsRes> vendors) {
        this.vendors = vendors;
    }

    @Override
    public String toString() {
        return "CsCartVendorsRes{" +
                "vendors:" + vendors +
                '}';
    }
}
