package com.patrick.delivery.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author patrick on 7/1/20
 * @project sprintel-delivery
 */
@Component
public class AppFunction extends GeneralLogger {

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

    public static String convertUnixDateToStringDate(Long unixDate){
        //convert seconds to milliseconds
        if (null==unixDate)
            return null;
        Date date = new Date(unixDate*1000L);
        String strDate;
        try {
            SimpleDateFormat jdf = new SimpleDateFormat("dd/MM/yyyy");
            strDate=jdf.format(date);
        }catch (Exception e){
            e.getMessage();
            strDate=null;
        }
        return strDate;
    }


    /**
     * validate phone number
     *
     * @return boolean
     */
    public static boolean validatePhoneNumber(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber verifyPhonenumber;
        try {
            verifyPhonenumber = phoneUtil.parse(phone, AppConstant.KENYA_COUNTRY_CODE);
        } catch (NumberParseException e) {
         //   logger.info("NumberParseException was thrown: " + e.toString());
            return false;
        }
        return phoneUtil.isValidNumber(verifyPhonenumber);
    }


    public static String getInternationalPhoneNumber(String mobileNumber, String country) {
        try {
            if (null == country || country.isEmpty() || " ".equals(country)) {
                country = AppConstant.KENYA_COUNTRY;
            }
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = null;

            //when the country is defined
            if (null != country || " ".equals(country) || country.isEmpty()) {
                Map<String, String> countries = new HashMap<>();
                for (String isoCountry : Locale.getISOCountries()) {
                    Locale l = new Locale("", isoCountry);
                    countries.put(l.getDisplayCountry(), isoCountry);
                }
                String countryCode = countries.get(country);
                numberProto = phoneUtil.parse(mobileNumber, countryCode);
            }
            //when country is unknown
            else {
                numberProto = phoneUtil.parse(mobileNumber, "");
            }

            return phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (Exception e) {
//            appAuditLogRepo.mailError(e.getLocalizedMessage());
           // logger.error("*********** Error getInternationalPhoneNumber() *************", e);
            return mobileNumber;
        }
    }


    public  static Integer getDiffBetweenDates(String from, String to) {

        Logger logger= LoggerFactory.getLogger(AppFunction.class);
        long daysBetween=0;
        try {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateTo = myFormat.parse(to);
            Date dateFrom = myFormat.parse(from);

            long difference =  dateTo.getTime()-dateFrom.getTime();
            daysBetween = (difference / (1000*60*60*24));

            System.out.println("Number of Days between dates: "+daysBetween);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("================ an error occurred while trying to get date difference ");
        }

        return Math.toIntExact((daysBetween));
    }

    public static Date formatStringToDate(String strdate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(strdate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public static String formatDateToStringDateSystemFormat(Date date) {
        SimpleDateFormat dateFormter = new SimpleDateFormat("yyyy-MM-dd");

        return dateFormter.format(date);
    }
}
