package model;

/**
 * Representa a classe abstrata base para todos os colaboradores do sistema.
 * Atende aos requisitos de tipos de dados compostos e declaração de constantes.
 */
public abstract class Colaborador {
    // Constante para o salário base de todos os colaboradores
    public static final double SALARIO_BASE = 2000.0;
    
    private String matricula;
    private String nome;

    public Colaborador(String matricula, String nome) {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("A matrícula não pode ser vazia.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        this.matricula = matricula;
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("A matrícula não pode ser vazia.");
        }
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        this.nome = nome;
    }

    /**
     * Calcula o salário final do colaborador com base no seu tipo de vínculo.
     * @return double contendo o salário final.
     */
    public abstract double calcularSalarioFinal();

    /**
     * Obtém o valor dos extras (comissões, bônus de produtividade, etc.).
     * @return double contendo os valores extras além do salário base.
     */
    public abstract double getExtras();

    /**
     * Retorna uma representação legível do tipo de vínculo do colaborador.
     * @return String contendo o tipo de funcionário.
     */
    public abstract String getTipoVinculo();

    @Override
    public String toString() {
        return "Nome: " + nome + "\n" +
               "Matrícula: " + matricula + "\n" +
               "Salário Fixo: " + SALARIO_BASE + "\n" +
               getExtrasLabel() + ": " + getExtras() + "\n" +
               "Salário final: " + calcularSalarioFinal();
    }

    /**
     * Auxiliar para obter a legenda correta dos adicionais/extras.
     */
    protected abstract String getExtrasLabel();
}
