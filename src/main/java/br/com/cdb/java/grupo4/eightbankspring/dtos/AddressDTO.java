package br.com.cdb.java.grupo4.eightbankspring.dtos;

import lombok.Getter;

@Getter
public class AddressDTO {

    private String streetName;
    private String number;
    private String district;
    private String city;
    private String state;  //ENUM
    private String zipCode;
    private String addressComplement;


}
