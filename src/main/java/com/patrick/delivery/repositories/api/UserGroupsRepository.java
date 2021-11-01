package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.UserGroups;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 7/21/20
 * @project sprintel-delivery
 */
@Repository
public interface UserGroupsRepository extends CrudRepository<UserGroups,Long> {
    UserGroups findFirstById(Long groupId);
}
