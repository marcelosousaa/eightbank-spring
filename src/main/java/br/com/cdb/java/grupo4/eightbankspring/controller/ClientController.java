package br.com.cdb.java.grupo4.eightbankspring.controller;

import br.com.cdb.java.grupo4.eightbankspring.dtos.ClientDTO;
import br.com.cdb.java.grupo4.eightbankspring.dtos.CurrentAccountDTO;
import br.com.cdb.java.grupo4.eightbankspring.dtos.SavingsAccountDTO;
import br.com.cdb.java.grupo4.eightbankspring.model.StandardResponse;
import br.com.cdb.java.grupo4.eightbankspring.usecase.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/savings-accounts")
    public List<SavingsAccountDTO> showClientSavingsAccount(@RequestParam String cpf){
        List<SavingsAccountDTO> clientSavingsAccount;
        long startTime = System.currentTimeMillis();

        clientSavingsAccount = clientService.showClientSavingsAccounts(cpf);

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        LOGGER.info("Tempo decorrido: {} milissegundos.", timeElapsed);

        return clientSavingsAccount;
    }

    @GetMapping("/current-accounts")
    public List<CurrentAccountDTO> showClientCurrentAccount(@RequestParam String cpf){
        List<CurrentAccountDTO> clientCurrentAccounts;
        long startTime = System.currentTimeMillis();

        clientCurrentAccounts = clientService.showClientCurrentAccounts(cpf);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        LOGGER.info("Tempo decorrido: {} milissegundos.", timeElapsed);

        return clientCurrentAccounts;
    }

    @PatchMapping("/deposit")
    public ResponseEntity<?> depositOnAccount(@RequestParam long accountNumber, double value, String accountType){
        long startTime = System.currentTimeMillis();
        clientService.depositOnClientAccount(accountNumber, value, accountType);

        ResponseEntity<StandardResponse> ok =
                ResponseEntity.ok(StandardResponse.builder().message("Valor depositado com sucesso!").build());

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        LOGGER.info("Tempo decorrido: {} milissegundos.", timeElapsed);

        return ok;
    }
}
