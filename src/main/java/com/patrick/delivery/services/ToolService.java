package com.patrick.delivery.services;

import com.patrick.delivery.utils.GeneralLogger;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author patrick on 6/30/20
 * @project sprintel-delivery
 */
@Service
public class ToolService extends GeneralLogger {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    public ResponseModel getProcessOrdersFromCsCart() {
       // scheduledTaskService.getOrdersFromCscartDb();
       // scheduledTaskService.getOrdersFromCartViaAPI();
        return new ResponseModel("00", "Get processed order");
    }

    public ResponseModel getVendors() {
        scheduledTaskService.getPlatformVendors();
        return new ResponseModel("00", "Get processed order");
    }
}
