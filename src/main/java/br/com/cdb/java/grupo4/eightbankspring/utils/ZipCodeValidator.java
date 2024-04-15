package br.com.cdb.java.grupo4.eightbankspring.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ZipCodeValidator {
    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");

    public boolean validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.isBlank()) {
            return false;
        }
        return ZIP_CODE_PATTERN.matcher(zipCode).matches();
    }
}
