package com.patrick.delivery.security.service;

import com.patrick.delivery.entities.Permissions;
import com.patrick.delivery.entities.UserTypes;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.repositories.AbstractRepository;
import com.patrick.delivery.repositories.api.UserGroupsRepository;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.security.web.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
@Transactional
@Service("customUserDetailsService")
public class CustomUserDetailsService extends AbstractService implements UserDetailsService {

    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private UserGroupsRepository usergroupRepo;


    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        Optional<Users> user = userRepository.findDistinctByEmail(email);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("No such user exists");
        }

        return buildUserForAuthentication(user.get());
    }

    private UserDetails buildAuthDetails(Users user) {
        // Iterate the list
        List<GrantedAuthority> result = new ArrayList<>();
        List<String> userRoles = new ArrayList<>();
        String field;

        return new User(user.getPhone(), user.getPassword(), true,
                true, true, true, result);
    }

    private User buildUserForAuthentication(Users user) {
        List<GrantedAuthority> roles = new ArrayList<>();
        List<String> userRoles = new ArrayList<>();
        String field;
        boolean parentStatus = false;
        String groupStatus = "1";
        /*Populate user permissions*/
        if (null != user.getUserGroupLink()) {
            /*Retrieve usergroup status*/
            groupStatus = user.getUserGroupLink().getFlag();
            Set<Permissions> permissions = usergroupRepo.findFirstById(user.getUsergroupNo()).getPermissions();

            for (Permissions permObject : permissions) {
                field = permObject.getRole().getRoleCode();
                if (!userRoles.contains(field)) {
                    roles.add(new SimpleGrantedAuthority(field));
                    userRoles.add(field);
                }
                if (!permObject.getActionCode().equals("default")) {
                    GrantedAuthority action = new SimpleGrantedAuthority(field + "_" + permObject.getActionCode().toUpperCase());
                    if (!roles.contains(action)) {
                        roles.add(action);
                    }
                }
            }
        }

        switch (user.getUserTypeLink().getCode()) {
            case UserTypes.SUPERADMIN:
                parentStatus = true;
                roles.add(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
                break;

            default:
                parentStatus = true;
                roles.add(new SimpleGrantedAuthority("ROLE_OTHERS"));
                break;
        }

        if (roles.isEmpty()) roles = AuthorityUtils.NO_AUTHORITIES;

        /* Check if user is enabled: enabled field, user status && usergroup status */
        boolean isUserActive =parentStatus && user.getFlag().equals(AbstractRepository.ACTIVE_RECORD) && groupStatus.equals(AbstractRepository.ACTIVATE_RECORD);


        /*Return Spring Security User object*/

        return new User(user.getEmail(), user.getPassword() == null ? "" : user.getPassword(), isUserActive, true, true,
                true,
                roles);
    }

    public Users getAuthicatedUser() {
        return userRepository.findDistinctByEmail(SecurityUtils.getCurrentUserLogin()).get();

    }
}
