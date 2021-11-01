package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DeliveryDriverMotorVehicles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 6/16/20
 * @project sprintel-delivery
 */
@Repository
public interface DeliveryDriverMotorVehiclesRepo extends CrudRepository<DeliveryDriverMotorVehicles,Long> {

    DeliveryDriverMotorVehicles findFirstByVehicleIdAndStatus(Long vehicleId, String status);
    DeliveryDriverMotorVehicles findFirstByDriverIdAndStatus(Long driverId, String status);
}
