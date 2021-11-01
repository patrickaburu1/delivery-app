package com.patrick.delivery.controllers;

import com.antkorwin.xsync.XSync;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.models.UpdateDriverReq;
import com.patrick.delivery.services.DriverService;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.utils.templates.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author patrick on 6/16/20
 * @project sprintel-delivery
 */
@RestController
public class DriversController extends AbstractController {

    @Autowired
    private DriverService driverService;
    @Autowired
    private XSync<String> xSync;

    @GetMapping(value = "available-drivers")
    public ResponseModel availableDrivers(HttpServletRequest request) {
        return driverService.availableDrivers();
    }

    @GetMapping(value = "/drivers-list")
    public Object driversList(HttpServletRequest request) {
        String status = request.getParameter("0[status]") == null ? "" : request.getParameter("0[status]");
        return driverService.riders(status, request);
    }

    @GetMapping(value = "/driver-details/{driverId}")
    public Object driverDetails(@PathVariable Long driverId, HttpServletRequest request) {
        return driverService.driverDetails(driverId);
    }

    @GetMapping(value = "/available-bikes/{driverId}")
    public Object availableBikes(@PathVariable Long driverId, HttpServletRequest request) {
        return driverService.availableMotorBikes(driverId);
    }

    @PostMapping(value = "update-driver")
    public Object updateDriverDetails(@ModelAttribute UpdateDriverReq req) {

        ResponseModel response= xSync.evaluate(req.getId().toString(), () -> driverService.updateDriverDetails(req));
        return response;
    }


    @RequestMapping(value = "/riders", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_RIDERS')")
    public Object riders(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        return new View("app/riders").getView();
    }


    @GetMapping(value = "/driver-orders/{driverId}")
    public Object driverOrders(@PathVariable Long driverId, HttpServletRequest request) {
        return driverService.driverOrders(driverId);
    }

    @RequestMapping(value = "/new-rider", method = RequestMethod.POST)
    @PostAuthorize("hasRole('ROLE_RIDERS_CREATE')")
    public ResponseModel newUser(@ModelAttribute Users request) {
        return driverService.newRider(request);
    }
}
