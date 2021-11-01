package com.patrick.delivery.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.delivery.entities.DeliveryOrderProducts;
import com.patrick.delivery.entities.DeliveryOrders;
import com.patrick.delivery.entities.DeliveryPlatforms;
import com.patrick.delivery.entities.Vendors;
import com.patrick.delivery.models.cscart.CsCartOrderDetailsRes;
import com.patrick.delivery.models.cscart.CsCartOrderRes;
import com.patrick.delivery.models.cscart.CsCartVendorsRes;
import com.patrick.delivery.properties.ApplicationPropertiesValues;
import com.patrick.delivery.repositories.api.DeliveryOrderProductsRepository;
import com.patrick.delivery.repositories.api.DeliveryOrdersRepository;
import com.patrick.delivery.repositories.api.DeliveryPlatformRepository;
import com.patrick.delivery.repositories.api.VendorsRepository;
import com.patrick.delivery.utils.AppConstant;
import com.patrick.delivery.utils.HttpCall;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author patrick on 6/30/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class ScheduledTaskService {

    @Autowired
    private DeliveryOrdersRepository deliveryOrdersRepository;
    @Autowired
    private ApplicationPropertiesValues applicationPropertiesValues;
    @Autowired
    private DeliveryPlatformRepository deliveryPlatformRepository;
    @Autowired
    private HttpCall<CsCartOrderRes> httpCall;
    @Autowired
    private HttpCall<CsCartOrderDetailsRes> httpCallOrderDetails;
    @Autowired
    private HttpCall<CsCartVendorsRes> httpCallVendors;
    @Autowired
    private DeliveryOrderProductsRepository deliveryOrderProductsRepository;
    @Autowired
    private VendorsRepository vendorsRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

   /* public void getProcessOrdersFromCsCart() {
        JPQLQuery<?> query = new JPAQuery<>(entityManager);

        QOrders qOrders = QOrders.orders;
        QDeliveryOrders qDeliveryOrders = QDeliveryOrders.deliveryOrders;
        List<Long> deliveryOrders = query.select(qDeliveryOrders.orderNo).from(qDeliveryOrders).fetch();

        List<Orders> orders = query.select(qOrders).from(qOrders)
                .where(qOrders.status.eq(Orders.ORDER_STATUS_PAID).and(qOrders.orderId.notIn(deliveryOrders)))
                .groupBy(qOrders.orderId)
                .fetch();

        logger.info("******* order found count " + orders.size());

        orders.forEach(node -> {
            JPQLQuery<?> orderQuery = new JPAQuery<>(entityManager);
            logger.info("******* adding order entry");
            QDeliveryOrders checkIfOrderExists = QDeliveryOrders.deliveryOrders;
            DeliveryOrders existsDeliveryOrders = orderQuery.select(checkIfOrderExists).from(checkIfOrderExists).where(checkIfOrderExists.orderNo.eq(node.getOrderId())).fetchOne();

            if (null == existsDeliveryOrders) {
                DeliveryOrders deliveryOrder = new DeliveryOrders();
                deliveryOrder.setOrderNo(node.getOrderId());
                deliveryOrder.setOrderState(DeliveryOrders.STATUS_PENDING);
                deliveryOrder.setStatus("active");
                deliveryOrder.setCreationDate(new Date());
                deliveryOrdersRepository.save(deliveryOrder);
            }
        });
    }*/


    public void getOrdersFromCartViaAPI() {

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("page", Collections.singletonList("1"));
        params.put("sort_by", Collections.singletonList("date"));
        params.put("sort_order", Collections.singletonList("desc"));
        params.put("status", Collections.singletonList(DeliveryOrders.ORDER_STATUS_PAID));


        List<DeliveryPlatforms> deliveryPlatform = deliveryPlatformRepository.findAllByStatus(AppConstant.STATUS_ACTIVE_RECORD);


        deliveryPlatform.forEach(deliverySystem -> {
            if (null != deliverySystem.getStorefrontNo())
                params.put("company_id", Collections.singletonList(deliverySystem.getStorefrontNo().toString()));
            //get orders
            CsCartOrderRes csCartOrderRes = apiHttpRequestGetOrders(params, deliverySystem.getUrl() + "/api/orders/", deliverySystem);

            logger.info("************* ====== orders got " + csCartOrderRes.getOrders().size());
            // new Thread(() -> {
            csCartOrderRes.getOrders().forEach(node -> {

/*
                CsCartOrderDetailsRes csCartOrderDetailsRes = apiHttpRequestGetOrderDetails(params,
                        deliverySystem.getUrl() + "/api/orders/" + node.getOrderId(), deliverySystem);
                csCartOrderDetailsRes.getProducts().getClass().getName();*/


                DeliveryOrders deliveryOrders = deliveryOrdersRepository.findFirstByOrderNoAndPlatformId(node.getOrderId(), deliverySystem.getId());

                if (null == deliveryOrders) {
                    deliveryOrders = new DeliveryOrders();
                    deliveryOrders.setOrderNo(node.getOrderId());
                    deliveryOrders.setOrderState(DeliveryOrders.STATUS_PENDING);
                    deliveryOrders.setCustomerFirstName(node.getFirstname());
                    deliveryOrders.setCustomerLastName(node.getLastname());
                    deliveryOrders.setCustomerPhoneNumber(node.getPhone());
                    deliveryOrders.setTotal(new BigDecimal(node.getTotal()));
                    deliveryOrders.setPlatformId(deliverySystem.getId());
                    deliveryOrders.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
                    deliveryOrdersRepository.save(deliveryOrders);

                    CsCartOrderDetailsRes csCartOrderDetailsRes = apiHttpRequestGetOrderDetails(params,
                            deliverySystem.getUrl() + "/api/orders/" + node.getOrderId(), deliverySystem);

                    deliveryOrders.setDeliveryAddress(csCartOrderDetailsRes.getsAddress());
                    deliveryOrders.setDeliveryLocation(csCartOrderDetailsRes.getsAddress2());
                    deliveryOrders.setDeliveryDate(csCartOrderDetailsRes.getDeliveryDate());
                    deliveryOrders.setDeliveryTimeFrom(csCartOrderDetailsRes.getDeliveryTimeFrom());
                    deliveryOrders.setDeliveryTimeTo(csCartOrderDetailsRes.getDeliveryTimeTo());
                    deliveryOrders.setDeliveryNote(csCartOrderDetailsRes.getDeliveryMessage());
                    deliveryOrdersRepository.save(deliveryOrders);

                    logger.info("************ Order details res " + csCartOrderDetailsRes.toString());

                    // Generating a Set of entries
                    Set set = csCartOrderDetailsRes.getProducts().entrySet();
                    // Displaying elements of LinkedHashMap
                    for (Object aSet : set) {
                        Map.Entry product = (Map.Entry) aSet;
                        logger.info("************* found Key is: " + product.getKey() +
                                "& Value is: " + product.getValue() + "\n");
                        CsCartOrderDetailsRes.CscartOrderProducts productsRes = getOrderProduct(product.getValue());

                        logger.info("************ order product found " + productsRes.toString());
                        if (null == productsRes.getItemId())
                            continue;
                        DeliveryOrderProducts deliveryOrderProducts = deliveryOrderProductsRepository.findFirstByItemIdAndOrderId(productsRes.getItemId(), deliveryOrders.getId());
                        if (null != deliveryOrderProducts)
                            continue;
                        deliveryOrderProducts = new DeliveryOrderProducts();
                        deliveryOrderProducts.setCompanyId(Long.valueOf(productsRes.getCompanyId()));
                        deliveryOrderProducts.setItemId(productsRes.getItemId());
                        deliveryOrderProducts.setProductId(productsRes.getProductId());
                        deliveryOrderProducts.setProductName(productsRes.getProduct());
                        deliveryOrderProducts.setOrderId(deliveryOrders.getId());
                        deliveryOrderProducts.setQuantity(productsRes.getAmount());
                        deliveryOrderProducts.setPrice(new BigDecimal(productsRes.getPrice()));
                        deliveryOrderProducts.setTotal(productsRes.getSubtotal());
                        deliveryOrderProducts.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
                        deliveryOrderProductsRepository.save(deliveryOrderProducts);
                    }
                }
            });
            //   });
        });
    }

    private CsCartOrderDetailsRes.CscartOrderProducts getOrderProduct(Object object) {
        CsCartOrderDetailsRes.CscartOrderProducts orderProduct = null;
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(object);
            JSONObject jsonObj = new JSONObject(json);
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            orderProduct = objectMapper.readValue(jsonObj.toString(), CsCartOrderDetailsRes.CscartOrderProducts.class);

        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
            logger.info("****** an error occurred while trying to get order products " + e.getMessage());
        }

        return orderProduct;
    }


    public void getPlatformVendors() {

        logger.info("************* get vendors ");
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("page", Collections.singletonList("1"));
        params.put("items_per_page", Collections.singletonList("20"));
        params.put("sort_by", Collections.singletonList("timestamp"));
        params.put("sort_order", Collections.singletonList("desc"));


        List<DeliveryPlatforms> deliveryPlatform = deliveryPlatformRepository.findAllByStatus(AppConstant.STATUS_ACTIVE_RECORD);


        deliveryPlatform.forEach(deliverySystem -> {

            CsCartVendorsRes csCartVendorsRes = apiHttpRequestGetVendors(params, deliverySystem.getUrl() + "/api/vendors/", deliverySystem);

            logger.info("************* ====== vendors got " + csCartVendorsRes.getVendors().size());
            csCartVendorsRes.getVendors().forEach(node -> {
                Vendors vendor = vendorsRepository.findFirstByCompanyIdAndPlatformId(node.getCompanyId(), deliverySystem.getId());
                if (null == vendor) {
                    vendor = new Vendors();
                    vendor.setEmail(node.getEmail());
                    vendor.setName(node.getCompany());
                    vendor.setCompanyId(node.getCompanyId());
                    vendor.setPlatformId(deliverySystem.getId());
                    vendor.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
                    vendorsRepository.save(vendor);
                }
            });


        });
    }

    /**
     * get orders
     */
    private CsCartOrderRes apiHttpRequestGetOrders(MultiValueMap<String, String> map, String url, DeliveryPlatforms deliverySystem) {

        CsCartOrderRes csCartOrders;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        String auth = deliverySystem.getApiUserName() + ":" + deliverySystem.getApiKey();

        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);

        logger.info("**** get orders url  " + url);
        logger.info("**** auth token " + authHeader);
        try {
            final ResponseEntity<CsCartOrderRes> balanceResponse = httpCall.sendAPIGetRequest(url, map, httpHeaders, CsCartOrderRes.class);
            csCartOrders = balanceResponse.getBody();

            assert csCartOrders != null;
            logger.info("================== api get orders : " + csCartOrders.toString());

            return csCartOrders;
        } catch (Exception e) {
            e.printStackTrace();
            csCartOrders = new CsCartOrderRes();
            logger.info("********** exception occurred " + e.getMessage());
        }
        return csCartOrders;
    }

    /**
     * get order details
     */
    private CsCartOrderDetailsRes apiHttpRequestGetOrderDetails(MultiValueMap<String, String> json, String url, DeliveryPlatforms deliverySystem) {

        CsCartOrderDetailsRes csCartOrders;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        String auth = deliverySystem.getApiUserName() + ":" + deliverySystem.getApiKey();
        //  String auth = applicationPropertiesValues.swiggoBackendUsername + ":" + applicationPropertiesValues.swiggoBackendAccessKey;

        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);

        logger.info("**** get orders url  " + url);
        logger.info("**** auth token " + authHeader);
        try {
            final ResponseEntity<CsCartOrderDetailsRes> balanceResponse = httpCallOrderDetails.sendAPIGetRequest(url, json, httpHeaders, CsCartOrderDetailsRes.class);
            csCartOrders = balanceResponse.getBody();

            assert csCartOrders != null;
            logger.info("================== api get orders : " + csCartOrders.toString());

            return csCartOrders;
        } catch (Exception e) {
            e.printStackTrace();
            csCartOrders = new CsCartOrderDetailsRes();
            logger.info("********** exception occurred  while getting order details " + e.getMessage());
        }
        return csCartOrders;
    }


    /**
     * get vendors
     */
    private CsCartVendorsRes apiHttpRequestGetVendors(MultiValueMap<String, String> map, String url, DeliveryPlatforms deliverySystem) {

        CsCartVendorsRes csCartVendorsRes;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        String auth = deliverySystem.getApiUserName() + ":" + deliverySystem.getApiKey();

        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);

        logger.info("**** vendor url url  " + url);
        logger.info("**** auth token " + authHeader);
        try {
            final ResponseEntity<CsCartVendorsRes> response = httpCallVendors.sendAPIGetRequest(url, map, httpHeaders, CsCartVendorsRes.class);
            csCartVendorsRes = response.getBody();

            assert csCartVendorsRes != null;
            logger.info("================== api get vendors : " + csCartVendorsRes.toString());

            return csCartVendorsRes;
        } catch (Exception e) {
            e.printStackTrace();
            csCartVendorsRes = new CsCartVendorsRes();
            logger.info("********** exception occurred getting vendors " + e.getMessage());
        }
        return csCartVendorsRes;
    }

    /**
     *
     *
     * */
//    public void getOrdersFromCscartDb() {
//        JPQLQuery<?> query = new JPAQuery<>(csCartEntityManager);
//        JPQLQuery<?> deliveryQuery = new JPAQuery<>(entityManager);
//        logger.info("******* started task to get orders");
//        QCscartOrders qOrders = QCscartOrders.cscartOrders;
//        QDeliveryOrders qDeliveryOrders = QDeliveryOrders.deliveryOrders;
//        List<Long> deliveryOrders = deliveryQuery.select(qDeliveryOrders.orderNo).from(qDeliveryOrders).fetch();
//
//        List<DeliveryPlatforms> deliveryPlatforms = deliveryPlatformRepository.findAllByStatusAndStorefrontNoIsNotNull(AppConstant.STATUS_ACTIVE_RECORD);
//
//
//        deliveryPlatforms.forEach(deliveryPlatform -> {
//
//            getStoreFrontVendors(deliveryPlatform);
//
//            List<CscartOrders> orders = query.select(qOrders).from(qOrders)
//                    .where(qOrders.status.eq(CscartOrders.ORDER_STATUS_PAID)
//                            .and(qOrders.orderId.notIn(deliveryOrders)
//                                    .and(qOrders.storefronId.eq(deliveryPlatform.getStorefrontNo()))))
//                    .groupBy(qOrders.orderId)
//                    .fetch();
//
//            logger.info("******* order found count " + orders.size());
//
//            orders.forEach(node -> {
//                logger.info("******* order entry");
//                DeliveryOrders deliveryOrder = new DeliveryOrders();
//                deliveryOrder.setOrderNo(node.getOrderId());
//                deliveryOrder.setOrderState(DeliveryOrders.STATUS_PENDING);
//                deliveryOrder.setStatus("active");
//                deliveryOrder.setCreationDate(new Date());
//
//                // deliveryOrders = new DeliveryOrders();
//                deliveryOrder.setOrderNo(node.getOrderId());
//                deliveryOrder.setOrderState(DeliveryOrders.STATUS_PENDING);
//                deliveryOrder.setCustomerFirstName(node.getFirstName());
//                deliveryOrder.setCustomerLastName(node.getLastName());
//                deliveryOrder.setCustomerPhoneNumber(node.getPhoneNumber());
//                deliveryOrder.setTotal(new BigDecimal(String.valueOf(node.getTotal())));
//                deliveryOrder.setPlatformId(deliveryPlatform.getId());
//                deliveryOrder.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
//
//                deliveryOrder.setDeliveryAddress(node.getDeiveryAddress());
//                deliveryOrder.setDeliveryLocation(node.getDeliveryLocation());
//              /*  deliveryOrder.setDeliveryDate(csCartOrderDetailsRes.getDeliveryDate());
//                deliveryOrder.setDeliveryTimeFrom(csCartOrderDetailsRes.getDeliveryTimeFrom());
//                deliveryOrder.setDeliveryTimeTo(csCartOrderDetailsRes.getDeliveryTimeTo());*/
//                deliveryOrder.setDeliveryNote(node.getDeliveryNote());
//                deliveryOrdersRepository.save(deliveryOrder);
//
//                orderedProductsFromDb(node, deliveryOrder);
//            });
//        });
//    }

/*
    private void orderedProductsFromDb(CscartOrders cscartOrders, DeliveryOrders deliveryOrders) {
        JPQLQuery<?> query = new JPAQuery<>(csCartEntityManager);
        QCscartOrderDetails qCscartOrderDetails = QCscartOrderDetails.cscartOrderDetails;

        // List<CscartOrderDetails> orderDetails = cscartOrders.getCscartOrderDetailsList();
        List<CscartOrderDetails> orderDetails = query.select(qCscartOrderDetails).from(qCscartOrderDetails)
                .where(qCscartOrderDetails.orderId.eq(cscartOrders.getOrderId()))
                .fetch();

        orderDetails.forEach(node -> {

            DeliveryOrderProducts deliveryOrderProducts = deliveryOrderProductsRepository.findFirstByItemIdAndOrderId(String.valueOf(node.getItemId()), deliveryOrders.getId());
            if (null == deliveryOrderProducts) {

                deliveryOrderProducts = new DeliveryOrderProducts();
                deliveryOrderProducts.setCompanyId(node.getProduct().getCompanyId());
                deliveryOrderProducts.setItemId(String.valueOf(node.getItemId()));
                deliveryOrderProducts.setProductId(String.valueOf(node.getProductId()));
                deliveryOrderProducts.setProductName(node.getProduct().getCscartProductsDescription().getProduct());
                deliveryOrderProducts.setOrderId(deliveryOrders.getId());
                deliveryOrderProducts.setQuantity(String.valueOf(node.getAmount()));
                deliveryOrderProducts.setPrice(node.getPrice());
                deliveryOrderProducts.setTotal(new BigDecimal(node.getAmount()).multiply(node.getPrice()));
                deliveryOrderProducts.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
                deliveryOrderProductsRepository.save(deliveryOrderProducts);
            }
        });
    }
*/

    /**
     *
     * */
//    private void getStoreFrontVendors(DeliveryPlatforms deliveryPlatform) {
//        JPQLQuery<?> query = new JPAQuery<>(csCartEntityManager);
//        JPQLQuery<?> queryStores = new JPAQuery<>(csCartEntityManager);
//        JPQLQuery<?> deliveryQuery = new JPAQuery<>(entityManager);
//
//        logger.info("******* get store vendors");
//
//
//        QCscartCompanies qCscartCompanies = QCscartCompanies.cscartCompanies;
//        QVendors qVendors = QVendors.vendors;
//
//        //get store front companies
//        List<Long> myVendors = deliveryQuery.select(qVendors.companyId).from(qVendors)
//                .where(qVendors.platformId.eq(deliveryPlatform.getId())).fetch();
//
//        QCsCartStoreFrontComapanies qCsCartStoreFrontComapanies = QCsCartStoreFrontComapanies.csCartStoreFrontComapanies;
//
//        List<Long> csCartStoreFrontComapanies = queryStores.select(qCsCartStoreFrontComapanies.companyId).from(qCsCartStoreFrontComapanies)
//                .where(qCsCartStoreFrontComapanies.storeFrontId.eq(deliveryPlatform.getStorefrontNo())
//                        .and(qCsCartStoreFrontComapanies.companyId.notIn(myVendors))
//                ).fetch();
//
//        List<CscartCompanies> cscartCompanies = query.select(qCscartCompanies).from(qCscartCompanies)
//                .where(qCscartCompanies.companyId.in(csCartStoreFrontComapanies)).fetch();
//
//        logger.info("******* store vendors found count {} " + cscartCompanies.size());
//
//        cscartCompanies.forEach(node -> {
//            Vendors vendor = vendorsRepository.findFirstByCompanyIdAndPlatformId(node.getCompanyId(), deliveryPlatform.getId());
//            if (null == vendor) {
//                vendor = new Vendors();
//                vendor.setEmail(node.getEmail());
//                vendor.setName(node.getCompany());
//                vendor.setPlatformId(deliveryPlatform.getId());
//                vendor.setCompanyId(node.getCompanyId());
//                vendor.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
//                vendor.setPhoneNumber(node.getPhone());
//                vendorsRepository.save(vendor);
//            }
//        });
//    }
}
