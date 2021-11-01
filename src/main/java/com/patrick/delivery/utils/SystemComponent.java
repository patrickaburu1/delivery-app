package com.patrick.delivery.utils;

import com.patrick.delivery.entities.Users;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
@Component
public class SystemComponent {
    /**
     * verify user password
     *
     * */
    public static Boolean passwordMatches(String password, Users user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * generate random  code
     */
    public static String randomCodeNumber(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

}
