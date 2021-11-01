package com.patrick.delivery.security.web;


import com.patrick.delivery.security.service.CustomUserDetailsService;
import com.patrick.delivery.entities.Permissions;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.entities.Users;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author patrick
 * @project
 */
@Component
@Transactional
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private CustomUserDetailsService userDetailsService;


    @PersistenceContext(unitName = "delivery")
    private EntityManager entityManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication auth) throws IOException, ServletException {

        HttpSession session = request.getSession();
        String username = SecurityUtils.getCurrentUserLogin();
        // Get the user who has logged into the app
        Users user = userDetailsService.getAuthicatedUser();
        session.setAttribute("_userNo", user.getId());

        // Set some parameters
        session.setAttribute("_userNo", user.getId());
        session.setAttribute("_appUserName", username);
        session.setAttribute("_appUserActions", fetchUserActions(user.getId()));

        logger.info("******* session set  on login "+user.getEmail());
        handle(request, response, auth);
        clearAuthenticationAttributes(request);
        int timeout = 30;
        session.setMaxInactiveInterval(timeout * 60);
        super.onAuthenticationSuccess(request, response, auth);
    }

    public Map<String, Map<String, Boolean>> fetchUserActions(Long userId) {

        Map<String, Map<String, Boolean>> userActions = new HashMap<String, Map<String, Boolean>>();

        // Build the query
        Iterator<Users> it =  entityManager.unwrap(Session.class)
                .createQuery("from Users where id = :userId")
                .setParameter("userId", userId)
                .list().iterator();

        // If there is no such user, end here
        if (!it.hasNext()) return null;

        // Get the user and initialise the roles
        String field;
        Users user = it.next();
        Set<Permissions> permissions = user.getUserGroupLink().getPermissions();

        // Generate the required user action map
        for (Permissions row : permissions) {
            field = row.getRole().getRoleCode();

            // If the item has not been set, place it there
            if (!userActions.containsKey(field))
                userActions.put(field, new HashMap<String, Boolean>());

            // Set the map
            userActions.get(field).put(row.getActionCode(), Boolean.TRUE);
        }

        return userActions;
    }
}
