package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.UserTypes;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */
@Repository
public interface UserTypeRepository extends CrudRepository<UserTypes,Long> {

    UserTypes findFirstByCode(String code);
}
