package br.com.cdb.java.grupo4.eightbankspring.utils;

public class PhoneNumberValidator {

    public static boolean validatePhoneNumber(String phoneNumberString) {
        String phoneNumberPattern = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}";
        return phoneNumberString.matches(phoneNumberPattern);
    }
}
