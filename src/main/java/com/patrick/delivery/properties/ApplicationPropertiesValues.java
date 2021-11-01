package com.patrick.delivery.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPropertiesValues {

    @Value("${jwt.expires_in}")
    public long expiresIn;

    @Value("${jwt.header}")
    public String authHeader;

    @Value("${api.access_key}")
    public String apiAccessKey;

    @Value("${app.name}")
    public String appName;

    @Value("${jwt.secret}")
    public String base64SecretBytes;

    @Value("${jwt.token_refresh_time_before_expiry}")
    public int tokenRefreshTimeBeforeExpiry;

    @Value("${firebase.key}")
    public String firebaseKey;

    @Value("${app.endpoint}")
    public String appEndPoint;

    @Value("${sms_endpoint}")
    public String smsUrl;

    @Value("${sms_api_name}")
    public String smsAppName;

    @Value("${sms_api_key}")
    public String smsApiKey;

    @Value("${sms_api_version}")
    public String smsApiVersion;


    @Value("${location.pin.lat}")
    public Double locationPinLat;

    @Value("${location.pin.lng}")
    public Double locationPinLng;

    @Value("${location.circle.radius}")
    public Integer locationCircleRadius;
}
