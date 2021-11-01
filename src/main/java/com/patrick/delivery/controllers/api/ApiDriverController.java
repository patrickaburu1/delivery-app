package com.patrick.delivery.controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.patrick.delivery.models.*;
import com.patrick.delivery.security.api.TokenService;
import com.patrick.delivery.services.DriverService;
import com.patrick.delivery.services.OrdersService;
import com.patrick.delivery.utils.ApiAbstractController;
import com.patrick.delivery.utils.AppConstant;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
@RestController
@RequestMapping("/api/v1/driver")
public class ApiDriverController extends ApiAbstractController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private TokenService tokenService;

    @GetMapping("/orders")
    public ResponseModel orders(@RequestHeader("Authorization") String authHeader,
                                @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        ResponseModel response;
        try {
            response = ordersService.driverOrders(page, size);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/orders", getObjectMapper().convertValue(page, JsonNode.class), e);
        }
        return apiLogger("/orders", getObjectMapper().convertValue(page, JsonNode.class), response);

    }

    @PostMapping("/respond-to-order")
    public ResponseModel responseToOrder(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody ApiAcceptOrdersReq req) {
        ResponseModel response;
        try {
            response = ordersService.respondToOrder(req);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/respond-to-order", getObjectMapper().convertValue(req, JsonNode.class), e);
        }
        return apiLogger("/respond-to-order", getObjectMapper().convertValue(req, JsonNode.class), response);

    }

    @PostMapping("/order-collected")
    public ResponseModel orderCollected(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody ApiOrderStateReq req) {
        ResponseModel response;
        try {
            response = ordersService.orderCollected(req);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/order-collected", getObjectMapper().convertValue(req, JsonNode.class), e);
        }
        return apiLogger("/order-collected", getObjectMapper().convertValue(req, JsonNode.class), response);

    }

    @PostMapping("/order-delivered")
    public ResponseModel completeOrder(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody  ApiOrderStateReq req) {
        ResponseModel response;
        try {
            response = ordersService.orderCompleted(req);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/order-delivered", getObjectMapper().convertValue(req, JsonNode.class), e);
        }
        return apiLogger("/order-delivered", getObjectMapper().convertValue(req, JsonNode.class), response);

    }

    @PostMapping(value = "/change-password")
    public ResponseModel changePassword(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody ApiChangePasswordReq request) {

        ResponseModel response;
        try {
            response = driverService.changePassword(request);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/change-password", getObjectMapper().convertValue(request, JsonNode.class), e);
        }
        return apiLogger("/change-password", getObjectMapper().convertValue(request, JsonNode.class), response);
    }


    @GetMapping("/driver-info")
    public ResponseModel profile(@RequestHeader("Authorization") String authHeader) {
        ResponseModel response;
        try {
            response = driverService.driverInfo();
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/driver-info", getObjectMapper().convertValue(null, JsonNode.class), e);
        }
        return apiLogger("/driver-info", getObjectMapper().convertValue(null, JsonNode.class), response);

    }

    @PostMapping("/update-status")
    public ResponseModel updateStatus(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody ApiDriverStatusReq req) {
        ResponseModel response;
        try {
            response = driverService.updateDriverState(req);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/update-status", getObjectMapper().convertValue(req, JsonNode.class), e);
        }
        return apiLogger("/update-status", getObjectMapper().convertValue(req, JsonNode.class), response);

    }

    @GetMapping("/pending-orders")
    public ResponseModel pendingOrders(@RequestHeader("Authorization") String authHeader,
                                @RequestParam(value = "page") Integer page, @RequestParam(value = "size") Integer size) {
        ResponseModel response;
        try {
            response = ordersService.driverPendingOrders(page, size);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/pending-orders", getObjectMapper().convertValue(page, JsonNode.class), e);
        }
        return apiLogger("/pending-orders", getObjectMapper().convertValue(page, JsonNode.class), response);

    }

    @PostMapping("/rider-location")
    public ResponseModel riderLocations(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody ApiDriverLocationReq req) {
        ResponseModel response;
        try {
            response = driverService.updateDriverLocation(req);
            response.setRefreshedToken(refreshedToken(authHeader));
        } catch (Exception e) {
            return apiError("/rider-location", getObjectMapper().convertValue(req, JsonNode.class), e);
        }
        return apiLogger("/rider-location", getObjectMapper().convertValue(req, JsonNode.class), response);

    }

    /**
     * Called to render the response as generated by the class item
     *
     * @param url
     * @param request
     * @param response
     * @return ResponseModel
     */
    protected ResponseModel apiLogger(String url, JsonNode request, ResponseModel response) {
        return super.apiLogger("/api/v1/driver" + url, filterRequest(request, ""), response);
    }

    /**
     * Called to render the response as generated by the class item
     *
     * @param url
     * @param request
     * @param ex
     * @return ResponseModel
     */
    protected ResponseModel apiError(String url, JsonNode request, Exception ex) {
        return super.apiError("/api/v1/driver" + url, filterRequest(request, ""), ex);
    }


    private String refreshedToken(String authHeader) {
        ResponseModel response = tokenService.refreshAccessToken(authHeader);
        if (response.getStatus().equalsIgnoreCase(AppConstant.SUCCESS_STATUS_CODE))
            return response.getRefreshedToken();
        else
            return null;
    }
}
