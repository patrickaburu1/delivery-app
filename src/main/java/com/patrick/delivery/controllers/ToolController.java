package com.patrick.delivery.controllers;

import com.patrick.delivery.services.ToolService;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author patrick on 6/30/20
 * @project sprintel-delivery
 */
@RestController
@RequestMapping("/tools")
public class ToolController extends AbstractController {

    @Autowired
    private ToolService toolService;

    @RequestMapping(value = "/get-processed-orders", method = RequestMethod.GET)
    public ResponseModel getProcessOrders() {
        return toolService.getProcessOrdersFromCsCart();
    }

    /*@RequestMapping(value = "/vendors", method = RequestMethod.GET)
    public ResponseModel getVendors() {
        return toolService.getVendors();
    }*/
}
