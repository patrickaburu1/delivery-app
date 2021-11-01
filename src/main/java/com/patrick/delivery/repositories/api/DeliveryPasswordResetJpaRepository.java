package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DeliveryPasswordReset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author patrick on 6/30/20
 * @project sprintel-delivery
 */
@Repository
public interface DeliveryPasswordResetJpaRepository extends CrudRepository<DeliveryPasswordReset,Long> {

    List<DeliveryPasswordReset> findAllByUserId(Long userId);
}
