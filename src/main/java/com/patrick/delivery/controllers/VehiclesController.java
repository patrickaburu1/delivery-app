package com.patrick.delivery.controllers;

import com.patrick.delivery.entities.DeliveryMotorVehicles;
import com.patrick.delivery.models.NewVehicleReq;
import com.patrick.delivery.services.VehicleService;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.utils.interfaces.DataTableNewInterface;
import com.patrick.delivery.utils.templates.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author patrick on 7/27/20
 * @project sprintel-delivery
 */
@RestController
public class VehiclesController extends AbstractController {

    @Autowired
    private DataTableNewInterface dataTable;
    @Autowired
    private VehicleService vehicleService;

    @RequestMapping(value = "/vehicles", method = RequestMethod.GET)
    public Object orders(RedirectAttributes redirectAttributes, HttpServletRequest request) {

        return new View("app/vehicles").getView();
    }

    @GetMapping(value = "/vehicles-list")
    public Object ordersData(HttpServletRequest request) {
        return vehicles(request);
    }

    private Object vehicles(HttpServletRequest request) {
        dataTable.select(" v.id, v.reg_no, v.status, u.first_name, u.last_name, u.phone,  DATE_FORMAT(v.creation_date; '%Y-%m-%d %H:%i:%s')")
                .join("driver_motor_vehicles dv on v.id = dv.vehicle_id")
                .join("drivers d on d.id = dv.driver_id")
                .join("users u on  u.id=d.user_id")
                .from("motor_vehicles as v")
                .orderBy("v.id desc")
                .nativeSQL(true);

        return dataTable.showTable();
    }

    @RequestMapping(value = "/new-vehicle", method = RequestMethod.POST)
  //  @PostAuthorize("hasRole('ROLE_VEHICLES')")
    public ResponseModel newVehicle(@ModelAttribute NewVehicleReq request) {
        return vehicleService.newVehicle(request);
    }

    @GetMapping(value = "/vehicle-info/{vehicleId}")
    public ResponseModel vehicleInfo(HttpServletRequest request, @PathVariable Long vehicleId) {
        return vehicleService.newVehicleInfo(vehicleId);
    }


    @RequestMapping(value = "/edit-vehicle", method = RequestMethod.POST)
    //  @PostAuthorize("hasRole('ROLE_VEHICLES')")
    public ResponseModel editVehicle(@ModelAttribute DeliveryMotorVehicles request) {
        return vehicleService.editVehicleDetails(request);
    }

    @GetMapping(value = "/activate-deactivate/{status}/{vehicleId}")
    public ResponseModel activateDeactivate(HttpServletRequest request,@PathVariable String status, @PathVariable Long vehicleId) {
        return vehicleService.activateDeactivate(status,vehicleId);
    }
}
