package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DeliveryOrders;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
@Repository
public interface DeliveryOrdersRepository extends CrudRepository<DeliveryOrders, Long> , QuerydslPredicateExecutor<DeliveryOrders> {

    DeliveryOrders findFirstByOrderNo(Long orderNo );
    DeliveryOrders findFirstByOrderNoAndPlatformId(Long orderNo , Long platformId);
}

