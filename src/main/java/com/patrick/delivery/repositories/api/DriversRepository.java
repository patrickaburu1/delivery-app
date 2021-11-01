package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.Drivers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 6/10/20
 * @project sprintel-delivery
 */
@Repository
public interface DriversRepository extends CrudRepository<Drivers,Long> {

    Drivers findFirstByUserId(Long userId);
}
