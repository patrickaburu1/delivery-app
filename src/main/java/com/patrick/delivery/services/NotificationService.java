package com.patrick.delivery.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.delivery.models.InfoBipSmsReq;
import com.patrick.delivery.properties.ApplicationPropertiesValues;
import com.patrick.delivery.utils.GeneralLogger;
import com.patrick.delivery.utils.templates.Console;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

/**
 * @author patrick on 6/10/20
 * @project sprintel-delivery
 */
@Service
public class NotificationService extends GeneralLogger {

    @Autowired
    private ApplicationPropertiesValues apv;

    public boolean sendFireBaseNotification(String to, String title, String message,  String context)  {
        try {


            JSONObject dataObj = new JSONObject();
            dataObj.put("title",title);
            dataObj.put("body",message);
            dataObj.put("context",context);

            JSONObject notificationObj = new JSONObject();
            notificationObj.put("to",to);
            notificationObj.put("notification",dataObj);

            ObjectMapper mapper = new ObjectMapper();
            String json = notificationObj.toString();

            //Generate the URL for this request
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .fromHttpUrl("https://fcm.googleapis.com/fcm/send")
                    .build()
                    .encode();

            //Utilize Rest template to execute this request
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("Authorization", apv.firebaseKey);

            HttpEntity<String> httpEntity = new HttpEntity<>(json, httpHeaders);

            logger.info("FIREBASE   REQUEST -------------------------------------------------");
            logger.info(json);
            logger.info("FIREBASE  REQUEST -------------------------------------------------");
            ResponseEntity<String> response
                    = restTemplate.exchange(uriComponents.toString(), HttpMethod.POST, httpEntity, String.class);

            logger.info("FIREBASE  RESPONSE -------------------------------------------------");
            logger.info(response.toString());
            logger.info("FIREBASE  RESPONSE -------------------------------------------------");

            //Parse the response received
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode node = mapper.readTree(response.getBody());
                return "1".equals(node.get("success").asText());
            }

            return true;
        } catch (RestClientException | IOException | JSONException e) {
//            appAuditLogRepo.mailError(e.getLocalizedMessage());
            logger.error("*********** Error sending firebase notification *************", e);
            return false;
        }
    }


    public boolean sendSMS(String phoneNumber, String message){
        try
        {
            List<Object> ldata = new ArrayList<Object>();
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            Map<String, Object> data = new LinkedHashMap<String, Object>();

            map.put("apiName", apv.smsAppName);
            map.put("apiKey", apv.smsApiKey);
            map.put("apiVersion", apv.smsApiVersion);

            data.put("message", message);
            data.put("recipients", phoneNumber);
            //data.put("cabCompany", smsOptions.getCabCompany());
            ldata.add( data );

            map.put("data", ldata);

            //AppFunctions.disableSslVerification();

            //fetch request data
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString( map );

            /*Generate the URL for this request*/
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .fromHttpUrl(apv.smsUrl)
                    .build()
                    .encode();

            /*Utilize Rest template to execute this request*/
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(json, httpHeaders);

            logger.info("SMS REQUEST -------------------------------------------------");
            logger.info( json );
            logger.info("SMS REQUEST -------------------------------------------------");
         /*   ResponseEntity<String> response
                    = restTemplate.exchange(uriComponents.toString(), org.springframework.http.HttpMethod.POST, httpEntity, String.class);
*/
            logger.info("SMS RESPONSE -------------------------------------------------");
         //   logger.info( response.toString() );
            logger.info("SMS RESPONSE -------------------------------------------------");

            /*Parse the response received*/
         /*   if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode node = mapper.readTree(response.getBody());
                return "00".equals( node.get("status").asText() );
            }*/

            return true;
        }
        catch(RestClientException | IOException e){
//            appAuditLogRepo.mailError(e.getLocalizedMessage());
            Console.printStackTrace(e);
            return false;
        }
    }
    public boolean sendSMSInfoBib(String phoneNumber, String message){
        try
        {
            List<Object> ldata = new ArrayList<Object>();
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            Map<String, Object> data = new LinkedHashMap<String, Object>();

            map.put("apiName", apv.smsAppName);
            map.put("apiKey", apv.smsApiKey);
            map.put("apiVersion", apv.smsApiVersion);

            data.put("message", message);
            data.put("recipients", phoneNumber);
            //data.put("cabCompany", smsOptions.getCabCompany());
            ldata.add( data );

            map.put("data", ldata);

            //AppFunctions.disableSslVerification();

            InfoBipSmsReq infoBipSmsReq=new InfoBipSmsReq();
            infoBipSmsReq.setFrom("AGILE");
            infoBipSmsReq.setText(message);
            List<InfoBipSmsReq.Destinations> destinations=new ArrayList<>();
            InfoBipSmsReq.Destinations destination=new InfoBipSmsReq.Destinations();
            destination.setTo(phoneNumber);
            destinations.add(destination);

            infoBipSmsReq.setDestinations(destinations);

            Map<String, Object> mapInfoBip =new HashMap<>();
            mapInfoBip.put("messages",infoBipSmsReq);

            //fetch request data
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString( mapInfoBip );

            /*Generate the URL for this request*/
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .fromHttpUrl(apv.smsUrl)
                    .build()
                    .encode();

            /*Utilize Rest template to execute this request*/
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set(HttpHeaders.AUTHORIZATION,"App 682d164dc9cd5c92d4eee1b26e1ba620-35816511-7055-403a-93c6-33e171b07a86");

            HttpEntity<String> httpEntity = new HttpEntity<>(json, httpHeaders);

            logger.info("SMS REQUEST -------------------------------------------------");
            logger.info( json );
            logger.info("SMS REQUEST -------------------------------------------------");
            ResponseEntity<String> response
                    = restTemplate.exchange(uriComponents.toString(), org.springframework.http.HttpMethod.POST, httpEntity, String.class);
            logger.info("SMS RESPONSE -------------------------------------------------");
         //   logger.info( response.toString() );
            JsonNode node = mapper.readTree(response.getBody());
            logger.info("SMS RESPONSE ------------------------------------------------- {} ",node);

            /*Parse the response received*/
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode node1 = mapper.readTree(response.getBody());
                return true;
            }

            return true;
        }
        catch(RestClientException | IOException e){
//            appAuditLogRepo.mailError(e.getLocalizedMessage());
            Console.printStackTrace(e);
            return false;
        }
    }

}
