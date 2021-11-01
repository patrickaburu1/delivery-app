package com.patrick.delivery.security.web;

import com.patrick.delivery.entities.UserTypes;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.repositories.api.UserTypeRepository;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author patrick on 2/8/20
 * @project sprintel-delivery
 */
@Component
public class DeliveryWebAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    @Qualifier("customUserDetailsService")
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        super.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        StringBuilder error = new StringBuilder();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For") == null ? request.getRemoteAddr() : request.getHeader("X-Forwarded-For");

        String email = authentication.getName();

        UserTypes userType=userTypeRepository.findFirstByCode(UserTypes.SUPERADMIN);

        if (null==userType){
            error.append("Sorry an error occurred please contact system admin!");
            throw new BadCredentialsException(error.toString());
        }
        Users user=userRepository.findByEmailAndUserTypeNo(email,userType.getId());

        if (null==user){
            error.append("Sorry invalid credentials!");
            throw new BadCredentialsException(error.toString());
        }
        if (null==user.getPassword()){
            error.append("Sorry! Follow instructions sent to your email for account setup.");
            throw new BadCredentialsException(error.toString());
        }
        if (!user.getStatus().equalsIgnoreCase(AppConstant.STATUS_ACTIVE_RECORD)){
            error.append("Sorry! your account is not active. Contact administrator for more info.");
            throw new BadCredentialsException(error.toString());
        }
        try {
            Authentication auth = super.authenticate(authentication);
            if (userRepository.findDistinctByEmail(email).isPresent()) {
                email = userRepository.findDistinctByEmail(email).get().getEmail();
            }

            return auth;
        } catch (LockedException e) {
            error.append("User account is locked");
            throw new LockedException(error.toString());
        } catch (UsernameNotFoundException e) {
            error.append("User does not exist");
            throw new UsernameNotFoundException(error.toString());
        } catch (CredentialsExpiredException e) {
            throw new CredentialsExpiredException(error.toString());
        } catch (BadCredentialsException e) {
            if (userRepository.findDistinctByEmail(email).isPresent()) {
                 email = userRepository.findDistinctByEmail(email).get().getEmail();

                error.append("Sorry credentials does't match our records.");
                throw new BadCredentialsException(error.toString());
            } else {
                error.append("User does not exist");
                throw new UsernameNotFoundException(error.toString());
            }
        }
    }
}
