package com.patrick.delivery.configs;

import com.patrick.delivery.entities.AppSettings;
import com.patrick.delivery.repositories.api.AppSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author patrick on 6/30/20
 * @project sprintel-delivery
 */
@Transactional
@Service
public class AppSettingsService {

    @Autowired
    private AppSettingsRepository appSettingsRepository;

    public AppSettings getAppSettings(String settingsKey){
        return appSettingsRepository.findByCode(settingsKey);
    }
}
