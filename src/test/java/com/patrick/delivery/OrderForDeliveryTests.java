//package com.swiggo.swiggodelivery;
//
//import com.swiggo.swiggodelivery.entities.DeliveryOrders;
//import com.swiggo.swiggodelivery.models.cscart.CsCartOrderDetailsRes;
//import com.swiggo.swiggodelivery.models.cscart.CsCartOrderRes;
//import com.swiggo.swiggodelivery.properties.ApplicationPropertiesValues;
//import com.swiggo.swiggodelivery.services.ScheduledTaskService;
//import com.swiggo.swiggodelivery.utils.HttpCall;
//import org.apache.tomcat.util.codec.binary.Base64;
//
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.nio.charset.Charset;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author patrick on 7/9/20
// * @project swiggo-delivery
// */
//public class OrderForDeliveryTests extends SwiggoDeliveryApplicationTests {
//
//    @Autowired
//    private ApplicationPropertiesValues applicationPropertiesValues;
//
//    @Autowired
//    private HttpCall<CsCartOrderRes> httpCall;
//    @Autowired
//    private HttpCall<CsCartOrderDetailsRes> httpCallOrderDetails;
//    @Autowired
//    private ScheduledTaskService scheduledTaskService;
//
//    private Logger logger= LoggerFactory.getLogger(getClass());
//
//    @Test
//    public  void  getOrdersViaApi(){
//        scheduledTaskService.getOrdersFromCartViaAPI();
//    }
//
//    @Test
//    public void  getOrders(){
//        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.put("page", Collections.singletonList("1"));
//        params.put("items_per_page", Collections.singletonList("100"));
//        params.put("sort_by", Collections.singletonList("date"));
//        params.put("sort_order", Collections.singletonList("desc"));
//        params.put("status", Collections.singletonList(DeliveryOrders.ORDER_STATUS_PAID));
//        //get orders
//        CsCartOrderRes csCartOrderRes=   apiHttpRequestGetOrders(params, applicationPropertiesValues.swiggoBackendUrl + "/api/orders/");
//
//       // new Thread(()->{
//            csCartOrderRes.getOrders().forEach(node ->{
//
//                CsCartOrderDetailsRes csCartOrderDetailsRes= apiHttpRequestGetOrderDetails(params, applicationPropertiesValues.swiggoBackendUrl + "/api/orders/"+ node.getOrderId());
//
//             logger.info("************ Order details res "+csCartOrderDetailsRes.toString());
//            });
//       // });
//
//    }
//
//
//    private CsCartOrderRes apiHttpRequestGetOrders(MultiValueMap<String, String> json, String url) {
//
//        CsCartOrderRes csCartOrders;
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        String auth = applicationPropertiesValues.swiggoBackendUsername + ":" + applicationPropertiesValues.swiggoBackendAccessKey;
//
//        byte[] encodedAuth = Base64.encodeBase64(
//                auth.getBytes(Charset.forName("US-ASCII")));
//        String authHeader = "Basic " + new String(encodedAuth);
//        httpHeaders.set("Authorization", authHeader);
//
//        logger.info("**** get orders url  "+url);
//        logger.info("**** auth token "+authHeader);
//        try {
//            final ResponseEntity<CsCartOrderRes> balanceResponse = httpCall.sendAPIGetRequest(url, json, httpHeaders, CsCartOrderRes.class);
//            csCartOrders = balanceResponse.getBody();
//
//            assert csCartOrders != null;
//            logger.info("================== api get orders : " + csCartOrders.toString());
//
//            return csCartOrders;
//        } catch (Exception e) {
//            e.printStackTrace();
//            csCartOrders=new CsCartOrderRes();
//            logger.info("********** exception occurred " + e.getMessage());
//        }
//        return csCartOrders;
//    }
//
//    private CsCartOrderDetailsRes apiHttpRequestGetOrderDetails(MultiValueMap<String, String> json, String url) {
//
//        CsCartOrderDetailsRes csCartOrders;
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        String auth = applicationPropertiesValues.swiggoBackendUsername + ":" + applicationPropertiesValues.swiggoBackendAccessKey;
//
//        byte[] encodedAuth = Base64.encodeBase64(
//                auth.getBytes(Charset.forName("US-ASCII")));
//        String authHeader = "Basic " + new String(encodedAuth);
//        httpHeaders.set("Authorization", authHeader);
//
//        logger.info("**** get orders url  "+url);
//        logger.info("**** auth token "+authHeader);
//        try {
//            final ResponseEntity<CsCartOrderDetailsRes> balanceResponse = httpCallOrderDetails.sendAPIGetRequest(url, json, httpHeaders, CsCartOrderDetailsRes.class);
//            csCartOrders = balanceResponse.getBody();
//
//            assert csCartOrders != null;
//            logger.info("================== api get orders : " + csCartOrders.toString());
//
//            return csCartOrders;
//        } catch (Exception e) {
//            e.printStackTrace();
//            csCartOrders=new CsCartOrderDetailsRes();
//            logger.info("********** exception occurred  while getting order details " + e.getMessage());
//        }
//        return csCartOrders;
//    }
//}
