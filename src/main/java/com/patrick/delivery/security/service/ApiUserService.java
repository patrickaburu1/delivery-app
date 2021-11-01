package com.patrick.delivery.security.service;

import com.patrick.delivery.services.NotificationService;
import com.patrick.delivery.entities.DeliveryPasswordReset;
import com.patrick.delivery.entities.UserTypes;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.models.ApiForgotPasswordReq;
import com.patrick.delivery.models.ApiResetPasswordReq;
import com.patrick.delivery.models.ApiUserDetailsRes;
import com.patrick.delivery.models.LoginRequest;
import com.patrick.delivery.properties.ApplicationPropertiesValues;
import com.patrick.delivery.repositories.api.DeliveryPasswordResetJpaRepository;
import com.patrick.delivery.repositories.api.DeliveryPasswordResetRepository;
import com.patrick.delivery.repositories.api.UserTypeRepository;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.security.api.TokenHelper;
import com.patrick.delivery.utils.AppConstant;
import com.patrick.delivery.utils.ApplicationMessages;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */
@Service
@Transactional
public class ApiUserService extends AbstractService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenHelper tokenHelper;
    @Autowired
    private ApplicationPropertiesValues apv;
    @Autowired
    private DeliveryPasswordResetRepository deliveryPasswordResetRepository;
    @Autowired
    private DeliveryPasswordResetJpaRepository deliveryPasswordResetJpaRepository;
    @Autowired
    private NotificationService notificationService;


    public Map<String, Object> login(LoginRequest request, HttpServletRequest httpServletRequest) {
        Map<String, Object> response = new HashMap<>();

        String ip = httpServletRequest.getHeader("X-Forwarded-For") == null ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("X-Forwarded-For");

        UserTypes userType = userTypeRepository.findFirstByCode(UserTypes.RIDER);
        Users user = usersRepository.findByEmailAndUserTypeNo(request.getEmail(), userType.getId());

        if (null == user) {
            response.put("status", "01");
            response.put("message", "Sorry you are not registered.");
            return response;
        }
        if (null==user.getPassword() || user.getPassword().isEmpty()){
            response.put("message", "The email address or password you have entered is invalid");
            response.put("statusMessage", "SET_UP_ACCOUNT");
            response.put("status", "01");
            return response;
        }

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    request.getPassword()
            );

            final Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            // Inject into security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String email = null;

            if (authentication.getPrincipal() instanceof Users)
                email = ((Users) authentication.getPrincipal()).getEmail();
            else
                email = (String) authentication.getName();

            logger.info("*******PHONE_NUMBER_TOKEN: " + email);

            if (!user.getStatus().equalsIgnoreCase(AppConstant.STATUS_ACTIVE_RECORD)) {
                response.put("message", "Sorry account suspended");
                response.put("status", "01");
                return response;
            }

            String jwToken = tokenHelper.generateToken(email);

            ApiUserDetailsRes apiUserDetailsRes = new ApiUserDetailsRes();
            apiUserDetailsRes.setEmail(user.getEmail());
            apiUserDetailsRes.setFirstName(user.getFirstName());
            apiUserDetailsRes.setLastName(user.getLastName());
            apiUserDetailsRes.setPhone(user.getPhone());

            response.put("status", "00");
            response.put("accessToken", jwToken);
            response.put("expiry", apv.expiresIn * 1000);
            response.put("message", "Authentication successful.");
            response.put("userDetails", apiUserDetailsRes);

            user.setFireBaseToken(request.getFirebaseToken());
            usersRepository.save(user);


        } catch (BadCredentialsException e) {
            // error.append("Sorry! Your account is inactive. Contact administrator.  ");
            response.put("message", "The email address or password you have entered is invalid");
            response.put("statusMessage", "ERR_MESSAGE_WRONG_CREDENTIALS");
            response.put("status", "01");
        }
        return response;
    }

    /**
     * forgot password
     */
    public ResponseModel forgotPassword(ApiForgotPasswordReq req) {
        UserTypes userType = userTypeRepository.findFirstByCode(UserTypes.RIDER);
        Users user = usersRepository.findByEmailAndUserTypeNo(req.getEmail(), userType.getId());

        if (null == user)
            return new ResponseModel("01", ApplicationMessages.get("response.forgot.password.driver.invalid.address"));

        if (!user.getStatus().equalsIgnoreCase(AppConstant.STATUS_ACTIVE_RECORD))
            return new ResponseModel("01", "Sorry has been account suspended");

        List<DeliveryPasswordReset> deliveryPasswordResets = deliveryPasswordResetJpaRepository.findAllByUserId(user.getId());
        deliveryPasswordResetJpaRepository.deleteAll(deliveryPasswordResets);

        String code = deliveryPasswordResetRepository.resetPasswordMobile(user.getId(), user.getEmail());

        //send sms
        notificationService.sendSMSInfoBib(user.getPhone(), String.format(ApplicationMessages.get("sms.reset.password"), code, req.getHashKey()));

        return new ResponseModel("00", "Success an OTP has been sent to your phone number.");
    }


    /**
     * reset password
     */
    public ResponseModel resetPassword(ApiResetPasswordReq req) {
        UserTypes userType = userTypeRepository.findFirstByCode(UserTypes.RIDER);
        Users user = usersRepository.findByEmailAndUserTypeNo(req.getEmail(), userType.getId());

        if (null == user)
            return new ResponseModel("01", "Sorry! No account found with that email provided.");

        if (!user.getStatus().equalsIgnoreCase(AppConstant.STATUS_ACTIVE_RECORD))
            return new ResponseModel("01", "Sorry has been account suspended");

        return deliveryPasswordResetRepository.completeResetMobile(req.getCode(), req.getNewPassword(), req.getConfirmPassword());

    }
}
