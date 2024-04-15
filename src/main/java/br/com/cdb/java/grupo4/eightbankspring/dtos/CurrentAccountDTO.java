package br.com.cdb.java.grupo4.eightbankspring.dtos;

import br.com.cdb.java.grupo4.eightbankspring.enuns.AccountType;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CurrentAccountDTO {

    private long accountNumber;
    private double balance;
    private String accountType;
    private double accountFee;
    private String ownerCpf;
}
