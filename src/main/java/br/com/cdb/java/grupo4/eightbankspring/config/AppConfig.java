package br.com.cdb.java.grupo4.eightbankspring.config;

import br.com.cdb.java.grupo4.eightbankspring.dao.*;
import br.com.cdb.java.grupo4.eightbankspring.dao.impl.JdbcTemplateDAOImpl;
import br.com.cdb.java.grupo4.eightbankspring.dtos.ClientDTO;
import br.com.cdb.java.grupo4.eightbankspring.usecase.*;
import lombok.Value;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

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

    @Bean
    public JdbcTemplateDAOImpl jdbcTemplateDAO(){
        return new JdbcTemplateDAOImpl();
    }

    @Bean
    @ConfigurationProperties(prefix="datasource")
    public DataSource eightbankDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(eightbankDataSource());
    }
}
