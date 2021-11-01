package com.patrick.delivery.security.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        // notify client of response body content type
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        // set the response status code
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // set up the response body
        Status unauthorized = new Status(HttpServletResponse.SC_UNAUTHORIZED,
                authEx.getMessage());
        // write the response body
        objectMapper.writeValue(response.getOutputStream(), unauthorized);
        // commit the response
        response.flushBuffer();

    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("DeveloperStack");
        super.afterPropertiesSet();
    }

    public class Status {
        private int code;
        private String message;

        public Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
