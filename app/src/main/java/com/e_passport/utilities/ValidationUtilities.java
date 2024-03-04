package com.e_passport.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class ValidationUtilities {
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z.]{3,}$";
        return Pattern.matches(emailPattern, email);
    }

    public static boolean isStrongPassword(String password) {
        return password.length() >= 8 && Pattern.matches(".*[a-zA-Z].*", password) &&
                Pattern.matches(".*\\d.*", password) && Pattern.matches(".*[@#$%^&+=!].*", password);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return (phoneNumber.length() == 11 && !phoneNumber.startsWith("+88")) || (phoneNumber.length() == 14 && phoneNumber.startsWith("+88"));
    }

    public static boolean isValidNidNumber(String nid) {
        if (nid.trim().length() < 9) {
            return false;
        }
        return true;
    }

    public static boolean isDateOfBirthValid(String selectedDate, int minimum_age) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date selected = dateFormat.parse(selectedDate);
            Calendar minValidDate = Calendar.getInstance();
            minValidDate.add(Calendar.YEAR, - minimum_age);
            if (selected != null) {
                return selected.before(minValidDate.getTime());
            }
        } catch (Exception e) {}
        return false;
    }
}
