package br.com.cdb.java.grupo4.eightbankspring.model.client;

import br.com.cdb.java.grupo4.eightbankspring.enuns.ClientCategory;

import java.time.LocalDate;

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public ClientCategory getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(ClientCategory clientCategory) {
        this.clientCategory = clientCategory;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getGrossMonthlyIncome() {
        return grossMonthlyIncome;
    }

    public void setGrossMonthlyIncome(double grossMonthlyIncome) {
        this.grossMonthlyIncome = grossMonthlyIncome;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
