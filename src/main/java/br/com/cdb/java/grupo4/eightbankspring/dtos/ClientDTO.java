package br.com.cdb.java.grupo4.eightbankspring.dtos;

import lombok.Getter;

@Getter
public class ClientDTO {
    private String email;
    private String password;
    private String name;
    private String cpf;
    private String dateOfBirth;
    private AddressDTO address;
    private String phoneNumber;
    private String grossMonthlyIncome;


}
