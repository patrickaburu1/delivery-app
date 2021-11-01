package com.patrick.delivery.controllers;

import com.patrick.delivery.entities.UserTypes;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.repositories.api.DeliveryPasswordResetRepository;
import com.patrick.delivery.repositories.api.UserTypeRepository;
import com.patrick.delivery.repositories.api.UsersRepository;
import com.patrick.delivery.security.service.UserService;
import com.patrick.delivery.utils.mails.MailOptions;
import com.patrick.delivery.utils.mails.MailerServiceInterface;
import com.patrick.delivery.utils.templates.View;
import com.patrick.delivery.properties.ApplicationPropertiesValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController extends AbstractController {
    @Autowired
    private UserService userService;
    @Autowired
    @Qualifier("sendGridMailService")
    protected MailerServiceInterface sendGridMailService;
    @Autowired
    private ApplicationPropertiesValues apv;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private DeliveryPasswordResetRepository deliveryPasswordResetRepository;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Object login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            @RequestParam(value = "login", required = false) String login,
            RedirectAttributes redirectAttributes, HttpServletRequest request) {

        ModelAndView model = new ModelAndView();

        model.setViewName("user/login");
        boolean redirect = false;

        if (null!=login) {
            redirectAttributes.addFlashAttribute("success", "Already registered login to continue.");
            return model;
        }
        if (null != error) {
            Exception exception = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            String response = getErrorMessage(request, null, exception);
            redirectAttributes
                    .addFlashAttribute("state", "danger")
                    .addFlashAttribute("msg", response);

            return "redirect:/login";

        }

        if (logout != null) {
            redirect = true;
            redirectAttributes.addFlashAttribute("msg", "You've been logged out successfully.");
        }

        if (expired != null) {
            redirect = true;
            redirectAttributes.addFlashAttribute("msg", "Sorry, your session timed out.");
        }
        if (redirect) return "redirect:/login";

        return model;
    }

    /**
     * Maps requests to initiate a password reset
     *
     * @param request
     * @param redirectAttributes
     * @return ModelAndView
     */
    @RequestMapping(value = "/password-reset")
    public ModelAndView passwordResetRequest(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // Get the view
        View view = new  View("user/forgot-password");

        // If we have posted the data
        if (request.getMethod().equals("POST")) {
            String email = request.getParameter("email");
            UserTypes userType=userTypeRepository.findFirstByCode(UserTypes.SUPERADMIN);
            Users entity = usersRepository.findByEmailAndUserTypeNo(email,userType.getId());
            if (null == entity) {
                redirectAttributes.addFlashAttribute("msg", view.getMessage("login.missingAccount"))
                        .addFlashAttribute("state", "danger");
                return view.redirect("password-reset");
            }
            // The flash message params
            String code = deliveryPasswordResetRepository.resetEmail(entity.getId(), entity.getEmail());
          //  String code = "28283748";
            boolean sent = sendGridMailService.sendMail(sendGridMailService.sendGridConfig()
                    .setTo(email)
                    .setTemplateId(MailOptions.Templates.PASSWORD_RESET_TEMPLATE.template)
                    .setSubject("Password Reset")
                    .addAttribute("__name", entity.getLastName())
                    .addAttribute("__baseUrl", apv.appEndPoint + "/password-reset/" + code)

            );

            if (sent) {
                redirectAttributes.addFlashAttribute("msg", "An email has been sent to " + email + " with the link to reset your password")
                        .addFlashAttribute("state", "success");
                return view.redirect("login");
            }

            else {
                redirectAttributes.addFlashAttribute("msg", "An error occurred while trying to send a password " +
                        "request email. Try again")
                        .addFlashAttribute("state", "danger");
                return view.redirect("password-reset");
            }
        }

        // Show the home page
        return view.getView();
    }



    /**
     * Complete password reset process
     *
     * @param request
     * @param redirectAttributes
     * @param resetKey
     * @return ModelAndView
     */
    @RequestMapping(value = "/password-reset/{resetKey}")
    public ModelAndView passwordResetConfirmation(
            HttpServletRequest request, RedirectAttributes redirectAttributes,
            @PathVariable String resetKey
    ) {
        Map<String, String> model = new HashMap<>();
        model.put("token", resetKey);
        View view = new View("user/reset", model);
        if (request.getMethod().equals("POST")) {
            String response = this.deliveryPasswordResetRepository.completeReset(resetKey,
                    request.getParameter("password"), request.getParameter("confirmPassword"));
            if ("mismatch".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return view.redirect("password-reset/" + resetKey);
            } else if ("invalid".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "Invalid reset key");
                return view.redirect("password-reset");
            } else if ("invalid".equals(response))
                return view.redirect("password-reset");
            else if ("code-expired".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "Reset password link is expired. Contact admin");
                return view.redirect("password-reset");
            } else if ("code-exist-fail".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "The link appears to have been used already");
                return view.redirect("login");
            } else {
                redirectAttributes.addFlashAttribute("msg", "Account has been successfully updated");
                return view.redirect("login");
            }
        }

        // Show the home page
        return view.getView();
    }




    /**
     *
     * @param request
     * @param redirectAttributes
     * @param id
     * @return ModelAndView
     */
    @RequestMapping(value = "/set-password/{id}")
    public ModelAndView setAccountPassword(
            HttpServletRequest request, RedirectAttributes redirectAttributes,
            @PathVariable Long id
    ) {
        Map<String, String> model = new HashMap<>();
        model.put("id", String.valueOf(id));
        View view = new View("user/set-password", model);
        if (request.getMethod().equals("POST")) {
            String response = this.deliveryPasswordResetRepository.setPassowrd(id,
                    request.getParameter("password"), request.getParameter("confirmPassword"));
            if ("mismatch".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return view.redirect("set-password/" + id);
            } else if ("invalid".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "Invalid reset key");
                return view.redirect("set-password");
            }
            else if ("already-set".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "Reset password link is expired. Contact admin");
                return view.redirect("login");
            } else if ("code-exist-fail".equals(response)) {
                redirectAttributes.addFlashAttribute("error", "The link appears to have been invalid");
                return view.redirect("login");
            } else {
                redirectAttributes.addFlashAttribute("msg", "Account has been successfully set");
                return view.redirect("login");
            }
        }

        // Show the home page
        return view.getView();
    }

    private String getErrorMessage(HttpServletRequest request, View view, Exception exception) {
        String error = "";
        //Account Does not exist
        if (exception instanceof UsernameNotFoundException) {
            error = exception.getMessage();
        }
        //Invalid credentials
        else if (exception instanceof BadCredentialsException) {
            error = exception.getMessage();
        }

        //When account has been locked
        else if (exception instanceof LockedException) {
            error = exception.getMessage();
        }

        //When credentials have expired
        else if (exception instanceof CredentialsExpiredException) {
            error = "Account Expired.";
        }

        //When account has expired( though this one will never happen)
        else if (exception instanceof AccountExpiredException) {
            error = "Account Expired.";
        }

        //When account is inactive
        else if (exception instanceof AccountStatusException) {
            error = "Account Suspended.";
        } else {
            exception.printStackTrace();
            error = "Login Error.";
        }
        return error;
    }

}
