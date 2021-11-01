package com.patrick.delivery.controllers;

import com.patrick.delivery.services.DashboardService;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.utils.templates.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author patrick on 6/9/20
 * @project sprintel-delivery
 */
@RestController
public class DashboardController extends AbstractController {

    @Autowired
    private DashboardService dashboardService;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public Object dashBoard(RedirectAttributes redirectAttributes, HttpServletRequest request) {

        return new View("app/dashboard").getView();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object index(RedirectAttributes redirectAttributes, HttpServletRequest request) {

        return new View("app/dashboard").getView();
    }


    @GetMapping(value = "/dashboard/data")
    public ResponseModel getDashboardData(){
        return dashboardService.getDashboardData();
    }

    @GetMapping(value = "/calendar/data")
    public Object getCalendarData(@RequestParam("start") String start, @RequestParam("end") String end, @RequestParam("_") Long currentDate){
        return dashboardService.getCalendarData(start,end,currentDate);
    }
}
