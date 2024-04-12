package br.com.cdb.java.grupo4.eightbankspring.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private long id;
    private String streetName;
    private long number;
    private String district;
    private String city;
    private String state;  //ENUM
    private String zipCode;
    private String addressComplement;

    public Address(String streetName, long number, String district, String city, String state, String zipCode) {
        this.streetName = streetName;
        this.number = number;
        this.district = district;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public Address(String streetName, long number, String district, String city, String state, String zipCode, String addressComplement) {
        this.streetName = streetName;
        this.number = number;
        this.district = district;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.addressComplement = addressComplement;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setAddressComplement(String addressComplement) {
        this.addressComplement = addressComplement;
    }

    @Override
    public String toString() {
        return "Address{" +
                "streetName='" + streetName + '\'' +
                ", number=" + number +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
