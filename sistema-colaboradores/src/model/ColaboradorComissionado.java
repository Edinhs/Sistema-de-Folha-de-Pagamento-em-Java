package model;

/**
 * Representa um funcionário comissionado que recebe o salário base mais comissão.
 * Comissão = (vendas * percentual / 100)
 */
public class ColaboradorComissionado extends Colaborador {
    private double valorVendas;
    private double percentualComissao;

    public ColaboradorComissionado(String matricula, String nome, double valorVendas, double percentualComissao) {
        super(matricula, nome);
        setValorVendas(valorVendas);
        setPercentualComissao(percentualComissao);
    }

    public double getValorVendas() {
        return valorVendas;
    }

    public void setValorVendas(double valorVendas) {
        if (valorVendas < 0) {
            throw new IllegalArgumentException("O valor total de vendas não pode ser negativo.");
        }
        this.valorVendas = valorVendas;
    }

    public double getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(double percentualComissao) {
        if (percentualComissao < 0) {
            throw new IllegalArgumentException("O percentual de comissão não pode ser negativo.");
        }
        this.percentualComissao = percentualComissao;
    }

    @Override
    public double calcularSalarioFinal() {
        return SALARIO_BASE + getExtras();
    }

    @Override
    public double getExtras() {
        return (valorVendas * percentualComissao) / 100.0;
    }

    @Override
    public String getTipoVinculo() {
        return "Funcionário Comissionado";
    }

    @Override
    protected String getExtrasLabel() {
        return "Comissão";
    }
}
