package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.Vendors;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 7/17/20
 * @project sprintel-delivery
 */
@Repository
public interface VendorsRepository extends CrudRepository<Vendors, Long> {

    Vendors findFirstByCompanyIdAndPlatformId(Long companyId, Long platformId);
}
