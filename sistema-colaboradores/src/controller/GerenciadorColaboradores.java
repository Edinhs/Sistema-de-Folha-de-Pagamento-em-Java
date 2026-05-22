package controller;

import java.util.ArrayList;
import java.util.List;
import model.Colaborador;
import model.ColaboradorComissionado;
import model.ColaboradorPadrao;
import model.ColaboradorProducao;

/**
 * Controlador responsável por gerenciar a lista de colaboradores (ArrayList).
 * Provê métodos de consulta, adição e cálculo de estatísticas.
 */
public class GerenciadorColaboradores {
    private final List<Colaborador> colaboradores;

    public GerenciadorColaboradores() {
        this.colaboradores = new ArrayList<>();
    }

    /**
     * Adiciona um colaborador à lista.
     * Valida se a matrícula já está cadastrada para evitar duplicatas.
     * @param colaborador O colaborador a ser inserido.
     * @return boolean indicando sucesso na inserção.
     */
    public boolean adicionarColaborador(Colaborador colaborador) {
        if (colaborador == null) return false;
        
        // Validação de matrícula existente
        if (buscarPorMatricula(colaborador.getMatricula()) != null) {
            throw new IllegalArgumentException("Erro: Já existe um colaborador cadastrado com a matrícula " + colaborador.getMatricula() + ".");
        }
        
        return colaboradores.add(colaborador);
    }

    /**
     * Remove um colaborador com base na sua matrícula.
     * @param matricula Matrícula do colaborador.
     * @return boolean indicando se o colaborador foi encontrado e removido.
     */
    public boolean removerColaborador(String matricula) {
        Colaborador c = buscarPorMatricula(matricula);
        if (c != null) {
            return colaboradores.remove(c);
        }
        return false;
    }

    /**
     * Busca um colaborador pela matrícula.
     * @param matricula Matrícula a ser buscada.
     * @return Colaborador correspondente ou null caso não encontrado.
     */
    public Colaborador buscarPorMatricula(String matricula) {
        for (Colaborador c : colaboradores) {
            if (c.getMatricula().equalsIgnoreCase(matricula.trim())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Retorna a lista completa de colaboradores.
     * @return List de Colaboradores.
     */
    public List<Colaborador> getColaboradores() {
        return new ArrayList<>(colaboradores); // Retorna uma cópia para preservar o encapsulamento
    }

    /**
     * Retorna a quantidade total de colaboradores cadastrados.
     * @return int total.
     */
    public int getQuantidadeTotal() {
        return colaboradores.size();
    }

    /**
     * Calcula o valor total de salários a serem pagos (Folha de Pagamento).
     * @return double total da folha.
     */
    public double calcularTotalFolha() {
        double total = 0.0;
        for (Colaborador c : colaboradores) {
            total += c.calcularSalarioFinal();
        }
        return total;
    }

    /**
     * Calcula o salário médio dos colaboradores.
     * @return double média salarial.
     */
    public double calcularMediaSalarial() {
        if (colaboradores.isEmpty()) return 0.0;
        return calcularTotalFolha() / colaboradores.size();
    }

    /**
     * Obtém a quantidade de colaboradores de um determinado tipo.
     * @param tipo Classe que estende Colaborador.
     * @return int contagem.
     */
    public int getQuantidadePorTipo(Class<? extends Colaborador> tipo) {
        int count = 0;
        for (Colaborador c : colaboradores) {
            if (tipo.isInstance(c)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Cadastra alguns colaboradores fictícios iniciais (Mock Data) 
     * para facilitar a demonstração inicial e visualização dos gráficos.
     */
    public void carregarDadosIniciais() {
        try {
            adicionarColaborador(new ColaboradorPadrao("101", "Ana Clara Silveira"));
            adicionarColaborador(new ColaboradorComissionado("102", "Lucas Henrique Souza", 12000.0, 6.5));
            adicionarColaborador(new ColaboradorProducao("103", "Beatriz Pinheiro", 1500, 0.40));
            adicionarColaborador(new ColaboradorComissionado("104", "Mariana Costa", 8500.0, 5.0));
            adicionarColaborador(new ColaboradorPadrao("105", "Gabriel Santos"));
        } catch (Exception e) {
            // Ignora se der erro de duplicidade ou validação no Mock
        }
    }
}
