package br.com.cdb.java.grupo4.eightbankspring.dtos;

import br.com.cdb.java.grupo4.eightbankspring.model.client.Address;
import lombok.Setter;

import java.time.LocalDate;

public class ClientDTO {
    private String email;
    private String password;
    private String name;
    private String cpf;
    @Setter
    private LocalDate dateOfBirth;
    private Address address;
    private String phoneNumber;
    private double grossMonthlyIncome;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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
