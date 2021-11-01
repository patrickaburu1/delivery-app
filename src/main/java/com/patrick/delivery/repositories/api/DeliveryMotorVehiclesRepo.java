package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DeliveryMotorVehicles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 6/16/20
 * @project sprintel-delivery
 */
@Repository
public interface DeliveryMotorVehiclesRepo extends CrudRepository<DeliveryMotorVehicles, Long> {
    DeliveryMotorVehicles findFirstByRegNo(String regNo);
}
