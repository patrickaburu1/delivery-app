package com.patrick.delivery.tasks;

import com.patrick.delivery.services.ScheduledTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
@Component
public class OrdersTasks {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    //run after every minute
   // @Scheduled(cron = "0 * * * * *")
    @Scheduled(fixedRate = 1000*60*2)
    public void getOrders() {
        logger.info("******* started task to get orders");
       // scheduledTaskService.getOrdersFromCscartDb();
        //scheduledTaskService.getPlatformVendors();
        //scheduledTaskService.getOrdersFromCartViaAPI();
    }


}
