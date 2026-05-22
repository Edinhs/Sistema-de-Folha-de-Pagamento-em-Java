import controller.GerenciadorColaboradores;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.SistemaConsole;
import view.SistemaGrafico;

/**
 * Ponto de entrada unificado para o Sistema de Cadastro e Folha de Pagamento.
 * Inicia a GUI Swing por padrão para uma experiência visual de alto nível,
 * mas permite inicializar o modo Console clássico via argumento '--console'.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Inicializar o Controlador compartilhado
        GerenciadorColaboradores gerenciador = new GerenciadorColaboradores();

        // 2. Verificar se foi solicitado o modo Console por argumento
        boolean modoConsole = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--console") || arg.equalsIgnoreCase("-c")) {
                modoConsole = true;
                break;
            }
        }

        if (modoConsole) {
            // Iniciar em modo Console interativo (CLI)
            System.out.println(">>> Iniciando sistema em modo Console CLI...");
            SistemaConsole console = new SistemaConsole(gerenciador);
            console.iniciar();
        } else {
            // Configurar visual nativo do sistema para a interface gráfica
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Se falhar, usa o padrão do Swing
            }

            // Iniciar a Interface Gráfica (GUI) moderna na thread do Swing
            SwingUtilities.invokeLater(() -> {
                System.out.println("==================================================");
                System.out.println(" SISTEMA DE FOLHA DE PAGAMENTO   ");
                System.out.println("==================================================");
                System.out.println("[+] Iniciando a Interface Gráfica Premium...");
                System.out.println("[i] Nota: Para rodar em modo texto acadêmico (CLI),");
                System.out.println("    execute novamente passando o argumento: --console");
                System.out.println("==================================================");

                SistemaGrafico gui = new SistemaGrafico(gerenciador);
                gui.setVisible(true);
            });
        }
    }
}
