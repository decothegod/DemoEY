package com.example.demoEY.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class UtilsServices {

    public static boolean validateEmail(String emailAddress) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public static Boolean validatePassword(String pwd) {
        String regexPattern = "(^([^A-Z]*[A-Z][^A-Z]*)(\\D*\\d\\D*\\d\\D*)$)|(^(\\D*\\d\\D*\\d\\D*)$)";
        return Pattern.compile(regexPattern)
                .matcher(pwd)
                .matches();
    }

    public static String covertDateStr(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date(timeMillis));
    }
}
