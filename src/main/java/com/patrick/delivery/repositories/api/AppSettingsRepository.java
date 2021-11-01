package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.AppSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author patrick on 6/29/20
 * @project sprintel-delivery
 */
@Repository
public interface AppSettingsRepository extends CrudRepository<AppSettings,Long> {

    AppSettings findByCode(String code);
}
