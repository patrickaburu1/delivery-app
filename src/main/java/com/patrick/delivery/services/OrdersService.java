package com.patrick.delivery.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.patrick.delivery.entities.*;
import com.patrick.delivery.models.*;
import com.patrick.delivery.repositories.api.*;
import com.patrick.delivery.utils.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.patrick.delivery.configs.AppSettingsService;
import com.patrick.delivery.properties.ApplicationPropertiesValues;
import com.patrick.delivery.security.service.CustomApiUserDetailsService;
import com.patrick.delivery.utils.interfaces.DataTableNewInterface;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class OrdersService extends GeneralLogger {
    @Autowired
    private CustomApiUserDetailsService customApiUserDetailsService;

    @PersistenceContext(unitName = "delivery")
    private EntityManager entityManager;
    @Autowired
    private DataTableNewInterface dataTable;
    @Autowired
    private DriversRepository driversRepository;
    @Autowired
    private DeliveryOrdersRepository deliveryOrdersRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private DriverOrdersRequestsRepository driverOrdersRequestsRepository;
    @Autowired
    private HttpCall<UpdateOrderRes> httpCall;
    @Autowired
    private ApplicationPropertiesValues applicationPropertiesValues;
    @Autowired
    private AppSettingsService appSettingsService;
    @Autowired
    private DeliveryOrderProductsRepository deliveryOrderProductsRepository;
    @Autowired
    private DeliveryPlatformRepository deliveryPlatformRepository;
    @Autowired
    private VendorsRepository vendorsRepository;

    public static String objectToString(Object object) {
        try {
            ObjectWriter ow = new ObjectMapper().writer();
            return ow.writeValueAsString(object);
        } catch (Exception ex) {
            return " An error has occurred " + ex.getMessage();
        }
    }

    /**
     * get assigned orders
     */
    public ResponseModel driverOrders(Integer page, Integer size) {
        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);

        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

        if (null == page)
            page = 0;
        if (null == size)
            size = 10;

        List<ApiDriverOrdersRes> ordersRes = new ArrayList<>();
        try {

            final List<DriverOrdersRequests> driverOrdersRequests = query.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                    //  .where(qDriverOrdersRequests.state.eq(AppConstant.DRIVER_ORDER_STATUS_PENDING))
                    .where(qDriverOrdersRequests.driverId.eq(user.getId()))
                    .orderBy(qDriverOrdersRequests.id.desc())
                    .offset(page)
                    .limit(size)
                    .fetch();

            logger.info("**************  driver orders count " + driverOrdersRequests.size());
            driverOrdersRequests.forEach(node -> {

                //Orders order = node.getDeliveryOrders().getOrders();
                DeliveryOrders order = node.getDeliveryOrders();
                ApiDriverOrdersRes apiDriverOrdersRes = new ApiDriverOrdersRes();
                apiDriverOrdersRes.setOrderNo(node.getOrderNo());
                apiDriverOrdersRes.setTotal(order.getTotal());
                apiDriverOrdersRes.setOrderState(node.getState());

                apiDriverOrdersRes.setOrderDate(order.getCreationDate().getTime());
                apiDriverOrdersRes.setRequestId(node.getId());

                apiDriverOrdersRes.setDeliveryTimeFrom(convertDeliveryTimeToHumanTime(order.getDeliveryTimeFrom()));
                apiDriverOrdersRes.setDeliveryTimeTo(convertDeliveryTimeToHumanTime(order.getDeliveryTimeTo()));
                apiDriverOrdersRes.setDeliveryDate(AppFunction.convertUnixDateToStringDate(order.getDeliveryDate()));
                apiDriverOrdersRes.setDeliveryNote(order.getDeliveryNote());

                ApiDriverOrdersRes.CustomerRes customerRes = new ApiDriverOrdersRes.CustomerRes();
                customerRes.setAddress(order.getDeliveryAddress());
                customerRes.setCustomerNames(order.getCustomerFirstName() + " " + order.getCustomerLastName());
                customerRes.setCustomerPhone(order.getCustomerPhoneNumber());

                //get delivery location
                customerRes.setDeliveryAddress(getOrderLocationAddress(order));

                apiDriverOrdersRes.setCustomerInfo(customerRes);

                if (node.getDeliveryOrders().getOrderState().equalsIgnoreCase(DeliveryOrders.STATUS_COMPLETED))
                    apiDriverOrdersRes.setDeliveredOn(node.getLastModifiedDate());

                List<DeliveryOrderProducts> deliveryOrderProducts=deliveryOrderProductsRepository.findAllByOrderId(order.getId());
                List<ApiDriverOrdersRes.ProductsRes> productsResList = new ArrayList<>();
                deliveryOrderProducts.forEach(orderDetail -> {
                    ApiDriverOrdersRes.ProductsRes productsRes = new ApiDriverOrdersRes.ProductsRes();

                    productsRes.setName(orderDetail.getProductName());
                    productsRes.setQuantity(Integer.valueOf(orderDetail.getQuantity()));
                    Vendors vendor = vendorsRepository.findFirstByCompanyIdAndPlatformId(orderDetail.getCompanyId(),order.getPlatformId());

                    if (null!=vendor) {
                        productsRes.setVendorName(vendor.getName());
                        //productsRes.setVendorCity(company.getCity());
                        productsRes.setVendorAddress(vendor.getName());
                        //productsRes.setVendorContact(company.getPhone());
                    }

                    productsResList.add(productsRes);

                });

                apiDriverOrdersRes.setProducts(productsResList);

                ordersRes.add(apiDriverOrdersRes);
            });
        } catch (Exception e) {

            logger.info("**************  driver orders an error occurred " + e.getMessage());

        }

        return new ResponseModel("00", "success", ordersRes);
    }

    /**
     * pending orders
     */

    public ResponseModel driverPendingOrders(Integer page, Integer size) {
        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);

        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

        if (null == page)
            page = 0;
        if (null == size)
            size = 10;

        List<ApiDriverOrdersRes> ordersRes = new ArrayList<>();
        try {

            final List<DriverOrdersRequests> driverOrdersRequests = query.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                    //  .where(qDriverOrdersRequests.state.eq(AppConstant.DRIVER_ORDER_STATUS_PENDING))
                    .where(qDriverOrdersRequests.driverId.eq(user.getId())
                            .and(qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_REJECTED)).and(qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_COMPLETED)))
                    .orderBy(qDriverOrdersRequests.id.desc())
                    .offset(page)
                    .limit(size)
                    .fetch();


            logger.info("**************  driver pending orders " + driverOrdersRequests.size());
            driverOrdersRequests.forEach(node -> {

             //   Orders order = node.getDeliveryOrders().getOrders();
                DeliveryOrders order = node.getDeliveryOrders();
                ApiDriverOrdersRes apiDriverOrdersRes = new ApiDriverOrdersRes();
                apiDriverOrdersRes.setOrderNo(node.getOrderNo());
                apiDriverOrdersRes.setTotal(order.getTotal());
                apiDriverOrdersRes.setOrderState(node.getState());
                apiDriverOrdersRes.setOrderDate(order.getCreationDate().getTime());
                apiDriverOrdersRes.setRequestId(node.getId());

                apiDriverOrdersRes.setDeliveryTimeFrom(convertDeliveryTimeToHumanTime(order.getDeliveryTimeFrom() !=null ? order.getDeliveryTimeFrom() : null));
                apiDriverOrdersRes.setDeliveryTimeTo(convertDeliveryTimeToHumanTime(order.getDeliveryTimeTo() !=null ? order.getDeliveryTimeTo() : null));
                apiDriverOrdersRes.setDeliveryDate(AppFunction.convertUnixDateToStringDate(order.getDeliveryDate() !=null ? order.getDeliveryDate() : null));
                apiDriverOrdersRes.setDeliveryNote(order.getDeliveryNote() !=null ? order.getDeliveryNote() : "");


                ApiDriverOrdersRes.CustomerRes customerRes = new ApiDriverOrdersRes.CustomerRes();
                customerRes.setAddress(order.getDeliveryAddress());
                customerRes.setCustomerNames(order.getCustomerFirstName() + " " + order.getCustomerLastName());
                customerRes.setCustomerPhone(order.getCustomerPhoneNumber());

                //get delivery location
                customerRes.setDeliveryAddress(getOrderLocationAddress(order));

                apiDriverOrdersRes.setCustomerInfo(customerRes);


                List<DeliveryOrderProducts> deliveryOrderProducts=deliveryOrderProductsRepository.findAllByOrderId(order.getId());
                List<ApiDriverOrdersRes.ProductsRes> productsResList = new ArrayList<>();
                deliveryOrderProducts.forEach(orderDetail -> {
                    ApiDriverOrdersRes.ProductsRes productsRes = new ApiDriverOrdersRes.ProductsRes();

                    productsRes.setName(orderDetail.getProductName());
                    productsRes.setQuantity(Integer.valueOf(orderDetail.getQuantity()));

                    Vendors vendor = vendorsRepository.findFirstByCompanyIdAndPlatformId(orderDetail.getCompanyId(),order.getPlatformId());

                    if (null!=vendor) {
                        productsRes.setVendorName(vendor.getName());
                        //productsRes.setVendorCity(company.getCity());
                        productsRes.setVendorAddress(vendor.getName());
                        //productsRes.setVendorContact(company.getPhone());
                    }


                    productsResList.add(productsRes);

                });

                apiDriverOrdersRes.setProducts(productsResList);

                ordersRes.add(apiDriverOrdersRes);
            });
        } catch (Exception e) {

            logger.info("**************  an error occurred driver pending orders " + e.getMessage());

        }

        return new ResponseModel("00", "success", ordersRes);
    }


    public String convertDeliveryTimeToHumanTime(Long time){
        if (null==time)
            return null;
        if (time<12){
          return   time+"AM";
        }
        else if (time==12){
            return 12+" PM";
        }
        else {
           return  (time-12)+"PM";
        }
    }

    public Object orders(String status, HttpServletRequest request) {

        String draw = request.getParameter("draw");
        String searchValue = request.getParameter("search[value]");
        Integer length = Integer.valueOf(request.getParameter("length"));
        Integer start = Integer.valueOf(request.getParameter("start"));

        Session session = entityManager.unwrap(Session.class);

        StringBuilder queryLent = new StringBuilder();
        queryLent
                .append("SELECT ")
                .append(" do.id, do.order_no, pt.name, do.order_state, do.total, do.customer_first_name, do.customer_last_name, do.customer_phone_number,  ifnull(du.first_name, ''), ifnull(du.phone,''), " +
                        "DATE_FORMAT(do.creation_date, '%Y-%m-%d %H:%i:%s'),  DATE_FORMAT(dor.last_modified_date, '%Y-%m-%d %H:%i:%s') , rmax ")
                .append(" from orders as do ")
                .append(" left join users u on do.created_by_Id = u.ID ")
                .append("  left join ( select r.delivery_order_id,  max(r.id) as rmax from driver_orders_requests r group by r.delivery_order_id) al on do.id=al.delivery_order_id ")
                .append(" left join driver_orders_requests dor on dor.id=rmax ")
                .append(" left join users du on du.id = dor.driver_id ")
                .append(" left join platforms pt on pt.id = do.platform_id ");
        if (status.equalsIgnoreCase(DeliveryOrders.STATUS_PENDING)) {
            queryLent.append(" WHERE do.order_state = '" + DeliveryOrders.STATUS_PENDING + "' ");

        } else if (status.equalsIgnoreCase(DeliveryOrders.STATUS_COMPLETED)) {
            queryLent.append(" WHERE do.order_state = '" + DeliveryOrders.STATUS_COMPLETED + "' ");
        }

        int totalRecords;
        int filteredRecords;

        if (!searchValue.isEmpty()) {

            totalRecords = session.createSQLQuery(queryLent.toString() + " group by  do.order_no").list().size();

            if (queryLent.toString().contains("WHERE")) {
                queryLent.append(" and ");
            } else {
                queryLent.append(" WHERE ");
            }
            queryLent.append("  (do.order_no LIKE '%").append(searchValue)
                    .append("%' OR do.order_state LIKE '%").append(searchValue).append("%'")
                    .append(" OR  do.customer_first_name LIKE '%").append(searchValue).append("%'")
                    .append(" OR  do.customer_last_name LIKE '%").append(searchValue).append("%'")
                    .append(" OR  do.customer_phone_number LIKE '%").append(searchValue).append("%'")
                    .append(" OR  du.first_name LIKE '%").append(searchValue).append("%'")
                    .append(" OR  du.phone LIKE '%").append(searchValue).append("%')");

            filteredRecords = session.createSQLQuery(queryLent.toString()).list().size();
            queryLent.append(" group by  do.order_no")
                    .append(" order by do.id desc ");
        } else {

            queryLent.append(" group by  do.order_no")
                    .append(" order by do.id desc ");

            totalRecords = session.createSQLQuery(queryLent.toString()).list().size();

            filteredRecords = session.createSQLQuery(queryLent.toString()).list().size();
        }

        queryLent.append(" LIMIT :limit  OFFSET :page ");


        List orders = session.createSQLQuery(queryLent.toString())
                .setParameter("limit", length)
                //.setParameter("page", (start / length))
                .setParameter("page", start)
                .list();

        return new DataTableNew(draw, totalRecords, filteredRecords, orders);

    }




    public ResponseModel assignOrder(AssignOrderReq request) {
        JPQLQuery<?> query = new JPAQuery<>(entityManager);
        JPQLQuery<?> driverQuery = new JPAQuery<>(entityManager);
        JPQLQuery<?> pendingOrderQuery = new JPAQuery<>(entityManager);

        QDrivers qDrivers = QDrivers.drivers;

        QDeliveryOrders qDeliveryOrders = QDeliveryOrders.deliveryOrders;
        DeliveryOrders deliveryOrder = query.select(qDeliveryOrders).from(qDeliveryOrders).where(qDeliveryOrders.id.eq(request.getId())).fetchFirst();

        if (!deliveryOrder.getOrderState().equalsIgnoreCase(DeliveryOrders.STATUS_PENDING) && !deliveryOrder.getOrderState().equalsIgnoreCase(DeliveryOrders.STATUS_REJECTED))
            return new ResponseModel("01", "Sorry order already assigned");

        Drivers driver = query.select(qDrivers).from(qDrivers).where(qDrivers.id.eq(request.getDriver())).fetchFirst();

        if (!driver.getDriverState().equalsIgnoreCase(Drivers.STATE_AVAILABLE) &&
                !driver.getDriverState().equalsIgnoreCase(Drivers.STATUS_ASSIGNED) && !driver.getDriverState().equalsIgnoreCase(Drivers.STATUS_ENROUTE))
            return new ResponseModel("01", "Sorry order is not accepting orders.");

        if (!driver.getStatus().equalsIgnoreCase(AppConstant.STATUS_ACTIVE_RECORD) ||
                !driver.getUserLink().getStatus().equalsIgnoreCase(AppConstant.STATUS_ACTIVE_RECORD))//driver not active
            return new ResponseModel("01", "Sorry driver not active.");

        //check max no of orders that can be assigned to a rider

        QDeliveryDriverMotorVehicles qDeliveryDriverMotorVehicles = QDeliveryDriverMotorVehicles.deliveryDriverMotorVehicles;

        DeliveryDriverMotorVehicles driverMotorVehicles = driverQuery.select(qDeliveryDriverMotorVehicles).from(qDeliveryDriverMotorVehicles)
                .where(qDeliveryDriverMotorVehicles.status.eq(AppConstant.STATUS_ACTIVE_RECORD)
                        .and(qDeliveryDriverMotorVehicles.driverId.eq(driver.getId())))
                .fetchFirst();

        if (null == driverMotorVehicles)
            return new ResponseModel("01", "Sorry driver has not been assigned to any motorbike.");

        //check for max number of orders
        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

        long pendingRiderOrders = pendingOrderQuery.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                .where(qDriverOrdersRequests.driverId.eq(driver.getUserId())
                        .and(qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_REJECTED)).and(qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_COMPLETED)))
                .fetchCount();

        AppSettings appSettings = appSettingsService.getAppSettings(AppSettings.APP_SETTINGS_MAX_ORDER_CAN_BE_ASSIGNED);

        if (null != appSettings && pendingRiderOrders >= Integer.valueOf(appSettings.getValue()))
            return new ResponseModel("01", "Sorry! driver have reached maximum of orders that can be assigned.");

        driver.setDriverState(Drivers.STATUS_ASSIGNED);
        driversRepository.save(driver);

        deliveryOrder.setOrderState(DeliveryOrders.STATUS_ASSIGNED);
        deliveryOrdersRepository.save(deliveryOrder);

        Users user = driver.getUserLink();

        DriverOrdersRequests driverOrdersRequests = new DriverOrdersRequests();
        driverOrdersRequests.setCreatedById(user.getId());
        driverOrdersRequests.setDriverId(user.getId());
        driverOrdersRequests.setOrderNo(deliveryOrder.getOrderNo());
        driverOrdersRequests.setCreationDate(new Date());
        driverOrdersRequests.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
        driverOrdersRequests.setDeliveryOrderId(deliveryOrder.getId());
        driverOrdersRequests.setState(DeliveryOrders.STATUS_ASSIGNED);

        driverOrdersRequestsRepository.save(driverOrdersRequests);

        logger.info("************ user driver " + user.getEmail());
        if (null != user.getFireBaseToken() && !user.getFireBaseToken().isEmpty())
            notificationService.sendFireBaseNotification(user.getFireBaseToken(), "NEW ORDER", "New order assigned.", "NEW_ORDER");


        return new ResponseModel("00", "Order assigned successfully");
    }

    /**
     * driver responding to assigned order accept of reject
     */
    public ResponseModel respondToOrder(ApiAcceptOrdersReq req) {
        ResponseModel response = new ResponseModel();

        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);

        DriverOrdersRequests driverOrdersRequest = query.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                .where(qDriverOrdersRequests.id.eq(req.getRequestId()))
                .where(qDriverOrdersRequests.status.eq(AppConstant.STATUS_ACTIVE_RECORD)).fetchFirst();

        if (null == driverOrdersRequest)
            return new ResponseModel("01", "Sorry order request not available");

        if (!driverOrdersRequest.getState().equalsIgnoreCase(DeliveryOrders.STATUS_ASSIGNED))
            return new ResponseModel("01", "Sorry cannot update order");

        Drivers driver = driversRepository.findFirstByUserId(user.getId());

        DeliveryOrders deliveryOrder = driverOrdersRequest.getDeliveryOrders();

        response.setStatus("00");
        if (req.getAccept()) {
            driverOrdersRequest.setState(DeliveryOrders.STATUS_ACCEPTED);
            driver.setDriverState(Drivers.STATUS_ENROUTE);
            deliveryOrder.setOrderState(DeliveryOrders.STATUS_ACCEPTED);
            response.setMessage("Order accepted.");
        } else {

            if (null == req.getReason() || req.getReason().isEmpty())
                return new ResponseModel("01", "Please provide reason for rejecting order.");

            driverOrdersRequest.setState(DeliveryOrders.STATUS_REJECTED);
            driverOrdersRequest.setRejectReason(req.getReason());

            deliveryOrder.setOrderState(DeliveryOrders.STATUS_REJECTED);
            driver.setDriverState(Drivers.STATE_AVAILABLE);
            response.setMessage("Order declined successfully");
        }
        driversRepository.save(driver);
        driverOrdersRequestsRepository.save(driverOrdersRequest);
        deliveryOrdersRepository.save(deliveryOrder);
        return response;
    }

    /**
     * driver order collected from vendor or store
     */
    public ResponseModel orderCollected(ApiOrderStateReq req) {
        ResponseModel response = new ResponseModel();

        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);

        DriverOrdersRequests driverOrdersRequest = query.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                .where(qDriverOrdersRequests.id.eq(req.getRequestId()))
                .where(qDriverOrdersRequests.status.eq(AppConstant.STATUS_ACTIVE_RECORD)).fetchFirst();

        if (null == driverOrdersRequest)
            return new ResponseModel("01", "Sorry order request not available");

        if (!driverOrdersRequest.getState().equalsIgnoreCase(DeliveryOrders.STATUS_ACCEPTED))
            return new ResponseModel("01", "Sorry cannot update order");

        DeliveryOrders deliveryOrder = driverOrdersRequest.getDeliveryOrders();

        Optional<DeliveryPlatforms> deliveryPlatforms=deliveryPlatformRepository.findById(deliveryOrder.getPlatformId());
        if (!deliveryPlatforms.isPresent())
            return new ResponseModel("01","Sorry! Something wrong happened to this order.");

        Drivers driver = driversRepository.findFirstByUserId(user.getId());
        driver.setDriverState(Drivers.STATUS_ENGAGED);
        driverOrdersRequest.setState(DeliveryOrders.STATUS_INTRANSIT);

        deliveryOrder.setOrderState(DeliveryOrders.STATUS_INTRANSIT);

        driversRepository.save(driver);
        driverOrdersRequestsRepository.save(driverOrdersRequest);
        deliveryOrdersRepository.save(deliveryOrder);

        //TO DO UPDATE ORDER STATUS IN TRANSIT VIA BACKEND
        UpdateOrderReq updateOrderReq = new UpdateOrderReq();
        updateOrderReq.setStatus(DeliveryOrders.ORDER_STATUS_INTRANSIT);
        updateOrderReq.setNotifyDepartment(true);
        updateOrderReq.setNotifyUser(true);
        updateOrderReq.setNotifyVendor(false);



        String json = objectToString(updateOrderReq);
        String url=deliveryPlatforms.get().getUrl();

        apiHttpRequest(json, url + "/api/orders/" + deliveryOrder.getOrderNo(), deliveryPlatforms.get());

        return new ResponseModel("00", "Order in transit");

    }

    /**
     * driver delivered to the customer.
     */
    public ResponseModel orderCompleted(ApiOrderStateReq req) {
        ResponseModel response = new ResponseModel();

        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);
        JPQLQuery<?> pendingOrdersQuery = new JPAQuery<>(entityManager);

        DriverOrdersRequests driverOrdersRequest = query.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                .where(qDriverOrdersRequests.id.eq(req.getRequestId()))
                .where(qDriverOrdersRequests.status.eq(AppConstant.STATUS_ACTIVE_RECORD)).fetchFirst();

        if (null == driverOrdersRequest)
            return new ResponseModel("01", "Sorry order request not available");

        if (!driverOrdersRequest.getState().equalsIgnoreCase(DeliveryOrders.STATUS_INTRANSIT))
            return new ResponseModel("01", "Sorry cannot update order");

        DeliveryOrders deliveryOrder = driverOrdersRequest.getDeliveryOrders();

        Drivers driver = driversRepository.findFirstByUserId(user.getId());

        QDriverOrdersRequests qDriverOrdersPendingRequests = QDriverOrdersRequests.driverOrdersRequests;

        long pendingOrders = pendingOrdersQuery.select(qDriverOrdersPendingRequests).from(qDriverOrdersPendingRequests)
                .where((qDriverOrdersPendingRequests.driverId.eq(user.getId())
                        .and(qDriverOrdersPendingRequests.status.eq(AppConstant.STATUS_ACTIVE_RECORD)))
                        .and((qDriverOrdersPendingRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_COMPLETED))
                                .and(qDriverOrdersPendingRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_REJECTED))))
                .fetchCount();


        logger.info("************* driver  pending order " + pendingOrders);

        //check if rider has other pending orders
        //if (pendingOrders <= 0)

        Optional<DeliveryPlatforms> deliveryPlatforms=deliveryPlatformRepository.findById(deliveryOrder.getPlatformId());
        if (!deliveryPlatforms.isPresent())
            return new ResponseModel("01","Sorry! Something wrong happened to this order.");

        driver.setDriverState(Drivers.STATE_AVAILABLE);

        driverOrdersRequest.setState(DeliveryOrders.STATUS_COMPLETED);
        deliveryOrder.setOrderState(DeliveryOrders.STATUS_COMPLETED);

        driversRepository.save(driver);
        driverOrdersRequestsRepository.save(driverOrdersRequest);
        deliveryOrdersRepository.save(deliveryOrder);

        //TO DO UPDATE ORDER STATUS IN COMPLETED VIA BACKEND
        UpdateOrderReq updateOrderReq = new UpdateOrderReq();
        updateOrderReq.setStatus(DeliveryOrders.ORDER_STATUS_COMPLETED);
        updateOrderReq.setNotifyDepartment(true);
        updateOrderReq.setNotifyUser(true);
        updateOrderReq.setNotifyVendor(false);

        String json = objectToString(updateOrderReq);
        String url=deliveryPlatforms.get().getUrl();
        //make http request to make order as completed
        apiHttpRequest(json, url + "/api/orders/" + deliveryOrder.getOrderNo(), deliveryPlatforms.get());
        return new ResponseModel("00", "Order completed");
    }


    /**
     * get order details
     * */
    public ResponseModel getOrderDetails(Long orderId){

        Optional<DeliveryOrders> deliveryOrders=deliveryOrdersRepository.findById(orderId);
        if (!deliveryOrders.isPresent())
            return new ResponseModel("01","Order not found");

        DeliveryOrders order=deliveryOrders.get();

       // Orders order = node.getOrders();
        ApiDriverOrdersRes orderRes = new ApiDriverOrdersRes();
        orderRes.setOrderNo(order.getOrderNo());
        orderRes.setOrderState(order.getOrderState());
        orderRes.setTotal(order.getTotal());
        orderRes.setOrderDate(order.getCreationDate().getTime());
        orderRes.setRequestId(order.getId());

        orderRes.setDeliveryTimeFrom(convertDeliveryTimeToHumanTime(order.getDeliveryTimeFrom()));
        orderRes.setDeliveryTimeTo(convertDeliveryTimeToHumanTime(order.getDeliveryTimeTo()));
        orderRes.setDeliveryDate(AppFunction.convertUnixDateToStringDate(order.getDeliveryDate()  !=null ? order.getDeliveryDate()  : 0));
        orderRes.setDeliveryNote(order.getDeliveryNote());

        ApiDriverOrdersRes.CustomerRes customerRes = new ApiDriverOrdersRes.CustomerRes();
        customerRes.setAddress(order.getDeliveryAddress());
        customerRes.setCustomerNames(order.getCustomerFirstName() + " " + order.getCustomerLastName());
        customerRes.setCustomerPhone(order.getCustomerPhoneNumber());

        //get delivery location
        customerRes.setDeliveryAddress(getOrderLocationAddress(order));

        orderRes.setCustomerInfo(customerRes);

        if (order.getOrderState().equalsIgnoreCase(DeliveryOrders.STATUS_COMPLETED))
            orderRes.setDeliveredOn(order.getLastModifiedDate());


        List<DeliveryOrderProducts> deliveryOrderProducts=deliveryOrderProductsRepository.findAllByOrderId(order.getId());
        List<ApiDriverOrdersRes.ProductsRes> productsResList = new ArrayList<>();
        deliveryOrderProducts.forEach(orderDetail -> {
            ApiDriverOrdersRes.ProductsRes productsRes = new ApiDriverOrdersRes.ProductsRes();

            productsRes.setName(orderDetail.getProductName());
            productsRes.setQuantity(Integer.valueOf(orderDetail.getQuantity()));

                 /*   Companies company = orderDetail.getProduct().getCompaniesLink();

                    productsRes.setVendorName(company.getCompany());
                    productsRes.setVendorCity(company.getCity());
                    productsRes.setVendorAddress(company.getAddress());
                    productsRes.setVendorContact(company.getPhone());*/
                 //
            Vendors vendor = vendorsRepository.findFirstByCompanyIdAndPlatformId(orderDetail.getCompanyId(),order.getPlatformId());

            if (null!=vendor) {
                productsRes.setVendorName(vendor.getName());
                //productsRes.setVendorCity(company.getCity());
                productsRes.setVendorAddress(vendor.getName());
                //productsRes.setVendorContact(company.getPhone());
            }

            productsResList.add(productsRes);

        });

        orderRes.setProducts(productsResList);


        return new ResponseModel("","00","success",orderRes);
    }

    /**
     * get order delivery location address
     *
     * @return void
     */
    public ApiDriverOrdersRes.DeliveryLocRes getOrderLocationAddress(DeliveryOrders order) {
        ApiDriverOrdersRes.DeliveryLocRes locRes = new ApiDriverOrdersRes.DeliveryLocRes();
        if (null != order.getDeliveryLocation() && !order.getDeliveryLocation().isEmpty()) {

            try {
                JSONObject jsonObj = new JSONObject(order.getDeliveryLocation());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                OrderDeliveryLocationInfo orderDeliveryLocationInfo;
                orderDeliveryLocationInfo = objectMapper.readValue(jsonObj.toString(), OrderDeliveryLocationInfo.class);
                locRes.setLat(orderDeliveryLocationInfo.getGeometry().getLocation().getLat());
                locRes.setLng(orderDeliveryLocationInfo.getGeometry().getLocation().getLng());
            } catch (JSONException | JsonProcessingException e) {
                e.printStackTrace();
                logger.info("****** an error occurred while trying to get order delivery location " + e.getMessage());
            }

        }
        return locRes;
    }


    /**
     * make http request to cs-cart backend
     */
    private ResponseModel apiHttpRequest(String json, String url, DeliveryPlatforms deliveryPlatforms) {
        ResponseModel responseModel=new ResponseModel();
        UpdateOrderRes updateOrderRes;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //String auth = applicationPropertiesValues.swiggoBackendUsername + ":" + applicationPropertiesValues.swiggoBackendAccessKey;
        String auth = deliveryPlatforms.getApiUserName() + ":" + deliveryPlatforms.getApiKey();

        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);

        //disabled to update  order details to cscart api

        try {
            final ResponseEntity<UpdateOrderRes> balanceResponse = httpCall.sendAPIRequest(url, json, httpHeaders, UpdateOrderRes.class);
            updateOrderRes = balanceResponse.getBody();

            assert updateOrderRes != null;
            logger.info("================== api response : " + updateOrderRes.toString());

            responseModel = new ResponseModel();
            responseModel.setStatus("00");
            responseModel.setMessage("Order updated");

            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = new ResponseModel();
            responseModel.setStatus("01");
            responseModel.setMessage("Sorry an error occurred while processing your request please try again later.");
            logger.info("********** exception occurred " + e.getMessage());
        }
        return responseModel;
    }


}
