package com.patrick.delivery.services;

import com.patrick.delivery.entities.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.patrick.delivery.models.CalendarResponse;
import com.patrick.delivery.repositories.api.UserTypeRepository;
import com.patrick.delivery.security.service.CustomApiUserDetailsService;
import com.patrick.delivery.utils.AppConstant;
import com.patrick.delivery.utils.AppFunction;
import com.patrick.delivery.utils.GeneralLogger;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

/**
 * @author patrick on 6/11/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class DashboardService extends GeneralLogger {
    @Autowired
    private CustomApiUserDetailsService customApiUserDetailsService;

    @PersistenceContext(unitName = "delivery")
    private EntityManager entityManager;
    @Autowired
    private UserTypeRepository userTypeRepository;

    public ResponseModel getDashboardData() {
        Map<String, Object> map = new HashMap<>();
        JPQLQuery<?> query = new JPAQuery<>(entityManager);
        JPQLQuery<?> queryPendingOrder = new JPAQuery<>(entityManager);
        JPQLQuery<?> query1 = new JPAQuery<>(entityManager);
        JPQLQuery<?> query2 = new JPAQuery<>(entityManager);
        JPQLQuery<?> query3 = new JPAQuery<>(entityManager);
        QUsers qUsers = QUsers.users;

        UserTypes userType = userTypeRepository.findFirstByCode(UserTypes.RIDER);

        Long activeRiders = query.select(qUsers).from(qUsers)
                .where(qUsers.userTypeNo.eq(userType.getId()).and(qUsers.status.eq(AppConstant.STATUS_ACTIVE_RECORD)))
                .fetchCount();

        QDeliveryOrders qDeliveryOrders = QDeliveryOrders.deliveryOrders;

        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;


        Long pendingDeliveries = queryPendingOrder.select(qDeliveryOrders).from(qDeliveryOrders)
                .where(qDeliveryOrders.orderState.eq(DeliveryOrders.STATUS_PENDING)).fetchCount();


        Long assignedOrders = query1.select(qDeliveryOrders)
                .from(qDeliveryOrders)
                .where(qDeliveryOrders.orderState.eq(DeliveryOrders.STATUS_ASSIGNED)
                        .or(qDeliveryOrders.orderState.eq(DeliveryOrders.STATUS_ACCEPTED))
                        .or(qDeliveryOrders.orderState.eq(DeliveryOrders.STATUS_INTRANSIT)))
                .fetchCount();


        Long completedOrder = query2.select(qDeliveryOrders).from(qDeliveryOrders)
                .where(qDeliveryOrders.orderState.eq(DeliveryOrders.STATUS_COMPLETED)).fetchCount();

        Long declinedDeliveries = query3.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                .where(qDriverOrdersRequests.state.eq(DeliveryOrders.STATUS_REJECTED)).fetchCount();

        map.put("activeRiders", activeRiders);
        map.put("pendingDeliveries", pendingDeliveries);
        map.put("assignedOrders", assignedOrders);
        map.put("completedOrders", completedOrder);
        map.put("declinedDeliveries", declinedDeliveries);
        return new ResponseModel("00", "success", map);
    }


    public Object getCalendarData(String startDate, String endDate, Long currentDateLng) {
        Map<String, Object> map = new HashMap<>();

     /* Date current = new Date(currentDateLng);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        Date begOfMonth=calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endOfMonth=calendar.getTime();

        startDate=AppFunction.formatDateToStringDateSystemFormat(begOfMonth);
        endDate=AppFunction.formatDateToStringDateSystemFormat(endOfMonth);
*/
        Integer days = AppFunction.getDiffBetweenDates(startDate, endDate);

        Calendar cal = Calendar.getInstance();
        Date date = AppFunction.formatStringToDate(startDate);
        cal.setTime(date);
        date = cal.getTime();


        List<CalendarResponse> calendarResponses = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            JPQLQuery<?> allOrders = new JPAQuery<>(entityManager);
            JPQLQuery<?> pendingOrders = new JPAQuery<>(entityManager);
            JPQLQuery<?> completedOrders = new JPAQuery<>(entityManager);

            String strDate = AppFunction.formatDateToStringDateSystemFormat(date);

            QDeliveryOrders qDeliveryOrders = QDeliveryOrders.deliveryOrders;
            QDeliveryOrders qDeliveryOrdersPending = QDeliveryOrders.deliveryOrders;
            QDeliveryOrders qDeliveryOrdersCompleted = QDeliveryOrders.deliveryOrders;

            Calendar calMinMax = Calendar.getInstance();
            calMinMax.setTime(date);
            calMinMax.set(Calendar.HOUR, calMinMax.getActualMinimum(Calendar.HOUR_OF_DAY));
            Date minDate=calMinMax.getTime();

            calMinMax.set(Calendar.HOUR, calMinMax.getActualMaximum(Calendar.HOUR_OF_DAY));
            Date maxDate=calMinMax.getTime();


            long all = allOrders.select(qDeliveryOrders)
                    .from(qDeliveryOrders)
                    .where(qDeliveryOrders.creationDate.between(minDate, maxDate))
                    .fetchCount();

            CalendarResponse calendarResponse = new CalendarResponse();
            calendarResponse.setDescription("All Orders");
            calendarResponse.setTitle("All Orders " + all);
            calendarResponse.setStart(strDate);
            calendarResponse.setEnd(strDate);
            calendarResponse.setBackgroundColor("#1976d2");

            calendarResponses.add(calendarResponse);

            long pending = pendingOrders.select(qDeliveryOrdersPending)
                    .from(qDeliveryOrdersPending)
                    .where(qDeliveryOrdersPending.orderState.eq(DeliveryOrders.STATUS_PENDING)
                            .and(qDeliveryOrdersPending.creationDate.between(minDate, maxDate)))
                    .fetchCount();

            CalendarResponse calendarResponsePending = new CalendarResponse();
            calendarResponsePending.setDescription("Pending Orders");
            calendarResponsePending.setTitle("Pending " + pending);
            calendarResponsePending.setStart(strDate);
            calendarResponsePending.setEnd(strDate);
            calendarResponsePending.setBackgroundColor("#ffb22b");

            calendarResponses.add(calendarResponsePending);

            long completed = completedOrders.select(qDeliveryOrdersCompleted)
                    .from(qDeliveryOrdersCompleted)
                    .where(qDeliveryOrdersCompleted.orderState.eq(DeliveryOrders.STATUS_COMPLETED)
                            .and(qDeliveryOrdersCompleted.creationDate.between(minDate, maxDate)))
                    .fetchCount();

            CalendarResponse calendarResponseCompleted = new CalendarResponse();
            calendarResponseCompleted.setDescription("Competed Orders");
            calendarResponseCompleted.setTitle("Completed " + completed);
            calendarResponseCompleted.setStart(strDate);
            calendarResponseCompleted.setEnd(strDate);
            calendarResponseCompleted.setBackgroundColor("#26dad2");

            calendarResponses.add(calendarResponseCompleted);

            cal.add(Calendar.DATE, 1);
            date = cal.getTime();

        }
        return calendarResponses;
    }
}
