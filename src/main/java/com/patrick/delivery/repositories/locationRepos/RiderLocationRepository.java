package com.patrick.delivery.repositories.locationRepos;

import com.patrick.delivery.locationEntities.RiderLocations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author patrick on 8/18/20
 * @project sprintel-delivery
 */
public interface RiderLocationRepository extends MongoRepository<RiderLocations,String> {

    public Page<RiderLocations> findByLocNear(Point p, Distance d, Pageable pageable);



    RiderLocations findTopByDriverId(Long driverId);

    RiderLocations findByOrderNo(Long orderNo);

}
