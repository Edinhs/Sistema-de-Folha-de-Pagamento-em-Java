package model;

/**
 * Representa um funcionário de produção que recebe o salário base mais bônus de produtividade.
 * Bônus = (valorPorPeça * quantidadeProduzida)
 */
public class ColaboradorProducao extends Colaborador {
    private int quantidadeProduzida;
    private double valorPeca;

    public ColaboradorProducao(String matricula, String nome, int quantidadeProduzida, double valorPeca) {
        super(matricula, nome);
        setQuantidadeProduzida(quantidadeProduzida);
        setValorPeca(valorPeca);
    }

    public int getQuantidadeProduzida() {
        return quantidadeProduzida;
    }

    public void setQuantidadeProduzida(int quantidadeProduzida) {
        if (quantidadeProduzida < 0) {
            throw new IllegalArgumentException("A quantidade produzida não pode ser negativa.");
        }
        this.quantidadeProduzida = quantidadeProduzida;
    }

    public double getValorPeca() {
        return valorPeca;
    }

    public void setValorPeca(double valorPeca) {
        if (valorPeca < 0) {
            throw new IllegalArgumentException("O valor da peça não pode ser negativo.");
        }
        this.valorPeca = valorPeca;
    }

    @Override
    public double calcularSalarioFinal() {
        return SALARIO_BASE + getExtras();
    }

    @Override
    public double getExtras() {
        return valorPeca * quantidadeProduzida;
    }

    @Override
    public String getTipoVinculo() {
        return "Funcionário de Produção";
    }

    @Override
    protected String getExtrasLabel() {
        return "Produtividade";
    }
}
