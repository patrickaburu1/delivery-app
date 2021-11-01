package com.patrick.delivery.services;

import com.patrick.delivery.entities.UserGroups;
import com.patrick.delivery.entities.UserTypes;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.properties.ApplicationPropertiesValues;
import com.patrick.delivery.repositories.api.UserGroupsRepository;
import com.patrick.delivery.repositories.api.UserTypeRepository;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.security.web.SecurityUtils;
import com.patrick.delivery.utils.AppConstant;
import com.patrick.delivery.utils.AppFunction;
import com.patrick.delivery.utils.GeneralLogger;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.utils.interfaces.DataTableNewInterface;
import com.patrick.delivery.utils.mails.MailOptions;
import com.patrick.delivery.utils.mails.MailerServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author patrick on 7/22/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class UsersService extends GeneralLogger {

    @Autowired
    private DataTableNewInterface dataTable;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserGroupsRepository userGroupsRepository;
    @Autowired
    private MailerServiceInterface sendGridMailService;
    @Autowired
    private ApplicationPropertiesValues apv;

    public Map<String, Object> usersDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("userGroups", userGroupsRepository.findAll());
        return map;
    }

    /**
     * backend users
     */
    public Object backEndUsers() {

        UserTypes userType = userTypeRepository.findFirstByCode(UserTypes.SUPERADMIN);

        dataTable.select("u.id , u.first_name, u.last_name, u.phone, u.email,   ug.group_name, u.status, DATE_FORMAT(u.creation_date; '%Y-%m-%d %H:%i:%s')")
                .join("user_groups ug on ug.id = u.user_group ")
                .from("users as u")
                .orderBy("u.id desc")
                .nativeSQL(true);

        dataTable.where("u.user_type =:userTypeId");
        dataTable.setParameter("userTypeId", userType.getId());

        return dataTable.showTable();
    }


    /**
     * new user
     */
    public ResponseModel newUser(Users request) {
        UserTypes userType = userTypeRepository.findFirstByCode(UserTypes.SUPERADMIN);

        Users authUser = usersRepository.findFirstByEmail(SecurityUtils.getCurrentUserLogin());

        Optional<Users> checkUser = usersRepository.findDistinctByEmail(request.getEmail());
        if (checkUser.isPresent())
            return new ResponseModel("01", "User exist with similar email address.");

        Optional<UserGroups> userGroup = userGroupsRepository.findById(request.getUsergroupNo());
        if (!userGroup.isPresent())
            return new ResponseModel("01", "User group not supported.");

        if (!AppFunction.validatePhoneNumber(request.getPhone()))
            return new ResponseModel("01", "Sorry supplied phone number is invalid.");

        Users user = new Users();
        user.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(AppFunction.getInternationalPhoneNumber(request.getPhone(), ""));
        user.setEmail(request.getEmail());
        user.setUserTypeNo(userType.getId());
        user.setUsergroupNo(request.getUsergroupNo());
        user.setFlag("1");
        user.setCreatedById(authUser.getId());
        usersRepository.save(user);

        //notify user send email
        boolean sent = sendGridMailService.sendMail(sendGridMailService.sendGridConfig()
                .setTo(user.getEmail())
                .setTemplateId(MailOptions.Templates.PASSWORD_RESET_TEMPLATE.template)
                .setSubject("Set Up Account")
                .addAttribute("__name", user.getLastName())
                .addAttribute("__baseUrl", apv.appEndPoint + "/set-password/" + user.getId())

        );

        return new ResponseModel("00", "User created successfully.");
    }

    public ResponseModel userDetails(Long userId) {

        Optional<Users> checkUser = usersRepository.findById(userId);
        if (!checkUser.isPresent())
            return new ResponseModel("01", "User not found");

        return new ResponseModel("00", "success", checkUser.get());
    }

    /**
     * edit user
     */
    public ResponseModel editUser(Users request) {

        Users authUser = usersRepository.findFirstByEmail(SecurityUtils.getCurrentUserLogin());

        Optional<Users> checkUser = usersRepository.findById(request.getId());
        if (!checkUser.isPresent())
            return new ResponseModel("01", "User not found");


        Optional<UserGroups> userGroup = userGroupsRepository.findById(request.getUsergroupNo());
        if (!userGroup.isPresent())
            return new ResponseModel("01", "User group not supported.");


        Users user=checkUser.get();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsergroupNo(userGroup.get().getId());
        usersRepository.save(user);

        return new ResponseModel("00","User details updated successfuly");
    }

    /**
     * updates user
     */
    public ResponseModel updatesUser(String status, Long userId) {

        Users authUser = usersRepository.findFirstByEmail(SecurityUtils.getCurrentUserLogin());

        Optional<Users> checkUser = usersRepository.findById(userId);
        if (!checkUser.isPresent())
            return new ResponseModel("01", "User not found");

        if (userId.equals(authUser.getId()))
            return new ResponseModel("01","Sorry can't edit self");

        Users user=checkUser.get();

        if (status.equalsIgnoreCase("activate"))
            user.setStatus(AppConstant.STATUS_ACTIVE_RECORD);
        else
            user.setStatus(AppConstant.STATUS_INACTIVE_RECORD);

        usersRepository.save(user);

        return new ResponseModel("00","User details updated successfully");
    }
}
