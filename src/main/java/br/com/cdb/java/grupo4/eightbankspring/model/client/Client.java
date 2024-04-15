package br.com.cdb.java.grupo4.eightbankspring.model.client;

import br.com.cdb.java.grupo4.eightbankspring.enuns.ClientCategory;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private long id;
    private String email;
    private String password;
    private String name;
    private String cpf;
    private LocalDate dateOfBirth;
    private Address address;
    private ClientCategory clientCategory;
    private String phoneNumber;
    private double grossMonthlyIncome;

    public Client(
            String email,
            String password,
            String name,
            String cpf,
            LocalDate dateOfBirth,
            Address address,
            ClientCategory clientCategory,
            String phoneNumber,
            double grossMonthlyIncome
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.cpf = cpf;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.clientCategory = clientCategory;
        this.phoneNumber = phoneNumber;
        this.grossMonthlyIncome = grossMonthlyIncome;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address=" + address +
                ", clientCategory=" + clientCategory +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", grossMonthlyIncome=" + grossMonthlyIncome +
                '}';
    }
}
