package com.patrick.delivery.services;

import com.patrick.delivery.entities.QDeliveryOrders;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.patrick.delivery.entities.DeliveryOrders;
import com.patrick.delivery.locationEntities.RiderLocations;
import com.patrick.delivery.models.ApiDriverOrdersRes;
import com.patrick.delivery.models.TrackOrderRes;
import com.patrick.delivery.repositories.locationRepos.RiderLocationRepository;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @author patrick on 9/8/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class ApiOrderService {

    @PersistenceContext(unitName = "delivery")
    private EntityManager entityManager;
    @Autowired
    private RiderLocationRepository riderLocationRepository;

    @Autowired
    private OrdersService ordersService;

    /**
     * track order
     *
     * @return ResponseModel
     */
    public ResponseModel trackOrder(Long orderNo) {

        JPQLQuery<?> query = new JPAQuery<>(entityManager);
        QDeliveryOrders qDeliveryOrders = QDeliveryOrders.deliveryOrders;
        DeliveryOrders order = query.select(qDeliveryOrders).from(qDeliveryOrders).where(qDeliveryOrders.orderNo.eq(orderNo))
                .fetchFirst();
        if (null == order)
            return new ResponseModel("01", "Sorry order not found.");

        TrackOrderRes trackOrderRes = new TrackOrderRes();

        RiderLocations riderLocation = riderLocationRepository.findByOrderNo(orderNo);
        if (null != riderLocation)
            trackOrderRes.setRiderLocation(new TrackOrderRes.RiderLocRes(riderLocation.getLat(), riderLocation.getLng()));

        trackOrderRes.setOrderNo(order.getOrderNo());
        trackOrderRes.setOrderStatus(order.getOrderState());

        ApiDriverOrdersRes.DeliveryLocRes deliveryLocRes = ordersService.getOrderLocationAddress(order);
        if (null != deliveryLocRes && null!=deliveryLocRes.getLat() )
            trackOrderRes.setDestinationLoc(new TrackOrderRes.DestinationLoc(deliveryLocRes.getLat(), deliveryLocRes.getLng()));

        Map<String, Object> map = new HashMap<>();
        map.put("trackInfo", trackOrderRes);

        return new ResponseModel("00", "Success", map);

    }
}
