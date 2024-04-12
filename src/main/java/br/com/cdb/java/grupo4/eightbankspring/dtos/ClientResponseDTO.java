package br.com.cdb.java.grupo4.eightbankspring.dtos;

import br.com.cdb.java.grupo4.eightbankspring.model.client.Address;

import java.time.LocalDate;

public class ClientResponseDTO {

    private String email;
    private String name;
    private String cpf;
    private LocalDate dateOfBirth;
    private Address address;
    private String phoneNumber;
    private double grossMonthlyIncome;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getGrossMonthlyIncome() {
        return grossMonthlyIncome;
    }
}
