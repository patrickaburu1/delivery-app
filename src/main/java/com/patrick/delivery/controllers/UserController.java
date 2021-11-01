package com.patrick.delivery.controllers;

import com.patrick.delivery.entities.Users;
import com.patrick.delivery.services.UsersService;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.utils.templates.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author patrick on 7/22/20
 * @project sprintel-delivery
 */
@RestController
public class UserController extends AbstractController {

    @Autowired
    private UsersService usersService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USERS')")
    public Object users( HttpServletRequest request) {
        return new View("app/users",usersService.usersDetails()).getView();
    }

    @RequestMapping(value = "/backend-users", method = RequestMethod.GET)
    public Object backendUsers() {
        return usersService.backEndUsers();
    }

    @RequestMapping(value = "/new-user", method = RequestMethod.POST)
    @PostAuthorize("hasRole('ROLE_USERS')")
    public ResponseModel newUser(@ModelAttribute Users request) {
        return usersService.newUser(request);
    }

    @GetMapping(value = "/user-details/{userId}")
    public Object userDetails(@PathVariable Long userId , HttpServletRequest request){
        return usersService.userDetails(userId);
    }


    @RequestMapping(value = "/edit-user", method = RequestMethod.POST)
    @PostAuthorize("hasRole('ROLE_USERS')")
    public ResponseModel editUser(@ModelAttribute Users request) {
        return usersService.editUser(request);
    }

    @RequestMapping(value = "/edit-user/{status}/{userId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_USERS')")
    public ResponseModel updateUser(@PathVariable String status, @PathVariable Long userId) {
        return usersService.updatesUser(status,userId);
    }
}
