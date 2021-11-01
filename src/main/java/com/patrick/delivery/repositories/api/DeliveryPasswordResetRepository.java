package com.patrick.delivery.repositories.api;

import com.patrick.delivery.entities.DeliveryPasswordReset;
import com.patrick.delivery.entities.Users;
import com.patrick.delivery.repositories.AbstractRepository;
import com.patrick.delivery.utils.ResponseModel;
import com.patrick.delivery.utils.SystemComponent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
@Repository
public class DeliveryPasswordResetRepository extends AbstractRepository<DeliveryPasswordReset> {

    public DeliveryPasswordResetRepository() {
        setMapping(DeliveryPasswordReset.class);
    }


    public String resetPasswordMobile(Long userId, String email) {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 2); // adds one hour
        // Generate the random code
        String response = SystemComponent.randomCodeNumber(4);

        //Check if a user reset is already there
        DeliveryPasswordReset newUserReset;
        newUserReset = fetchOne(userId);

        if (null != newUserReset) { // delete is exist
            //deleteRecord(newUserReset);
            getCurrentSession().delete(newUserReset);
        }

        // Save the new reset value
        newUserReset = new DeliveryPasswordReset();
        newUserReset.setGuid(response);
        newUserReset.setUserId(userId);
        newUserReset.setEmail(email);
        newUserReset.setTime(cal.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 3);
        Timestamp token_expiry_timestamp = new Timestamp(calendar.getTime().getTime());
        newUserReset.setTime(token_expiry_timestamp);
        // Save the transaction
        getCurrentSession().persist(newUserReset);

        // All is well
        return response;
    }

    public String resetEmail(Long userId, String email) {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 2); // adds one hour
        // Generate the random code
        String response = randomStringGenerator(32, false);

        //Check if a user reset is already there
        DeliveryPasswordReset newUserReset;
        newUserReset = fetchOne(userId);

        if (null != newUserReset) { // delete is exist
            deleteRecord(newUserReset);
        }


        // Save the new reset value
        newUserReset = new DeliveryPasswordReset();
        newUserReset.setGuid(response);
        newUserReset.setUserId(userId);
        newUserReset.setEmail(email);
        newUserReset.setTime(cal.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 3);
        Timestamp token_expiry_timestamp = new Timestamp(calendar.getTime().getTime());
        newUserReset.setTime(token_expiry_timestamp);
        // Save the transaction
        getCurrentSession().persist(newUserReset);


        // All is well
        return response;
    }


    @Transactional
    public String completeReset(String code, String oldPassword, String newPassword) {
        // If the password do not match
        if (!oldPassword.equals(newPassword)) {
            return "mismatch";
        }

        // Check if the code has been created
        Iterator<DeliveryPasswordReset> it = getCurrentSession()
                .createQuery("from DeliveryPasswordReset where guid = :guid")
                .setParameter("guid", code)
                .list().iterator();

        // If the item does not exist, indicate that
        if (!it.hasNext()) {
            return "code-exist-fail";
        }

        // Get the request
        DeliveryPasswordReset userReset = it.next();
        String response = "code-expired";

        // Check if the code has not expired
        Date ddate = (new Date(System.currentTimeMillis()));
        if (userReset.getTime().after(ddate)) {
            Users user = (Users) getCurrentSession()
                    .createQuery("from Users where id = :userId")
                    .setParameter("userId", userReset.getUserId())
                    .list().iterator().next();

            // Set the response
            response = user.getEmail();

            user.setPassword(newPassword);
//                    .setEnabled(true);

            // Save the changes
            getCurrentSession().persist(user);
        }

        // Remove the user reset record
        getCurrentSession().delete(userReset);

        // All is well
        return response;
    }

    @Transactional
    public String setPassowrd(Long id, String oldPassword, String newPassword) {
        // If the password do not match
        if (!oldPassword.equals(newPassword)) {
            return "mismatch";
        }

        Users user = (Users) getCurrentSession()
                .createQuery("from Users where id = :userId")
                .setParameter("userId", id)
                .list().iterator().next();

        // If the item does not exist, indicate that
        if (null==user) {
            return "code-exist-fail";
        }

        String response = "already-set";


        if (null != user.getPassword() && !user.getPassword().isEmpty())
            return response;

        // Set the response
        response = user.getEmail();

        user.setPassword(newPassword);

        getCurrentSession().persist(user);
        // All is well
        return response;
    }


    @Transactional
    public ResponseModel completeResetMobile(String code, String oldPassword, String newPassword) {
        // If the password do not match
        if (!oldPassword.equals(newPassword)) {

            return new ResponseModel("01", "Sorry Password mismatch.");
        }

        // Check if the code has been created
        Iterator<DeliveryPasswordReset> it = getCurrentSession()
                .createQuery("from DeliveryPasswordReset where guid = :guid")
                .setParameter("guid", code)
                .list().iterator();

        // If the item does not exist, indicate that
        if (!it.hasNext()) {
            return new ResponseModel("01", "Invalid code.");
        }

        // Get the request
        DeliveryPasswordReset userReset = it.next();
        String response = "code-expired";

        // Check if the code has not expired
        Date ddate = (new Date(System.currentTimeMillis()));
        if (userReset.getTime().after(ddate)) {
            Users user = (Users) getCurrentSession()
                    .createQuery("from Users where id = :userId")
                    .setParameter("userId", userReset.getUserId())
                    .list().iterator().next();

            // Set the response
            response = user.getEmail();

            user.setPassword(newPassword);
//                    .setEnabled(true);

            // Save the changes
            getCurrentSession().persist(user);
        }

        // Remove the user reset record
        getCurrentSession().delete(userReset);

        // All is well
        return new ResponseModel("00", "Password reset successfully.");
    }
}
