package br.com.cdb.java.grupo4.eightbankspring.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateOfBirthValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Verifica se a data está no formato correto e se o usuário tem 18 anos ou mais
    public static boolean validateDateOfBirth(String dob) {
        try {
            LocalDate.parse(dob, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isOfLegalAge(String dob) {
        try {
            LocalDate birthDate = LocalDate.parse(dob, DATE_FORMATTER);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears() >= 18;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
