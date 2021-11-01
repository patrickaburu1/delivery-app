package com.patrick.delivery.models.cscart;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author patrick on 7/9/20
 * @project sprintel-delivery
 */
public class CsCartOrderRes {

    public List<CsCartOrderResData> orders;

    public List<CsCartOrderResData> getOrders() {
        return orders;
    }

    public void setOrders(List<CsCartOrderResData> orders) {
        this.orders = orders;
    }

    public static class CsCartOrderResData{

        @JsonProperty("order_id")
        private Long orderId;
        @JsonProperty("issuer_id")
        private Object issuerId;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("is_parent_order")
        private String isParentOrder;
        @JsonProperty("parent_order_id")
        private String parentOrderId;
        @JsonProperty("company_id")
        private String companyId;
        @JsonProperty("timestamp")
        private String timestamp;
        @JsonProperty("firstname")
        private String firstname;
        @JsonProperty("lastname")
        private String lastname;
        @JsonProperty("email")
        private String email;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("status")
        private String status;
        @JsonProperty("total")
        private String total;
        @JsonProperty("invoice_id")
        private String invoiceId;
        @JsonProperty("credit_memo_id")
        private String creditMemoId;
        @JsonProperty("points")
        private String points;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Object getIssuerId() {
            return issuerId;
        }

        public void setIssuerId(Object issuerId) {
            this.issuerId = issuerId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getIsParentOrder() {
            return isParentOrder;
        }

        public void setIsParentOrder(String isParentOrder) {
            this.isParentOrder = isParentOrder;
        }

        public String getParentOrderId() {
            return parentOrderId;
        }

        public void setParentOrderId(String parentOrderId) {
            this.parentOrderId = parentOrderId;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }

        public String getCreditMemoId() {
            return creditMemoId;
        }

        public void setCreditMemoId(String creditMemoId) {
            this.creditMemoId = creditMemoId;
        }

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }
    }
}
