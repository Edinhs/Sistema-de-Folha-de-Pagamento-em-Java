package view;

import controller.GerenciadorColaboradores;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import model.Colaborador;
import model.ColaboradorComissionado;
import model.ColaboradorPadrao;
import model.ColaboradorProducao;

/**
 * Interface interativa via Console (CLI) para o sistema de colaboradores.
 * Utiliza Scanner, estruturas de repetição (do-while), seleção (switch) e decisão (if-else).
 */
public class SistemaConsole {
    private final GerenciadorColaboradores gerenciador;
    private final Scanner scanner;

    public SistemaConsole(GerenciadorColaboradores gerenciador) {
        this.gerenciador = gerenciador;
        // Forçar localidade americana ou brasileira para leitura de decimais
        this.scanner = new Scanner(System.in).useLocale(new Locale("pt", "BR"));
    }

    /**
     * Inicia o loop principal do menu no console.
     */
    public void iniciar() {
        int opcao = -1;

        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    cadastrarPadrao();
                    break;
                case 2:
                    cadastrarComissionado();
                    break;
                case 3:
                    cadastrarProducao();
                    break;
                case 4:
                    gerarFolhaPagamento();
                    break;
                case 0:
                    System.out.println("\n>>> Encerrando o sistema console. Obrigado!");
                    break;
                default:
                    System.out.println("\n[!] Opção inválida. Digite um número de 0 a 4.");
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("\n========================================");
        System.out.println("  SISTEMA DE GESTÃO DE COLABORADORES   ");
        System.out.println("========================================");
        System.out.println("1 - Cadastrar Funcionário Padrão");
        System.out.println("2 - Cadastrar Funcionário Comissionado");
        System.out.println("3 - Cadastrar Funcionário Produção");
        System.out.println("4 - Gerar Folha de Pagamento");
        System.out.println("0 - Sair do Programa");
        System.out.println("========================================");
    }

    private void cadastrarPadrao() {
        System.out.println("\n--- Cadastro: Funcionário Padrão ---");
        String nome = lerTexto("Nome: ");
        String matricula = lerMatriculaNaoRepetida();

        try {
            Colaborador c = new ColaboradorPadrao(matricula, nome);
            gerenciador.adicionarColaborador(c);
            System.out.println("\n[✓] Funcionário Padrão cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[!] Erro: " + e.getMessage());
        }
    }

    private void cadastrarComissionado() {
        System.out.println("\n--- Cadastro: Funcionário Comissionado ---");
        String nome = lerTexto("Nome: ");
        String matricula = lerMatriculaNaoRepetida();
        double vendas = lerDoublePositivo("Informe valor das vendas: ");
        double comissao = lerDoublePositivo("Informe comissão percentual (%): ");

        try {
            Colaborador c = new ColaboradorComissionado(matricula, nome, vendas, comissao);
            gerenciador.adicionarColaborador(c);
            System.out.println("\n[✓] Funcionário Comissionado cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[!] Erro: " + e.getMessage());
        }
    }

    private void cadastrarProducao() {
        System.out.println("\n--- Cadastro: Funcionário Produção ---");
        String nome = lerTexto("Nome: ");
        String matricula = lerMatriculaNaoRepetida();
        int qtdePecas = lerInteiroPositivo("Informe qtde de peças: ");
        double valorPeca = lerDoublePositivo("Informe valor da peça: ");

        try {
            Colaborador c = new ColaboradorProducao(matricula, nome, qtdePecas, valorPeca);
            gerenciador.adicionarColaborador(c);
            System.out.println("\n[✓] Funcionário de Produção cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[!] Erro: " + e.getMessage());
        }
    }

    private void gerarFolhaPagamento() {
        System.out.println("\n========================================");
        System.out.println("          FOLHA DE PAGAMENTO            ");
        System.out.println("========================================");
        
        int totalCadastrados = gerenciador.getQuantidadeTotal();
        System.out.println("Total de pessoas cadastradas: " + totalCadastrados);
        
        if (totalCadastrados == 0) {
            System.out.println("\nNenhum colaborador cadastrado no sistema.");
            System.out.println("========================================");
            return;
        }

        for (Colaborador c : gerenciador.getColaboradores()) {
            System.out.println();
            System.out.println(c.toString());
        }
        
        System.out.println("\n----------------------------------------");
        System.out.printf("Valor Total da Folha: R$ %.2f\n", gerenciador.calcularTotalFolha());
        System.out.printf("Média Salarial Geral: R$ %.2f\n", gerenciador.calcularMediaSalarial());
        System.out.println("========================================");
    }

    // --- Métodos Auxiliares de Leitura com Validação robusta ---

    private String lerTexto(String prompt) {
        System.out.print(prompt);
        String texto = scanner.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.println("[!] A entrada não pode estar em branco.");
            System.out.print(prompt);
            texto = scanner.nextLine().trim();
        }
        return texto;
    }

    private String lerMatriculaNaoRepetida() {
        while (true) {
            String matricula = lerTexto("Matrícula: ");
            if (gerenciador.buscarPorMatricula(matricula) != null) {
                System.out.println("[!] Matrícula já cadastrada. Informe um valor diferente.");
            } else {
                return matricula;
            }
        }
    }

    private int lerInteiro(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int valor = scanner.nextInt();
                scanner.nextLine(); // limpar buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("[!] Entrada inválida. Digite um número inteiro.");
                scanner.nextLine(); // limpar buffer
            }
        }
    }

    private int lerInteiroPositivo(String prompt) {
        while (true) {
            int valor = lerInteiro(prompt);
            if (valor < 0) {
                System.out.println("[!] Erro: O valor não pode ser negativo.");
            } else {
                return valor;
            }
        }
    }

    private double lerDoublePositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                // Tenta ler com vírgula ou ponto dependendo do local
                String entrada = scanner.nextLine().trim().replace(',', '.');
                double valor = Double.parseDouble(entrada);
                if (valor < 0) {
                    System.out.println("[!] Erro: O valor não pode ser negativo.");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Entrada inválida. Digite um número decimal (ex.: 1500.50 ou 1500,50).");
            }
        }
    }
}
