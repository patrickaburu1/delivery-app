package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author patrick on 6/6/20
 *
 * @project sprintel-delivery
 */
@Repository
public interface UsersRepository extends CrudRepository<Users,Long> {

    Optional<Users> findDistinctByPhone(String phone);

    Optional<Users> findDistinctByEmail(String email);

    Users findFirstByEmail(String email);

    Users findByPhoneAndUserTypeNo(String phone, Long userTypeNo);

    Users findByEmailAndUserTypeNo(String phone, Long userTypeNo);
}
