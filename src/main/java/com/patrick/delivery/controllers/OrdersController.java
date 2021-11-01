package com.patrick.delivery.controllers;

import com.patrick.delivery.models.AssignOrderReq;
import com.patrick.delivery.services.OrdersService;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.utils.templates.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author patrick on 6/10/20
 * @project sprintel-delivery
 */
@RestController
public class OrdersController extends AbstractController {

    @Autowired
    private OrdersService ordersService;


    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public Object orders(RedirectAttributes redirectAttributes, HttpServletRequest request) {

        return new View("app/orders").getView();
    }

    @GetMapping(value = "orders-list")
    public Object ordersData(HttpServletRequest request){
        String status = request.getParameter("0[status]") == null ? "" : request.getParameter("0[status]");
       return ordersService.orders(status, request);
    }



    @PostMapping(value = "/assign-order")
    public ResponseModel assignOrder(@RequestBody AssignOrderReq request){
        return ordersService.assignOrder(request);
    }


    @GetMapping(value = "/order-details/{orderId}")
    public ResponseModel orderDetails(@PathVariable Long orderId){
        return ordersService.getOrderDetails( orderId);
    }

}
