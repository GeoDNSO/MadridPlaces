package com.example.App.utilities;

import com.example.App.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    // https://stackoverflow.com/questions/8204680/java-regex-email
    public static boolean validEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean argumentsEmpty(String... args) {
        for (String arg : args) {
            if(arg.isEmpty())
                return true;
        }
        return false;
    }


}
