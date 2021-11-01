package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DeliveryPlatforms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author patrick on 7/9/20
 * @project sprintel-delivery
 */
@Repository
public interface DeliveryPlatformRepository extends CrudRepository<DeliveryPlatforms,Long> {

    List<DeliveryPlatforms> findAllByStatus(String status);
    List<DeliveryPlatforms> findAllByStatusAndStorefrontNoIsNotNull(String status);
}
