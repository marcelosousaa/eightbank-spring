package br.com.cdb.java.grupo4.eightbankspring.controller;

import br.com.cdb.java.grupo4.eightbankspring.dtos.ClientDTO;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InvalidValueException;
import br.com.cdb.java.grupo4.eightbankspring.model.StandardResponse;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.usecase.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.InputMismatchException;
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

        try {
            long startTime = System.currentTimeMillis();

            clientService.addClient(clientDTO);

            ResponseEntity<StandardResponse> ok =
                    ResponseEntity.ok(StandardResponse.builder().message("Cliente cadastrado com sucesso!").build());

            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            LOGGER.info("Tempo decorrido: {} milissegundos.", timeElapsed);
            return ok;
        } catch (RuntimeException ex) {
            LOGGER.error("Erro de validação: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(StandardResponse.builder().message(ex.getMessage()).build());
        } catch (Exception ex) {
            LOGGER.error("Erro ao cadastrar cliente: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StandardResponse.builder().message("Erro ao cadastrar cliente."));
        }
    }

    @GetMapping("/{cpf}/accounts")
    public List<Account> showClientAccounts(@PathVariable String cpf) {
        return clientService.showClientAccounts(cpf);
    }
}
