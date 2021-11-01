package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DeliveryOrderProducts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author patrick on 7/10/20
 * @project sprintel-delivery
 */
@Repository
public interface DeliveryOrderProductsRepository extends CrudRepository<DeliveryOrderProducts, Long> {

    DeliveryOrderProducts findFirstByItemIdAndOrderId(String itemId, Long orderId);

    List<DeliveryOrderProducts> findAllByOrderId( Long orderId);
}
