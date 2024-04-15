package br.com.cdb.java.grupo4.eightbankspring.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneNumberValidator {
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}");

    public boolean validatePhoneNumber(String phoneNumberString) {
        if (phoneNumberString == null || phoneNumberString.isBlank()){
            return false;
        }
       return PHONE_PATTERN.matcher(phoneNumberString).matches();
    }
}
