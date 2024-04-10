package br.com.cdb.java.grupo4.eightbankspring.usecase;

import br.com.cdb.java.grupo4.eightbankspring.dao.InsuranceDAO;
import br.com.cdb.java.grupo4.eightbankspring.enuns.ClientCategory;
import br.com.cdb.java.grupo4.eightbankspring.enuns.InsuranceType;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import br.com.cdb.java.grupo4.eightbankspring.model.insurance.Insurance;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@Service
public class InsuranceService {
    InsuranceDAO insuranceDAO = new InsuranceDAO();

    public void showInsuranceMenu(Client client) {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.println("\nSeguros contratados para esta conta:");
            List<Insurance> insurances = insuranceDAO.findByClientCPF(client.getCpf());
            if (insurances.isEmpty()) {
                System.out.println("Nenhum seguro contratado.");
            } else {
                int index = 1;
                for (Insurance insurance : insurances) {
                    System.out.println(index++ + " - " + insurance.getInsuranceType().getInsuranceName()
                            + ": " + insurance.getPolicyConditions() + " - Valor: R$"
                            + insurance.getPolicyValue());
                }
            }

            System.out.println("\nEscolha uma opção:");
            System.out.println("1 - Contratar Seguros");
            System.out.println("2 - Desativar Seguros");
            System.out.println("0 - Voltar");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer do scanner

            switch (choice) {
                case 1:
                    contractInsurance(client);
                    break;
                case 2:
                    deactivateInsurance(client);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    }

    private void contractInsurance(Client client) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha o tipo de seguro para contratar:");
        System.out.println("1 - Seguro Viagem");
        System.out.println("2 - Seguro de Fraude");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (choice) {
            case 1:
                // Lógica para contratar Seguro Viagem
                if (client.getClientCategory() == ClientCategory.PREMIUM) {
                    // Cliente Premium - Seguro Viagem gratuito
                    Insurance travelInsurance = new Insurance(LocalDate.now(), 0, "Cobertura completa em viagens", InsuranceType.INSURANCE_TRAVEL, client.getCpf());
                    insuranceDAO.addInsurance(travelInsurance);
                    System.out.println("Seguro Viagem ativado gratuitamente para categoria Premium.");
                } else {
                    // Cliente Comum ou Super
                    Insurance travelInsurance = new Insurance(LocalDate.now(), 50, "Cobertura completa em viagens", InsuranceType.INSURANCE_TRAVEL, client.getCpf());
                    insuranceDAO.addInsurance(travelInsurance);
                    System.out.println("Seguro Viagem ativado. Taxa mensal de R$50,00.");
                }
                break;
            case 2:
                // Lógica para contratar Seguro de Fraude
                Insurance fraudInsurance = new Insurance(LocalDate.now(), 5000, "Cobertura contra fraudes em transações indesejadas", InsuranceType.INSURANCE_FRAUD, client.getCpf());
                insuranceDAO.addInsurance(fraudInsurance);
                System.out.println("Seguro contra fraude ativado com sucesso.");
                break;
            default:
                System.out.println("Opção inválida. Por favor, tente novamente.");
                break;
        }
    }

    private void deactivateInsurance(Client client) {
        Scanner scanner = new Scanner(System.in);
        List<Insurance> insurances = insuranceDAO.findByClientCPF(client.getCpf());

        if (insurances.isEmpty()) {
            System.out.println("Nenhum seguro contratado.");
            return;
        }

        System.out.println("Escolha o seguro para desativar:");
        for (int i = 0; i < insurances.size(); i++) {
            Insurance insurance = insurances.get(i);
            System.out.println((i + 1) + " - " + insurance.getInsuranceType().getInsuranceName() + " Número da apólice: " + insurance.getPolicyNumber());
        }

        int choice = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        if (choice < 1 || choice > insurances.size()) {
            System.out.println("Opção inválida.");
        } else {
            Insurance selectedInsurance = insurances.get(choice - 1);
            insuranceDAO.removeInsurance(selectedInsurance.getPolicyNumber());
            System.out.println("Seguro " + selectedInsurance.getInsuranceType().getInsuranceName() + " desativado com sucesso.");
        }
    }
}