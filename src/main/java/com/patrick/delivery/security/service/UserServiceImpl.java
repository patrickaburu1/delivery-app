package com.patrick.delivery.security.service;


import com.patrick.delivery.entities.Users;
import com.patrick.delivery.models.LoginRequest;
import com.patrick.delivery.repositories.api.UserTypeRepository;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.security.web.LoginSuccessHandler;
import com.patrick.delivery.security.web.SecurityUtils;
import com.patrick.delivery.utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("userService")
public class UserServiceImpl extends AbstractService implements UserService {

    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseModel webSignin(LoginRequest request) {
        ResponseModel response = new ResponseModel();
        response.setStatus("01");

        Optional<Users> checkUser = userRepository.findDistinctByEmail(request.getEmail());

        if (!checkUser.isPresent()) {
            response.setMessage("Sorry! Phone number not in our records. Click get started to start.");
            return response;
        }

        Users user = checkUser.get();
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getPhone(),
                    request.getPassword()
            );

            final Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            // Inject into security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String phoneNumber = null;
            if (authentication.getPrincipal() instanceof Users)
                phoneNumber = ((Users) authentication.getPrincipal()).getPhone();
            else
                phoneNumber = (String) authentication.getName();

            response.setStatus("00");
            response.setMessage("Authentication successful.");
            new LoginSuccessHandler();

        } catch (BadCredentialsException e) {
            // error.append("Sorry! Your account is inactive. Contact administrator.  ");
            response.setMessage("Sorry! Credentials don't match our records");
            response.setStatusMessage("ERR_MESSAGE_WRONG_CREDENTIALS");
            response.setStatus("01");
        }
        return response;
    }

    // todo I don't know why I need this @Transactional - propagation new since without it, it causes an infinite loop
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseModel<Users> findLoggedInUser() {
        Users loggedInUser = loggedInUser();

        return new ResponseModel<>("00", "Ok", loggedInUser);
    }

    public Users loggedInUser() {
        String loggedInUser = SecurityUtils.getCurrentUserLogin();
        Optional<Users> loggedInUserOptional = userRepository.findDistinctByPhone(loggedInUser);
        return loggedInUserOptional.orElse(null);
    }

    public Users findUserByPhone(String phone) {
        return userRepository.findDistinctByPhone(phone).get();
    }


}
