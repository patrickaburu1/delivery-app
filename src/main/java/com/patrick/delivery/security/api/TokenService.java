package com.patrick.delivery.security.api;


import com.patrick.delivery.security.service.AbstractService;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Kaburu
 */
@Service
public class TokenService extends AbstractService {

    @Autowired
    private TokenHelper tokenHelper;

    @Value("${api.access_key}")
    private String accessKey;

    public Map<String, Object> mapResponse(Map<String, Object> response, Map<String, Object> tokenRefreshResponse) {

        if (tokenRefreshResponse.get("status").equals("00")) {
            response.put("accessToken", tokenRefreshResponse.get("accessToken"));
        } else if (tokenRefreshResponse.get("status").equals("003")) {
            response.clear();
            response.put("status", "003");
            response.put("message", "Session Expired!");
        }

        return response;
    }

    /**
     * Refresh Token
     *
     * @param
     * @return Map
     */

    public ResponseModel refreshAccessToken(String authToken) {
        ResponseModel response = new ResponseModel();

        String refreshedToken = "";

        if (authToken != null && accessKey != null) {

            if (authToken.startsWith("Bearer "))
                authToken = authToken.substring(7);

            if (tokenHelper.isTokenExpired(authToken)) {

                return new ResponseModel("003", "Session Expired!");
            } else {

                if (tokenHelper.shouldTokenBeRefreshed(authToken)) {
                    refreshedToken = tokenHelper.refreshToken(authToken);
                    logger.info("************ token refreshed " + refreshedToken);
                    response.setStatus("00");
                    response.setMessage("Successful refreshed token.");
                    response.setRefreshedToken(refreshedToken);
                    return response;
                } else {

                    logger.info("************ token not refreshed");
                    return new ResponseModel("01", "Not Refreshed!");
                }

            }

        }

        return response;

    }

}
