package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.AppRoles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author patrick on 8/1/20
 * @project sprintel-delivery
 */
@Repository
public interface AppRoleRepository extends CrudRepository<AppRoles,Long> {

    List<AppRoles> findAllByStatus(String status);
}
