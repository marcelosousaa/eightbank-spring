package br.com.cdb.java.grupo4.eightbankspring.config;

import br.com.cdb.java.grupo4.eightbankspring.dao.AccountDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.CardDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.ClientDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.InsuranceDAO;
import br.com.cdb.java.grupo4.eightbankspring.dtos.ClientDTO;
import br.com.cdb.java.grupo4.eightbankspring.model.insurance.Insurance;
import br.com.cdb.java.grupo4.eightbankspring.usecase.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public AccountService accountService(){
        return new AccountService();
    }

    @Bean
    public CardService cardService(){
        return new CardService();
    }

    @Bean
    public ClientService clientService(){
        return new ClientService();
    }

    @Bean
    public InsuranceService insuranceService(){
        return new InsuranceService();
    }


    @Bean
    public ClientDAO clientDAO(){
        return new ClientDAO();
    }

    @Bean
    public ClientDTO clientDTO(){
        return new ClientDTO();
    }

    @Bean
    public AccountDAO accountDAO(){
        return new AccountDAO();
    }

    @Bean
    public CardDAO cardDAO(){
        return new CardDAO();
    }

    @Bean
    public InsuranceDAO insuranceDAO(){
        return new InsuranceDAO();
    }
}
