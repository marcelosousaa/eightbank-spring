package br.com.cdb.java.grupo4.eightbankspring.dtos;

import br.com.cdb.java.grupo4.eightbankspring.model.client.Address;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class ClientDTO {
    private String email;
    private String password;
    private String name;
    private String cpf;
    private LocalDate dateOfBirth;
    private Address address;
    private String phoneNumber;
    private double grossMonthlyIncome;

}
