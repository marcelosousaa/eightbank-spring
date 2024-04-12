package br.com.cdb.java.grupo4.eightbankspring.dao.impl;

import br.com.cdb.java.grupo4.eightbankspring.dao.IJdbcTemplateDAO;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class JdbcTemplateDAOImpl implements IJdbcTemplateDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveClient(Client client){

        try{
            String sql = "SELECT public.save_client (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.execute(sql, (PreparedStatementCallback<Void>) preparedStatement -> {
                preparedStatement.setString(1, client.getCpf());
                preparedStatement.setString(2, client.getName());
                preparedStatement.setString(3, client.getEmail());
                preparedStatement.setString(4, client.getPassword());
                preparedStatement.setDate(5, Date.valueOf(client.getDateOfBirth()));
                preparedStatement.setString(6, String.valueOf(client.getClientCategory()));
                preparedStatement.setString(7, client.getPhoneNumber());
                preparedStatement.setBigDecimal(8, BigDecimal.valueOf(client.getGrossMonthlyIncome()));
                preparedStatement.setString(9, client.getAddress().getStreetName());
                preparedStatement.setInt(10, Integer.parseInt(String.valueOf(client.getAddress().getNumber())));
                preparedStatement.setString(11, client.getAddress().getDistrict());
                preparedStatement.setString(12, client.getAddress().getCity());
                preparedStatement.setString(13, client.getAddress().getState());
                preparedStatement.setString(14, client.getAddress().getZipCode());
                preparedStatement.setString(15, client.getAddress().getAddressComplement());
                preparedStatement.execute();

                return null;
            });


//            String sql = """
//                    SELECT public.save_client(
//                    '22296607055',
//                    'Marcelo de Sousa Silva',
//                    'sousams93@outlook.com',
//                    '1000:c6102221b07eb25f4106e35eab644c0a:bfd31a0f36ee68ab4a1b6fe36f9cd8bc6ca70075e9f93ab987e499962e3e031eb9571aa7462c6cd3a487657f9fc6586abdfb5e29af7367527c25e95578d861cd',
//                    '2006-03-07',
//                    'COMMOM',
//                    '(11) 9999-9999',
//                    1000.0,
//                    'Rua Java',
//                    111,
//                    'Bay Area',
//                    'Los Angeles',
//                    'SP',
//                    '06186-120',
//                    'Some address complement'
//                    )""";
//
//            jdbcTemplate.execute(sql);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
