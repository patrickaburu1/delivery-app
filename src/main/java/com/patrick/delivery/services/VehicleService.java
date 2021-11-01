package com.patrick.delivery.services;

import com.patrick.delivery.entities.DeliveryDriverMotorVehicles;
import com.patrick.delivery.entities.DeliveryMotorVehicles;
import com.patrick.delivery.models.NewVehicleReq;
import com.patrick.delivery.repositories.api.DeliveryDriverMotorVehiclesRepo;
import com.patrick.delivery.repositories.api.DeliveryMotorVehiclesRepo;
import com.patrick.delivery.utils.AppConstant;
import com.patrick.delivery.utils.GeneralLogger;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @author patrick on 7/27/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class VehicleService extends GeneralLogger {
    @Autowired
    private DeliveryMotorVehiclesRepo deliveryMotorVehiclesRepo;
    @Autowired
    private DeliveryDriverMotorVehiclesRepo deliveryDriverMotorVehiclesRepo;

    public ResponseModel newVehicle(NewVehicleReq request) {
        DeliveryMotorVehicles deliveryMotorVehicles = deliveryMotorVehiclesRepo.findFirstByRegNo(request.getRegNo());

        if (null != deliveryMotorVehicles)
            return new ResponseModel("01", "Sorry already existing with a similar registration number");

        deliveryMotorVehicles = new DeliveryMotorVehicles();
        deliveryMotorVehicles.setRegNo(request.getRegNo().toUpperCase());
        deliveryMotorVehicles.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
        deliveryMotorVehiclesRepo.save(deliveryMotorVehicles);

        return new ResponseModel("00", "Successfully registered new vehicle");
    }

    public ResponseModel newVehicleInfo(Long vehicleId) {
        Optional<DeliveryMotorVehicles> deliveryMotorVehicles = deliveryMotorVehiclesRepo.findById(vehicleId);

        if (!deliveryMotorVehicles.isPresent())
            return new ResponseModel("01", "Sorry! Vehicle not found.");
        DeliveryMotorVehicles vehicle = deliveryMotorVehicles.get();
        DeliveryDriverMotorVehicles driverMotorVehicle = deliveryDriverMotorVehiclesRepo.findFirstByVehicleIdAndStatus(vehicle.getId(), AppConstant.STATUS_ACTIVE_RECORD);
        if (null != driverMotorVehicle)
            vehicle.setAssignedTo(driverMotorVehicle.getDriverAssigned().getUserLink().getFirstName() + " (" + driverMotorVehicle.getDriverAssigned().getUserLink().getPhone() + ")");

        return new ResponseModel("00", "Success", vehicle);
    }

    public ResponseModel editVehicleDetails(DeliveryMotorVehicles request) {
        Optional<DeliveryMotorVehicles> deliveryMotorVehicles = deliveryMotorVehiclesRepo.findById(request.getId());

        if (!deliveryMotorVehicles.isPresent())
            return new ResponseModel("01", "Sorry! Vehicle not found.");
        DeliveryMotorVehicles vehicle = deliveryMotorVehicles.get();

        if (vehicle.getRegNo().equalsIgnoreCase(request.getRegNo()))
            return new ResponseModel("01", "No changes found");

        DeliveryMotorVehicles deliveryMotorVehicle = deliveryMotorVehiclesRepo.findFirstByRegNo(request.getRegNo());
        if (null != deliveryMotorVehicle)
            return new ResponseModel("01", "Sorry already existing with a similar registration number");

        vehicle.setRegNo(request.getRegNo());
        vehicle.setLastModifiedDate(new Date());
        deliveryMotorVehiclesRepo.save(vehicle);

        return new ResponseModel("00", "Vehicle details updated successfully.");
    }

    public ResponseModel activateDeactivate(String status, Long vehicleId) {
        Optional<DeliveryMotorVehicles> deliveryMotorVehicles = deliveryMotorVehiclesRepo.findById(vehicleId);

        if (!deliveryMotorVehicles.isPresent())
            return new ResponseModel("01", "Sorry! Vehicle not found.");

        DeliveryMotorVehicles vehicle = deliveryMotorVehicles.get();
        String message;
        if (status.equalsIgnoreCase("activate")) {
            vehicle.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
            message="vehicle activated successfully";
        }
        else {
            vehicle.setStatus(AppConstant.STATUS_INACTIVE_RECORD);
            message="vehicle de-activated successfully";
        }

        deliveryMotorVehiclesRepo.save(vehicle);
        return new ResponseModel("00", message);
    }

}
