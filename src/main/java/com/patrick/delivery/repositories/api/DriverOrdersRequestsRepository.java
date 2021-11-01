package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DriverOrdersRequests;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 6/10/20
 * @project sprintel-delivery
 */
@Repository
public interface DriverOrdersRequestsRepository extends CrudRepository<DriverOrdersRequests, Long> {
}
