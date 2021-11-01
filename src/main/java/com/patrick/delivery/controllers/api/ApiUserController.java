package com.patrick.delivery.controllers.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.delivery.models.ApiForgotPasswordReq;
import com.patrick.delivery.models.ApiResetPasswordReq;
import com.patrick.delivery.models.LoginRequest;
import com.patrick.delivery.security.service.ApiUserService;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.controllers.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */
@RestController
@RequestMapping("/api/v1")
public class ApiUserController extends AbstractController {


    @Autowired
    private ApiUserService apiUserService;

    @PostMapping(value = "/login")
    public ResponseEntity login(@Valid  @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        Map<String, Object> response = apiUserService.login(request,httpServletRequest);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(response);
            logger.info("*************** api login response "+json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/forgot-password")
    public ResponseModel forgotPassword(@Valid  @RequestBody ApiForgotPasswordReq request, HttpServletRequest httpServletRequest) {
        ResponseModel response = apiUserService.forgotPassword(request);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(response);
            logger.info("*************** api forgot response "+json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping(value = "/reset-password")
    public ResponseModel resetPassword(@Valid  @RequestBody ApiResetPasswordReq request) {
        ResponseModel response = apiUserService.resetPassword(request);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(response);
            logger.info("*************** api reset response "+json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }


}
