package com.patrick.delivery.services;

import com.patrick.delivery.entities.*;
import com.patrick.delivery.models.*;
import com.patrick.delivery.repositories.api.DeliveryDriverMotorVehiclesRepo;
import com.patrick.delivery.repositories.api.DriversRepository;
import com.patrick.delivery.repositories.api.UserTypeRepository;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.utils.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.patrick.delivery.configs.AppSettingsService;
import com.patrick.delivery.locationEntities.Location;
import com.patrick.delivery.locationEntities.RiderLocations;
import com.patrick.delivery.properties.ApplicationPropertiesValues;
import com.patrick.delivery.repositories.locationRepos.RiderLocationRepository;
import com.patrick.delivery.security.service.CustomApiUserDetailsService;
import com.patrick.delivery.security.web.SecurityUtils;
import com.patrick.delivery.utils.interfaces.DataTableNewInterface;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class DriverService extends GeneralLogger {

    @Autowired
    private CustomApiUserDetailsService customApiUserDetailsService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @PersistenceContext(unitName = "delivery")
    private EntityManager entityManager;
    @Autowired
    private DataTableNewInterface dataTable;
    @Autowired
    private DriversRepository driversRepository;
    @Autowired
    private ApplicationPropertiesValues apv;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private DeliveryDriverMotorVehiclesRepo deliveryDriverMotorVehiclesRepo;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RiderLocationRepository riderLocationRepository;
    @Autowired
    private AppSettingsService appSettingsService;
    /**
     * change driver password
     *
     * @return ResponseModel
     */
    public ResponseModel changePassword(ApiChangePasswordReq request) {
        Users user = customApiUserDetailsService.getAuthicatedUser();
        if (request.getNewPassword().equals(request.getOldPassword()))
            return new ResponseModel("01", ApplicationMessages.get("response.reset.password.same"));

        if (!SystemComponent.passwordMatches(request.getOldPassword(), user))
            return new ResponseModel("01", "Sorry old password doesn't match our records.");

        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);


        return new ResponseModel("00", ApplicationMessages.get("response.change.password"));
    }

    /**
     * get driver info
     *
     * @return ResponseModel
     */
    public ResponseModel driverInfo() {
        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);
        JPQLQuery<?> driverQuery = new JPAQuery<>(entityManager);
        JPQLQuery<?> pendingOrdersQuery = new JPAQuery<>(entityManager);

        QDrivers qDrivers = QDrivers.drivers;

        Drivers driver = query.select(qDrivers).from(qDrivers).where(qDrivers.userId.eq(user.getId()))
                .fetchFirst();

        QDeliveryDriverMotorVehicles qDeliveryDriverMotorVehicles = QDeliveryDriverMotorVehicles.deliveryDriverMotorVehicles;

        DeliveryDriverMotorVehicles driverMotorVehicles = driverQuery.select(qDeliveryDriverMotorVehicles)
                .from(qDeliveryDriverMotorVehicles)
                .where(qDeliveryDriverMotorVehicles.status.eq(AppConstant.STATUS_ACTIVE_RECORD)
                        .and(qDeliveryDriverMotorVehicles.driverId.eq(driver.getId())))
                .fetchFirst();

        ApiUserDetailsRes apiUserDetailsRes = new ApiUserDetailsRes();
        apiUserDetailsRes.setEmail(user.getEmail());
        apiUserDetailsRes.setFirstName(user.getFirstName());
        apiUserDetailsRes.setLastName(user.getLastName());
        apiUserDetailsRes.setPhone(user.getPhone());
        apiUserDetailsRes.setState(driver.getDriverState());

        if (null != driverMotorVehicles)
            apiUserDetailsRes.setMotorRegNo(driverMotorVehicles.getDeliveryMotorVehicleAssigned().getRegNo());

        QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

        long pendingOrders = pendingOrdersQuery.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                .where((qDriverOrdersRequests.driverId.eq(user.getId()).and(qDriverOrdersRequests.status.eq(AppConstant.STATUS_ACTIVE_RECORD)))
                        .and((qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_COMPLETED))
                                .and(qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_REJECTED))))
                .fetchCount();

        apiUserDetailsRes.setPendingOrdersCount(pendingOrders);

        if (pendingOrders > 0)
            apiUserDetailsRes.setPendingOrders(true);

        Map<String, Object> map = new HashMap<>();
        map.put("driverInfo", apiUserDetailsRes);
        return new ResponseModel("00", "success", map);
    }


    /**
     * get riders/ drivers
     *
     * @return Object
     */
    public Object riders(String status, HttpServletRequest request) {

        String draw = request.getParameter("draw");
        String searchValue = request.getParameter("search[value]");
        Integer length = Integer.valueOf(request.getParameter("length"));
        Integer start = Integer.valueOf(request.getParameter("start"));

        Session session = entityManager.unwrap(Session.class);

        StringBuilder queryLent = new StringBuilder();
        queryLent
                .append("SELECT ")
                .append(" d.id,  u.first_name, u.last_name, u.phone, u.email, d.driver_state, v.reg_no, DATE_FORMAT(d.creation_date,  '%Y-%m-%d %H:%i:%s'), rmax ")
                .append(" from drivers  as d ")
                .append(" left join users u on d.user_id = u.ID ")
                .append("  left join ( select r.driver_id,  max(r.id) as rmax from driver_motor_vehicles r where status  = '" + AppConstant.STATUS_ACTIVE_RECORD + "' ")
                .append( " group by r.driver_id ) al on d.id=al.driver_id ")
                .append(" left join driver_motor_vehicles dh on dh.id = rmax ")
                .append("  left join motor_vehicles v on v.id = dh.vehicle_id ");

        if (status.equalsIgnoreCase(Drivers.STATE_AVAILABLE)) {
            queryLent.append(" WHERE d.driver_state = '" + Drivers.STATE_AVAILABLE + "' ");

        }

        logger.info("************** query "+queryLent.toString());
        int totalRecords;
        int filteredRecords;

        if (!searchValue.isEmpty()) {

            totalRecords = session.createSQLQuery(queryLent.toString() + " group by  d.id  ").list().size();

            if (queryLent.toString().contains("WHERE")) {
                queryLent.append(" and ");
            } else {
                queryLent.append(" WHERE ");
            }
            queryLent.append("  (u.first_name LIKE '%").append(searchValue)
                    .append("%' OR u.last_name LIKE '%").append(searchValue).append("%'")
                    .append(" OR  u.phone LIKE '%").append(searchValue).append("%'")
                    .append(" OR  u.email LIKE '%").append(searchValue).append("%'")
                    .append(" OR  d.driver_state LIKE '%").append(searchValue).append("%'")
                    .append(" OR  v.reg_no LIKE '%").append(searchValue).append("%')");

            filteredRecords = session.createSQLQuery(queryLent.toString()).list().size();
            queryLent.append(" group by  d.id  ")
                    .append(" order by d.id desc ");
        } else {

            queryLent.append(" group by  d.id  ")
                    .append(" order by d.id desc ");

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

    public Object riders1(String status) {

        dataTable.select(" d.id,  u.first_name, u.last_name, u.phone, u.email, d.driver_state, v.reg_no, DATE_FORMAT(d.creation_date; '%Y-%m-%d %H:%i:%s')")
                .join("users u on d.user_id = u.ID")
                .join("driver_motor_vehicles dh on dh.driver_id = d.id")
                .join("motor_vehicles v on v.id = dh.vehicle_id")
                .from("drivers as d")
                .orderBy("d.id desc")
                .groupBy("d.id")
                .nativeSQL(true);

        if (status.equalsIgnoreCase(Drivers.STATE_AVAILABLE)) {
            dataTable.where("d.driver_state =:state");
            dataTable.setParameter("state", Drivers.STATE_AVAILABLE);
        }
        return dataTable.showTable();
    }



    /**
     * change available drivers
     *
     * @return ResponseModel
     */
    public ResponseModel availableDrivers() {
        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);

        QDrivers qDrivers = QDrivers.drivers;
        QUsers qUsers = QUsers.users;


        int size=10;
        int page=0;
        Pageable pageable = PageRequest.of(page, size);

        Double lngLoc=apv.locationPinLng;
        Double latLoc=apv.locationPinLat;
        Integer distance=apv.locationCircleRadius;

        Page<RiderLocations> riderLocation =
                riderLocationRepository.findByLocNear(new Point(lngLoc,latLoc ), new Distance(distance, Metrics.KILOMETERS),pageable);

        List<Long> availableDriversId=  riderLocation.getContent().stream() .map(RiderLocations::getDriverId)
                .collect(Collectors.toList());

        List<Drivers> drivers = query.select(qDrivers).from(qDrivers)
                .innerJoin(qDrivers.userLink, qUsers)
               // .where(qDrivers.id.in(availableDriversId))
                .where(qDrivers.driverState.eq(Drivers.STATE_AVAILABLE)
                        .or(qDrivers.driverState.eq(Drivers.STATUS_ASSIGNED))
                        .or(qDrivers.driverState.eq(Drivers.STATUS_ENROUTE)).and(qUsers.status.eq(AppConstant.STATUS_ACTIVE_RECORD)))
                .fetch();


        return new ResponseModel("00", "success", drivers);
    }



    /**
     * update driver state
     *
     * @return ResponseModel
     */
    public ResponseModel updateDriverState(ApiDriverStatusReq req) {
        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);
        JPQLQuery<?> driverQuery = new JPAQuery<>(entityManager);
        JPQLQuery<?> pendingOrdersQuery = new JPAQuery<>(entityManager);

        QDrivers qDrivers = QDrivers.drivers;

        QDeliveryDriverMotorVehicles qDeliveryDriverMotorVehicles = QDeliveryDriverMotorVehicles.deliveryDriverMotorVehicles;

        Drivers driver = query.select(qDrivers).from(qDrivers).where(qDrivers.userId.eq(user.getId()))
                .fetchFirst();

        DeliveryDriverMotorVehicles driverMotorVehicles = driverQuery.select(qDeliveryDriverMotorVehicles)
                .from(qDeliveryDriverMotorVehicles)
                .where(qDeliveryDriverMotorVehicles.status.eq(AppConstant.STATUS_ACTIVE_RECORD)
                        .and(qDeliveryDriverMotorVehicles.driverId.eq(driver.getId())))
                .fetchFirst();

        ResponseModel response = new ResponseModel();
        if (req.getState().equalsIgnoreCase(Drivers.STATE_OFF)) {

            //if driver
            if (!driver.getDriverState().equalsIgnoreCase(Drivers.STATE_AVAILABLE))
                return new ResponseModel("01", "Cannot set status to offline. Complete assigned orders first");

            QDriverOrdersRequests qDriverOrdersRequests = QDriverOrdersRequests.driverOrdersRequests;

            long pendingOrders = pendingOrdersQuery.select(qDriverOrdersRequests).from(qDriverOrdersRequests)
                    .where((qDriverOrdersRequests.driverId.eq(user.getId())
                            .and(qDriverOrdersRequests.status.eq(AppConstant.STATUS_ACTIVE_RECORD)))
                            .and((qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_COMPLETED))
                                    .and(qDriverOrdersRequests.state.notEqualsIgnoreCase(DeliveryOrders.STATUS_REJECTED))))
                    .fetchCount();

            //check if rider has other pending orders
            if (pendingOrders > 0)
                return new ResponseModel("01", ApplicationMessages.get("response.change.driver.state.with.orders"));

            //check if has incomplete orders
            driver.setDriverState(Drivers.STATE_OFF);

            response.setMessage("Your are now offline");
        } else if (req.getState().equalsIgnoreCase(Drivers.STATE_AVAILABLE)) {

            //check if driver is off
            if (!driver.getDriverState().equalsIgnoreCase(Drivers.STATE_OFF))
                return new ResponseModel("01", "Sorry you cannot change your status. Your are already online.");

            //check has also
            if (null == driverMotorVehicles)
                return new ResponseModel("01", "Sorry you are not assigned yo any motor bike. Contact your supervisor");

            driver.setDriverState(Drivers.STATE_AVAILABLE);

            response.setMessage("Your are now online.");
        } else {
            return new ResponseModel("01", "Sorry invalid driver state");
        }

        driversRepository.save(driver);

        response.setStatus("00");
        return response;
    }

    /**
     * get driver details
     */
    public ResponseModel driverDetails(Long driverId) {
        Optional<Drivers> checkdriver = driversRepository.findById(driverId);
        if (!checkdriver.isPresent())
            return new ResponseModel("01", "Sorry! Not driver found");

        Drivers driver=checkdriver.get();

        Users  user=driver.getUserLink();
        DriverRes driverRes=new DriverRes();
        driverRes.setId(driverId);
        driverRes.setPhone(user.getPhone());
        driverRes.setEmail(user.getEmail());
        driverRes.setFirstName(user.getFirstName());
        driverRes.setLastName(user.getLastName());
        driverRes.setStatus(driver.getStatus());
        driverRes.setDriverState(driver.getDriverState());

        DeliveryDriverMotorVehicles driverMotor=deliveryDriverMotorVehiclesRepo.findFirstByDriverIdAndStatus(driverId,AppConstant.STATUS_ACTIVE_RECORD);
        if (null!=driverMotor)
            driverRes.setVehicleId(driverId);
        else
            driverRes.setVehicleId(driverId);

        return new ResponseModel("00", "success", driverRes);
    }

    /**
     * update driver details
     *
     * @return ResponseModel
     */
    public ResponseModel updateDriverDetails(UpdateDriverReq req) {

        Optional<Drivers> driver = driversRepository.findById(req.getId());
        if (!driver.isPresent())
            return new ResponseModel("01", "Sorry! Not driver found");

        Users user = driver.get().getUserLink();

        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setStatus(req.getStatus());
        usersRepository.save(user);

        DeliveryDriverMotorVehicles driverMotor=deliveryDriverMotorVehiclesRepo.findFirstByDriverIdAndStatus(req.getId(),AppConstant.STATUS_ACTIVE_RECORD);

        if (req.getMotorBike().equals(0L) && null!=driverMotor)
        {
            //deactivare
            driverMotor.setStatus(AppConstant.STATUS_INACTIVE_RECORD);
            deliveryDriverMotorVehiclesRepo.save(driverMotor);
        } else  if (null!=driverMotor && !driverMotor.getVehicleId().equals(req.getMotorBike())){
            //assigned another motor

            //deactivate
            driverMotor.setStatus(AppConstant.STATUS_INACTIVE_RECORD);
            deliveryDriverMotorVehiclesRepo.save(driverMotor);

            DeliveryDriverMotorVehicles newAssigned=new DeliveryDriverMotorVehicles();
            newAssigned.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
            newAssigned.setDriverId(req.getId());
            newAssigned.setVehicleId(req.getMotorBike());

            deliveryDriverMotorVehiclesRepo.save(newAssigned);

        }else if (null==driverMotor && !req.getMotorBike().equals(0L)){
            //assigned motor bike

            DeliveryDriverMotorVehicles newAssigned=new DeliveryDriverMotorVehicles();
            newAssigned.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
            newAssigned.setDriverId(req.getId());
            newAssigned.setVehicleId(req.getMotorBike());

            deliveryDriverMotorVehiclesRepo.save(newAssigned);
        }
        return new ResponseModel("00", "Driver details updated successfully");

    }


    /**
     * driver get assigned orders
     */
    public Object driverOrders(Long driverId) {

        Optional<Drivers> driver = driversRepository.findById(driverId);
        if (!driver.isPresent())
            return new ResponseModel("01", "Sorry! Not driver found");

        Users user = driver.get().getUserLink();

        dataTable.select(" do.id, do.order_no, do.order_state, do.customer_first_name, do.customer_last_name, do.customer_phone_number,  do.delivery_address, " +
                "DATE_FORMAT(do.creation_date; '%Y-%m-%d %H:%i:%s'),  DATE_FORMAT(dor.last_modified_date; '%Y-%m-%d %H:%i:%s')")
                .join("orders do on do.id = dor.delivery_order_id ")
                .from("driver_orders_requests as dor")
                .orderBy("dor.id desc")
                .nativeSQL(true);

        dataTable.where("dor.driver_id =:driverId");
        dataTable.setParameter("driverId", user.getId());

        return dataTable.showTable();
    }


    /**
     * new user
     */
    public ResponseModel newRider(Users request) {
        UserTypes userType = userTypeRepository.findFirstByCode(UserTypes.RIDER);

        Users authUser = usersRepository.findFirstByEmail(SecurityUtils.getCurrentUserLogin());

        Optional<Users> checkUser = usersRepository.findDistinctByEmail(request.getEmail());
        if (checkUser.isPresent())
            return new ResponseModel("01", "User exist with similar email address.");

        if (!AppFunction.validatePhoneNumber(request.getPhone()))
            return new ResponseModel("01", "Sorry supplied phone number is invalid.");

        request.setPhone(AppFunction.getInternationalPhoneNumber(request.getPhone(), ""));

        Users checkUserPhone = usersRepository.findByPhoneAndUserTypeNo(request.getPhone(), userType.getId());
        if (null != checkUserPhone)
            return new ResponseModel("01", "User exist with similar phone number.");

        String code=AppFunction.randomCodeNumber(4);

        Users user = new Users();
        user.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUserTypeNo(userType.getId());
        user.setFlag("1");
        user.setCreatedById(authUser.getId());
        user.setPassword(bCryptPasswordEncoder.encode(code));
        usersRepository.save(user);

        //
        Drivers driver=new Drivers();
        driver.setDriverState(Drivers.STATE_OFF);
        driver.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
        driver.setUserId(user.getId());
        driver.setCreatedById(authUser.getId());
        driversRepository.save(driver);

        notificationService.sendSMSInfoBib(user.getPhone(),String.format(ApplicationMessages.get("sms.rider.created"),user.getFirstName(), code));

        //notify user send email
      /*  boolean sent = sendGridMailService.sendMail(sendGridMailService.sendGridConfig()
                .setTo(user.getEmail())
                .setTemplateId(MailOptions.Templates.PASSWORD_RESET_TEMPLATE.template)
                .setSubject("Set Up Account")
                .addAttribute("__name", user.getLastName())
                .addAttribute("__baseUrl", apv.appEndPoint + "/set-password/")

        );*/

        return new ResponseModel("00", "Rider created successfully.");
    }

    /**
     * get driver details
     */
    public ResponseModel availableMotorBikes(Long driverId) {
        Optional<Drivers> driver = driversRepository.findById(driverId);

        JPQLQuery<?> query = new JPAQuery<>(entityManager);
        JPQLQuery<?> vehiclesQuery = new JPAQuery<>(entityManager);

        QDeliveryDriverMotorVehicles qDeliveryDriverMotorVehicles=QDeliveryDriverMotorVehicles.deliveryDriverMotorVehicles;

        List<Long> driverMotorVehicles = query.select(qDeliveryDriverMotorVehicles.vehicleId)
                .from(qDeliveryDriverMotorVehicles)
                .where(qDeliveryDriverMotorVehicles.status.eq(AppConstant.STATUS_ACTIVE_RECORD).and(qDeliveryDriverMotorVehicles.driverId.eq(driverId).not()))
                .groupBy(qDeliveryDriverMotorVehicles.vehicleId)
                .fetch();

        QDeliveryMotorVehicles qDeliveryMotorVehicles=QDeliveryMotorVehicles.deliveryMotorVehicles;


        List<DeliveryMotorVehicles> deliveryMotorVehicles=vehiclesQuery.select(qDeliveryMotorVehicles)
                .from(qDeliveryMotorVehicles)
                .where(qDeliveryMotorVehicles.id.notIn(driverMotorVehicles))
                .groupBy(qDeliveryMotorVehicles.id)
                .fetch();

      /*  DeliveryDriverMotorVehicles driverMotor=deliveryDriverMotorVehiclesRepo.findFirstByDriverIdAndStatus(driverId,AppConstant.STATUS_ACTIVE_RECORD);
        if (null!=driverMotor)
            deliveryMotorVehicles.add(driverMotor.getDeliveryMotorVehicleAssigned());
*/
        return new ResponseModel("00", "success", deliveryMotorVehicles);
    }

    /**
     * update driver state
     *
     * @return ResponseModel
     */
    public ResponseModel updateDriverLocation(ApiDriverLocationReq req) {
        Users user = customApiUserDetailsService.getAuthicatedUser();

        JPQLQuery<?> query = new JPAQuery<>(entityManager);

        QDrivers qDrivers = QDrivers.drivers;

        Drivers driver = query.select(qDrivers).from(qDrivers).where(qDrivers.userId.eq(user.getId()))
                .fetchFirst();

        if (driver.getDriverState().equalsIgnoreCase(Drivers.STATE_OFF))
            return new ResponseModel("01","Sorry you are offline");
        //check for existing rider loc and set last location

        RiderLocations riderLocation = riderLocationRepository.findTopByDriverId(driver.getId());
        if (null == riderLocation)
            riderLocation = new RiderLocations();

        riderLocation.setDriverId(driver.getId());
        riderLocation.setLat(req.getLat());
        riderLocation.setLng(req.getLng());
        riderLocation.setUpdatedOn(new Date());
        Location location = new Location(riderLocation.getLat(), riderLocation.getLng());

        riderLocation.setLoc(location);
        riderLocation.setOrderNo(req.getOrderNo());
        riderLocationRepository.save(riderLocation);

        return new ResponseModel("00", "Success");

    }
}
