package com.patrick.delivery.models.cscart;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * @author patrick on 7/9/20
 * @project sprintel-delivery
 */
public class CsCartOrderDetailsRes {

    @JsonProperty("s_address")
    private String sAddress;
    @JsonProperty("s_address_2")
    private String sAddress2;
    @JsonProperty("s_city")
    private String sCity;
    @JsonProperty("s_county")
    private String sCounty;
    @JsonProperty("s_state")
    private String sState;
    @JsonProperty("s_country")
    private String sCountry;
    @JsonProperty("s_zipcode")
    private String sZipcode;
    @JsonProperty("s_phone")
    private String sPhone;
    @JsonProperty("s_address_type")
    private String sAddressType;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("fax")
    private String fax;
    @JsonProperty("url")
    private String url;
    @JsonProperty("email")
    private String email;
    @JsonProperty("payment_id")
    private String paymentId;
    @JsonProperty("tax_exempt")
    private String taxExempt;
    @JsonProperty("lang_code")
    private String langCode;
    @JsonProperty("ip_address")
    private String ipAddress;
    @JsonProperty("repaid")
    private String repaid;
    @JsonProperty("validation_code")
    private String validationCode;
    @JsonProperty("localization_id")
    private String localizationId;
    @JsonProperty("profile_id")
    private String profileId;
    @JsonProperty("storefront_id")
    private String storefrontId;
    @JsonProperty("delivery_message")
    private String deliveryMessage;
    @JsonProperty("delivery_date")
    private Long deliveryDate;
    @JsonProperty("delivery_time_from")
    private Long deliveryTimeFrom;
    @JsonProperty("delivery_time_to")
    private Long deliveryTimeTo;

    @JsonProperty("products")
    public LinkedHashMap<String,Object> products;


    public static class CscartOrderProducts{
        @JsonProperty("item_id")
        private String itemId;
        @JsonProperty("order_id")
        private String orderId;
        @JsonProperty("product_id")
        private String productId;
        @JsonProperty("price")
        private String price;
        @JsonProperty("amount")
        private String amount;
        @JsonProperty("product")
        private String product;
        @JsonProperty("company_id")
        private String companyId;
        @JsonProperty("base_price")
        private BigDecimal basePrice;
        @JsonProperty("original_price")
        private BigDecimal originalPrice;
        @JsonProperty("subtotal")
        private BigDecimal subtotal;
        @JsonProperty("display_subtotal")
        private BigDecimal displaySubtotal;
        @JsonProperty("shipment_amount")
        private String shipmentAmount;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public BigDecimal getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(BigDecimal originalPrice) {
            this.originalPrice = originalPrice;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        public BigDecimal getDisplaySubtotal() {
            return displaySubtotal;
        }

        public void setDisplaySubtotal(BigDecimal displaySubtotal) {
            this.displaySubtotal = displaySubtotal;
        }

        public String getShipmentAmount() {
            return shipmentAmount;
        }

        public void setShipmentAmount(String shipmentAmount) {
            this.shipmentAmount = shipmentAmount;
        }

        @Override
        public String toString() {
            return "CscartOrderProducts{" +
                    "itemId='" + itemId + '\'' +
                    ", orderId='" + orderId + '\'' +
                    ", productId='" + productId + '\'' +
                    ", price='" + price + '\'' +
                    ", amount='" + amount + '\'' +
                    ", product='" + product + '\'' +
                    ", companyId='" + companyId + '\'' +
                    ", basePrice=" + basePrice +
                    ", originalPrice=" + originalPrice +
                    ", subtotal=" + subtotal +
                    ", displaySubtotal=" + displaySubtotal +
                    ", shipmentAmount='" + shipmentAmount + '\'' +
                    '}';
        }
    }
    public LinkedHashMap<String, Object> getProducts() {
        return products;
    }

    public void setProducts(LinkedHashMap<String, Object> products) {
        this.products = products;
    }


    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getsAddress2() {
        return sAddress2;
    }

    public void setsAddress2(String sAddress2) {
        this.sAddress2 = sAddress2;
    }

    public String getsCity() {
        return sCity;
    }

    public void setsCity(String sCity) {
        this.sCity = sCity;
    }

    public String getsCounty() {
        return sCounty;
    }

    public void setsCounty(String sCounty) {
        this.sCounty = sCounty;
    }

    public String getsState() {
        return sState;
    }

    public void setsState(String sState) {
        this.sState = sState;
    }

    public String getsCountry() {
        return sCountry;
    }

    public void setsCountry(String sCountry) {
        this.sCountry = sCountry;
    }

    public String getsZipcode() {
        return sZipcode;
    }

    public void setsZipcode(String sZipcode) {
        this.sZipcode = sZipcode;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public String getsAddressType() {
        return sAddressType;
    }

    public void setsAddressType(String sAddressType) {
        this.sAddressType = sAddressType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(String taxExempt) {
        this.taxExempt = taxExempt;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRepaid() {
        return repaid;
    }

    public void setRepaid(String repaid) {
        this.repaid = repaid;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public String getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(String localizationId) {
        this.localizationId = localizationId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getStorefrontId() {
        return storefrontId;
    }

    public void setStorefrontId(String storefrontId) {
        this.storefrontId = storefrontId;
    }

    public String getDeliveryMessage() {
        return deliveryMessage;
    }

    public void setDeliveryMessage(String deliveryMessage) {
        this.deliveryMessage = deliveryMessage;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getDeliveryTimeFrom() {
        return deliveryTimeFrom;
    }

    public void setDeliveryTimeFrom(Long deliveryTimeFrom) {
        this.deliveryTimeFrom = deliveryTimeFrom;
    }

    public Long getDeliveryTimeTo() {
        return deliveryTimeTo;
    }

    public void setDeliveryTimeTo(Long deliveryTimeTo) {
        this.deliveryTimeTo = deliveryTimeTo;
    }

    @Override
    public String toString() {
        return "CsCartOrderDetailsRes{" +
                "sAddress='" + sAddress + '\'' +
                ", sAddress2='" + sAddress2 + '\'' +
                ", sCity='" + sCity + '\'' +
                ", sCounty='" + sCounty + '\'' +
                ", sState='" + sState + '\'' +
                ", sCountry='" + sCountry + '\'' +
                ", sZipcode='" + sZipcode + '\'' +
                ", sPhone='" + sPhone + '\'' +
                ", sAddressType='" + sAddressType + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", url='" + url + '\'' +
                ", email='" + email + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", taxExempt='" + taxExempt + '\'' +
                ", langCode='" + langCode + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", repaid='" + repaid + '\'' +
                ", validationCode='" + validationCode + '\'' +
                ", localizationId='" + localizationId + '\'' +
                ", profileId='" + profileId + '\'' +
                ", storefrontId='" + storefrontId + '\'' +
                ", deliveryMessage='" + deliveryMessage + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", deliveryTimeFrom='" + deliveryTimeFrom + '\'' +
                ", deliveryTimeTo='" + deliveryTimeTo + '\'' +
                '}';
    }
}
