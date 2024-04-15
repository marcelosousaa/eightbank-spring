package br.com.cdb.java.grupo4.eightbankspring.dtos;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@Getter
public class SavingsAccountDTO {

    private long accountNumber;
    private BigDecimal balance;
    private String accountType;
    private BigDecimal savingsAccountAnnualPercentageYield;
    private String ownerCpf;
}
