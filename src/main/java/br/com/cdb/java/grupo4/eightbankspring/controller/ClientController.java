package br.com.cdb.java.grupo4.eightbankspring.controller;

import br.com.cdb.java.grupo4.eightbankspring.dtos.ClientDTO;
import br.com.cdb.java.grupo4.eightbankspring.model.StandardResponse;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import br.com.cdb.java.grupo4.eightbankspring.usecase.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")

@Service
public class ClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    ClientService clientService;

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestBody ClientDTO clientDTO) {
        long startTime = System.currentTimeMillis();

        Client convertedClient = convertToEntity(clientDTO);
        clientService.addClient(convertedClient);

        ResponseEntity<StandardResponse> ok =
                ResponseEntity.ok(StandardResponse.builder().message("Cliente cadastrado com sucesso!").build());

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        LOGGER.info("Tempo decorrido: " + timeElapsed + " milissegundos.");
        return ok;
    }

    // Conversao de ObjetosDTO
    private Client convertToEntity(ClientDTO clientDTO) {
        Client client = new Client();
        BeanUtils.copyProperties(clientDTO, client);
        return client;
    }

    @GetMapping("/all")
    public List<Client> getAllClients() {
        return clientService.getClients();
    }

    @GetMapping("/{cpf}/accounts")
    public List<Account> showClientAccounts(@PathVariable String cpf) {
        return clientService.listClientAccounts(cpf);
    }
}
