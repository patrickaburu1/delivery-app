package com.patrick.delivery.security.service;


import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.security.web.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author patrick
 * @project
 */
@Transactional
@Service("customApiUserDetailsService")
public class CustomApiUserDetailsService extends AbstractService implements UserDetailsService {

    @Autowired
    private UsersRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        Optional<Users> user = userRepository.findDistinctByEmail(email);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("No such user exists");
        }

        return buildAuthDetails(user.get());
    }

    private UserDetails buildAuthDetails(Users user) {
        // Iterate the list
        List<GrantedAuthority> result = new ArrayList<>();
        List<String> userRoles = new ArrayList<>();
        String field;
       /* boolean parentStatus;
        boolean enabled = (!user.getActive() && user.getUsergroup().getFlag().equals("ok") && user.getFlag().equals(AbstractRepository.ACTIVE_RECORD));
        boolean passwordNonExpired = user.getPasswordExpiry().after(new Date());
        boolean isAccountNonLocked = this.userAttemptsRepository.isAccountNonLocked(user.getEmail());
        Set<Permissions> groupPermissions = user.getUsergroup().getPermissions();
        for (Permissions row : groupPermissions) {
            field = row.getRole().getRoleCode();
            if (!userRoles.contains(field)) {
                result.add(new SimpleGrantedAuthority(field));
                userRoles.add(field);
            }
            if (!row.getActionCode().equals("default")) {
                GrantedAuthority action = new SimpleGrantedAuthority(field + "_" + row.getActionCode().toUpperCase());
                if (!result.contains(action)) {
                    result.add(action);
                }
            }
        }
        if (user.getEmail().contains("riverbank")) {
            result.add(new SimpleGrantedAuthority("ROLE_DOCUMENTATION"));
            result.add(new SimpleGrantedAuthority("ROLE_RB"));
        }
        String userType = user.getUsergroup().getUsertype().getName();
        switch (userType) {
            case BANK_ADMIN_TYPE:
                Bank bank = bankRepository.findById(user.getUsergroup().getBankId()).get();
                parentStatus = (bank.getStatus()) == 1;
                break;
            case CTP_ADMIN_TYPE:
                CTP ctp = ctpRepository.findById(user.getUsergroup().getCtpId()).get();
                parentStatus = (ctp.getStatus()) == 1;
                break;
            default:
                parentStatus = true;
                break;
        }*/
        return new User(user.getEmail(), user.getPassword(), true,
                true, true, true, result);
    }

    public Users getAuthicatedUser() {
        return userRepository.findDistinctByEmail(SecurityUtils.getCurrentUserLogin()).get();

    }
}
