package br.com.cdb.java.grupo4.eightbankspring.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateOfBirthValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Verifica se a data está no formato correto e se o usuário tem 18 anos ou mais

    public boolean validateDateOfBirth(String dob) {
        try {
            // Tenta parsear a data; se for bem-sucedido, retorna verdadeiro
            LocalDate.parse(dob, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean isOfLegalAge(String dob) {
        try {
            LocalDate birthDate = LocalDate.parse(dob, DATE_FORMATTER);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears() >= 18;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
