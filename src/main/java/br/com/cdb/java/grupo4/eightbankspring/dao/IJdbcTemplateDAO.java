package br.com.cdb.java.grupo4.eightbankspring.dao;

import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import org.springframework.stereotype.Service;

@Service
public interface IJdbcTemplateDAO {
    void saveClient(Client client);
}
