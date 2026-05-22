package model;

/**
 * Representa um funcionário padrão que recebe apenas o salário base.
 */
public class ColaboradorPadrao extends Colaborador {

    public ColaboradorPadrao(String matricula, String nome) {
        super(matricula, nome);
    }

    @Override
    public double calcularSalarioFinal() {
        return SALARIO_BASE;
    }

    @Override
    public double getExtras() {
        return 0.0;
    }

    @Override
    public String getTipoVinculo() {
        return "Funcionário Padrão";
    }

    @Override
    protected String getExtrasLabel() {
        return "Extras";
    }
}
