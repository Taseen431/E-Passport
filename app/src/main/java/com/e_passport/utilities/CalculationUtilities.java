package com.e_passport.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculationUtilities {
    public static String calculateAge(String birthdate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = dateFormat.parse(birthdate);
            Date currentDate = new Date();
            long diff = currentDate.getTime() - birthDate.getTime();
            long ageInMilliseconds = Math.abs(diff);
            int age = (int) (ageInMilliseconds / (1000L * 60 * 60 * 24 * 365));
            return age + " years";
        } catch (ParseException e) {
            return "Age calculation error";
        }
    }
}
