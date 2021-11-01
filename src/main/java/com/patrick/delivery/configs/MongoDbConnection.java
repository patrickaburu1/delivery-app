package com.patrick.delivery.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author patrick on 8/18/20
 * @project sprintel-delivery
 */
@Configuration
@EnableMongoRepositories(
        basePackages = {"com.patrick.delivery.repositories.locationRepos"})
public class MongoDbConnection {

}
