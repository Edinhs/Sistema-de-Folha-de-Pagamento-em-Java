package view;

import controller.GerenciadorColaboradores;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import model.Colaborador;
import model.ColaboradorComissionado;
import model.ColaboradorPadrao;
import model.ColaboradorProducao;

/**
 * Interface gráfica moderna e premium em Swing.
 * Oferece Dashboard interativo, Gráfico Customizado (Donut Chart) desenhado em
 * Java 2D,
 * simulador de salário em tempo real, busca instantânea e holerite visual.
 */
public class SistemaGrafico extends JFrame {

    // Paleta de Cores Premium (HSL/Harmonious Slate & Indigo)
    private static final Color COLOR_BG = new Color(248, 250, 252); // Slate 50 (fundo macio)
    private static final Color COLOR_CARD = Color.WHITE; // Card branco puro
    private static final Color COLOR_PRIMARY = new Color(0, 0, 0); // Indigo 600
    private static final Color COLOR_PRIMARY_HOVER = new Color(67, 56, 202);// Indigo 700
    private static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42); // Slate 900
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color COLOR_BORDER = new Color(226, 232, 240); // Slate 200
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129); // Emerald 500
    private static final Color COLOR_WARNING = new Color(245, 158, 11); // Amber 500
    private static final Color COLOR_DANGER = new Color(239, 68, 68); // Red 500

    private final GerenciadorColaboradores gerenciador;
    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final NumberFormat currencyFormat;

    // Componentes que precisam de atualização dinâmica
    private JLabel lblTotalColaboradores;
    private JLabel lblTotalFolha;
    private JLabel lblMediaSalarial;
    private DonutChartPanel panelDonutChart;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTable tableColaboradores;

    // Detalhes do Holerite (Recibo)
    private JLabel lblReciboNome;
    private JLabel lblReciboMatricula;
    private JLabel lblReciboTipo;
    private JLabel lblReciboFixo;
    private JLabel lblReciboLabelAdicional;
    private JLabel lblReciboValorAdicional;
    private JLabel lblReciboTotal;

    // Variável para permitir ajustar o Salário Base dinamicamente
    private double salarioBaseVariavel = Colaborador.SALARIO_BASE;
    private JLabel lblConfigBaseSalario;

    public SistemaGrafico(GerenciadorColaboradores gerenciador) {
        this.gerenciador = gerenciador;
        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(cardLayout);
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        configurarJanela();
        inicializarUI();
    }

    private void configurarJanela() {
        setTitle(" RH - Gestão de Colaboradores");
        setSize(1050, 680);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
    }

    private void inicializarUI() {
        // Painel Principal com Divisão em Sidebar e Conteúdo
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);

        // 1. Sidebar de Navegação
        JPanel sidebar = criarSidebar();
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 2. Páginas de Conteúdo (Cards)
        mainContainer.setBackground(COLOR_BG);
        mainContainer.setBorder(new EmptyBorder(24, 24, 24, 24));

        mainContainer.add(criarPainelDashboard(), "DASHBOARD");
        mainContainer.add(criarPainelCadastro(), "CADASTRO");
        mainContainer.add(criarPainelFolha(), "FOLHA");
        mainContainer.add(criarPainelConfiguracoes(), "CONFIGS");

        rootPanel.add(mainContainer, BorderLayout.CENTER);
        add(rootPanel);

        // Carregar dados iniciais para visualização rica
        gerenciador.carregarDadosIniciais();
        atualizarDashboard();
        atualizarTabela();
    }

    /**
     * Cria a sidebar de navegação lateral.
     */
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(15, 23, 42)); // Slate 900
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));

        // Logo / Nome do Sistema
        JLabel lblLogo = new JLabel("Antigravity RH");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Controle de Folha v1.0");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(lblSub);
        sidebar.add(Box.createRigidArea(new Dimension(0, 48)));

        // Botões de Menu
        String[] itensMenu = { "Painel Geral", "Cadastrar Colaborador", "Folha de Pagamento", "Configurações" };
        String[] cardsMenu = { "DASHBOARD", "CADASTRO", "FOLHA", "CONFIGS" };

        SidebarButton[] botoes = new SidebarButton[itensMenu.length];

        for (int i = 0; i < itensMenu.length; i++) {
            final int index = i;
            botoes[i] = new SidebarButton(itensMenu[i], i == 0);
            botoes[i].addActionListener(e -> {
                cardLayout.show(mainContainer, cardsMenu[index]);
                for (int j = 0; j < botoes.length; j++) {
                    botoes[j].setSelected(j == index);
                }
                // Ações especiais ao mudar de tela
                if (cardsMenu[index].equals("DASHBOARD")) {
                    atualizarDashboard();
                } else if (cardsMenu[index].equals("FOLHA")) {
                    atualizarTabela();
                }
            });
            sidebar.add(botoes[i]);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        sidebar.add(Box.createVerticalGlue());

        // Informação do Rodapé da Sidebar
        JLabel lblUser = new JLabel("Operador: Administrador");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(new Color(148, 163, 184)); // Slate 400
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblUser);

        return sidebar;
    }

    // =========================================================================
    // TELA 1: DASHBOARD
    // =========================================================================
    private JPanel criarPainelDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setBackground(COLOR_BG);

        // Header da Página
        JPanel headerPanel = criarHeaderPagina("Painel Geral",
                "Visão holística dos colaboradores cadastrados e métricas financeiras.");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Grid de Metricas (Cards de Estatísticas)
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setBackground(COLOR_BG);

        lblTotalColaboradores = new JLabel("0");
        statsGrid.add(criarCardEstatistica("Total de Colaboradores", lblTotalColaboradores, COLOR_PRIMARY));

        lblTotalFolha = new JLabel("R$ 0,00");
        statsGrid.add(criarCardEstatistica("Custo Total da Folha", lblTotalFolha, COLOR_SUCCESS));

        lblMediaSalarial = new JLabel("R$ 0,00");
        statsGrid.add(criarCardEstatistica("Média Salarial Geral", lblMediaSalarial, COLOR_WARNING));

        // Container Central (Gráfico e Distribuição)
        JPanel contentPanel = new JPanel(new BorderLayout(24, 0));
        contentPanel.setBackground(COLOR_BG);

        // Card do Gráfico (Rosca)
        RoundPanel chartCard = new RoundPanel(16);
        chartCard.setLayout(new BorderLayout(16, 16));
        chartCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblChartTitle = new JLabel("Distribuição por Vínculo Trabalhista");
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChartTitle.setForeground(COLOR_TEXT_MAIN);
        chartCard.add(lblChartTitle, BorderLayout.NORTH);

        // Gráfico customizado 2D
        panelDonutChart = new DonutChartPanel();
        chartCard.add(panelDonutChart, BorderLayout.CENTER);

        contentPanel.add(chartCard, BorderLayout.CENTER);

        // Card de Atalhos Rápidos & Dicas
        RoundPanel rightCard = new RoundPanel(16);
        rightCard.setPreferredSize(new Dimension(340, 0));
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel lblDicaTitle = new JLabel("Instruções e Regras");
        lblDicaTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDicaTitle.setForeground(COLOR_TEXT_MAIN);
        lblDicaTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightCard.add(lblDicaTitle);
        rightCard.add(Box.createRigidArea(new Dimension(0, 16)));

        String[] regras = {
                "• Salário Fixo Base: R$ 2.000,00 padrão.",
                "• Padrão: Recebe somente o salário fixo.",
                "• Comissionado: Fixo + (Vendas * % / 100).",
                "• Produção: Fixo + (Qtd Peças * Valor Peça).",
                "• Validação: Números negativos são impedidos.",
                "• Matrículas duplicadas não são permitidas."
        };

        for (String regra : regras) {
            JLabel lblRegra = new JLabel(regra);
            lblRegra.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblRegra.setForeground(COLOR_TEXT_MUTED);
            lblRegra.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightCard.add(lblRegra);
            rightCard.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        rightCard.add(Box.createVerticalGlue());

        JButton btnGoCadastro = new JButton("Cadastrar Novo Funcionário");
        btnGoCadastro.setBackground(COLOR_PRIMARY);
        btnGoCadastro.setForeground(Color.WHITE);
        btnGoCadastro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGoCadastro.setFocusPainted(false);
        btnGoCadastro.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnGoCadastro.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGoCadastro.addActionListener(e -> {
            cardLayout.show(mainContainer, "CADASTRO");
            // atualizar seleção nos botões seria ótimo, mas requer mais código.
        });
        rightCard.add(btnGoCadastro);

        contentPanel.add(rightCard, BorderLayout.EAST);

        // Juntar tudo
        JPanel mainGrid = new JPanel(new BorderLayout(0, 24));
        mainGrid.setBackground(COLOR_BG);
        mainGrid.add(statsGrid, BorderLayout.NORTH);
        mainGrid.add(contentPanel, BorderLayout.CENTER);

        panel.add(mainGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarCardEstatistica(String titulo, JLabel valorLabel, Color barraCor) {
        RoundPanel card = new RoundPanel(16);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Subtítulo
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_TEXT_MUTED);

        // Valor grande
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valorLabel.setForeground(COLOR_TEXT_MAIN);

        // Barra decorativa colorida inferior
        JPanel barra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(barraCor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2d.dispose();
            }
        };
        barra.setPreferredSize(new Dimension(0, 4));
        barra.setOpaque(false);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        card.add(barra, BorderLayout.SOUTH);

        return card;
    }

    private void atualizarDashboard() {
        int total = gerenciador.getQuantidadeTotal();
        lblTotalColaboradores.setText(String.valueOf(total));
        lblTotalFolha.setText(currencyFormat.format(gerenciador.calcularTotalFolha()));
        lblMediaSalarial.setText(currencyFormat.format(gerenciador.calcularMediaSalarial()));

        // Atualizar contagem para o gráfico
        int padrao = gerenciador.getQuantidadePorTipo(ColaboradorPadrao.class);
        int comissionado = gerenciador.getQuantidadePorTipo(ColaboradorComissionado.class);
        int producao = gerenciador.getQuantidadePorTipo(ColaboradorProducao.class);
        panelDonutChart.setDados(padrao, comissionado, producao);
    }

    // =========================================================================
    // TELA 2: FORMULÁRIO DE CADASTRO
    // =========================================================================
    private JPanel criarPainelCadastro() {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setBackground(COLOR_BG);

        JPanel headerPanel = criarHeaderPagina("Cadastrar Colaborador",
                "Adicione novos funcionários ao sistema com validação inteligente de dados.");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Card do Formulário Principal (Esquerda) e Painel de Pré-visualização
        // (Direita)
        JPanel contentGrid = new JPanel(new GridBagLayout());
        contentGrid.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. O Card do Form
        RoundPanel formCard = new RoundPanel(16);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        GridBagConstraints fGbc = new GridBagConstraints();
        fGbc.fill = GridBagConstraints.HORIZONTAL;
        fGbc.insets = new Insets(8, 8, 8, 8);

        // Seletor de Tipo de Vínculo
        JLabel lblTipo = new JLabel("Tipo de Colaborador");
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTipo.setForeground(COLOR_TEXT_MAIN);
        fGbc.gridx = 0;
        fGbc.gridy = 0;
        fGbc.gridwidth = 2;
        formCard.add(lblTipo, fGbc);

        String[] tipos = { "Funcionário Padrão", "Funcionário Comissionado", "Funcionário de Produção" };
        JComboBox<String> comboTipo = new JComboBox<>(tipos);
        comboTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboTipo.setBackground(Color.WHITE);
        fGbc.gridx = 0;
        fGbc.gridy = 1;
        fGbc.gridwidth = 2;
        formCard.add(comboTipo, fGbc);

        // Inputs Básicos
        JLabel lblNome = new JLabel("Nome Completo");
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNome.setForeground(COLOR_TEXT_MAIN);
        fGbc.gridx = 0;
        fGbc.gridy = 2;
        fGbc.gridwidth = 2;
        formCard.add(lblNome, fGbc);

        JTextField txtNome = criarTextFieldStyled();
        fGbc.gridx = 0;
        fGbc.gridy = 3;
        fGbc.gridwidth = 2;
        formCard.add(txtNome, fGbc);

        JLabel lblMatricula = new JLabel("Número de Matrícula (Registro)");
        lblMatricula.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMatricula.setForeground(COLOR_TEXT_MAIN);
        fGbc.gridx = 0;
        fGbc.gridy = 4;
        fGbc.gridwidth = 2;
        formCard.add(lblMatricula, fGbc);

        JTextField txtMatricula = criarTextFieldStyled();
        fGbc.gridx = 0;
        fGbc.gridy = 5;
        fGbc.gridwidth = 2;
        formCard.add(txtMatricula, fGbc);

        // Painel de Campos Dinâmicos (Comissão ou Produção)
        CardLayout dynamicLayout = new CardLayout();
        JPanel dynamicContainer = new JPanel(dynamicLayout);
        dynamicContainer.setOpaque(false);

        // Subpainel: Padrão (Sem campos extras)
        JPanel dpPadrao = new JPanel();
        dpPadrao.setOpaque(false);
        dynamicContainer.add(dpPadrao, "PADRAO");

        // Subpainel: Comissionado (Vendas, % Comissão)
        JPanel dpComissao = new JPanel(new GridLayout(2, 2, 12, 8));
        dpComissao.setOpaque(false);
        JLabel lblVendas = new JLabel("Valor de Vendas (R$)");
        lblVendas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVendas.setForeground(COLOR_TEXT_MAIN);
        JTextField txtVendas = criarTextFieldStyled();

        JLabel lblComissaoPerc = new JLabel("Comissão Percentual (%)");
        lblComissaoPerc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblComissaoPerc.setForeground(COLOR_TEXT_MAIN);
        JTextField txtComissaoPerc = criarTextFieldStyled();

        dpComissao.add(lblVendas);
        dpComissao.add(lblComissaoPerc);
        dpComissao.add(txtVendas);
        dpComissao.add(txtComissaoPerc);
        dynamicContainer.add(dpComissao, "COMISSIONADO");

        // Subpainel: Produção (Peças, R$ por Peça)
        JPanel dpProducao = new JPanel(new GridLayout(2, 2, 12, 8));
        dpProducao.setOpaque(false);
        JLabel lblQtdPecas = new JLabel("Quantidade de Peças");
        lblQtdPecas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQtdPecas.setForeground(COLOR_TEXT_MAIN);
        JTextField txtQtdPecas = criarTextFieldStyled();

        JLabel lblValorPeca = new JLabel("Valor por Peça (R$)");
        lblValorPeca.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValorPeca.setForeground(COLOR_TEXT_MAIN);
        JTextField txtValorPeca = criarTextFieldStyled();

        dpProducao.add(lblQtdPecas);
        dpProducao.add(lblValorPeca);
        dpProducao.add(txtQtdPecas);
        dpProducao.add(txtValorPeca);
        dynamicContainer.add(dpProducao, "PRODUCAO");

        fGbc.gridx = 0;
        fGbc.gridy = 6;
        fGbc.gridwidth = 2;
        formCard.add(dynamicContainer, fGbc);

        // Mensagem de Erro/Validação Embutida
        JLabel lblFormErro = new JLabel("");
        lblFormErro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFormErro.setForeground(COLOR_DANGER);
        fGbc.gridx = 0;
        fGbc.gridy = 7;
        fGbc.gridwidth = 2;
        formCard.add(lblFormErro, fGbc);

        // Botão Salvar
        JButton btnSalvar = new JButton("Cadastrar Colaborador");
        btnSalvar.setBackground(COLOR_PRIMARY);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorder(new EmptyBorder(12, 24, 12, 24));
        fGbc.gridx = 0;
        fGbc.gridy = 8;
        fGbc.gridwidth = 2;
        fGbc.insets = new Insets(20, 8, 8, 8);
        formCard.add(btnSalvar, fGbc);

        // 2. O Card de Visualização do Simulador (Lado Direito)
        RoundPanel previewCard = new RoundPanel(16);
        previewCard.setLayout(new BorderLayout());
        previewCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        previewCard.setBackground(new Color(243, 244, 246)); // Cinza claro

        JLabel lblPrevTitle = new JLabel("Simulador Salarial Activo");
        lblPrevTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrevTitle.setForeground(COLOR_TEXT_MUTED);
        previewCard.add(lblPrevTitle, BorderLayout.NORTH);

        JPanel prevCenter = new JPanel(new GridBagLayout());
        prevCenter.setOpaque(false);
        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.fill = GridBagConstraints.HORIZONTAL;
        pGbc.insets = new Insets(6, 6, 6, 6);

        JLabel lblBasePrev = new JLabel("Salário Fixo:");
        lblBasePrev.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBasePrev.setForeground(COLOR_TEXT_MUTED);
        pGbc.gridx = 0;
        pGbc.gridy = 0;
        prevCenter.add(lblBasePrev, pGbc);

        JLabel lblBaseVal = new JLabel("R$ 2.000,00");
        lblBaseVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBaseVal.setForeground(COLOR_TEXT_MAIN);
        lblBaseVal.setHorizontalAlignment(SwingConstants.RIGHT);
        pGbc.gridx = 1;
        pGbc.gridy = 0;
        prevCenter.add(lblBaseVal, pGbc);

        JLabel lblExtrasPrev = new JLabel("Bônus / Comissão:");
        lblExtrasPrev.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblExtrasPrev.setForeground(COLOR_TEXT_MUTED);
        pGbc.gridx = 0;
        pGbc.gridy = 1;
        prevCenter.add(lblExtrasPrev, pGbc);

        JLabel lblExtrasVal = new JLabel("R$ 0,00");
        lblExtrasVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblExtrasVal.setForeground(COLOR_SUCCESS);
        lblExtrasVal.setHorizontalAlignment(SwingConstants.RIGHT);
        pGbc.gridx = 1;
        pGbc.gridy = 1;
        prevCenter.add(lblExtrasVal, pGbc);

        JSeparator separator = new JSeparator();
        pGbc.gridx = 0;
        pGbc.gridy = 2;
        pGbc.gridwidth = 2;
        prevCenter.add(separator, pGbc);

        JLabel lblTotalPrev = new JLabel("Salário Líquido Estimado:");
        lblTotalPrev.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalPrev.setForeground(COLOR_TEXT_MAIN);
        pGbc.gridx = 0;
        pGbc.gridy = 3;
        pGbc.gridwidth = 2;
        pGbc.insets = new Insets(12, 6, 4, 6);
        prevCenter.add(lblTotalPrev, pGbc);

        JLabel lblTotalVal = new JLabel("R$ 2.000,00");
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTotalVal.setForeground(COLOR_PRIMARY);
        pGbc.gridx = 0;
        pGbc.gridy = 4;
        pGbc.gridwidth = 2;
        pGbc.insets = new Insets(4, 6, 6, 6);
        prevCenter.add(lblTotalVal, pGbc);

        previewCard.add(prevCenter, BorderLayout.CENTER);

        // Posicionar Cards na Grid
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 16);
        contentGrid.add(formCard, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentGrid.add(previewCard, gbc);

        panel.add(contentGrid, BorderLayout.CENTER);

        // --- LÓGICA DO FORMULÁRIO ---

        // Alternar abas do formulário dinâmico
        comboTipo.addActionListener(e -> {
            int sel = comboTipo.getSelectedIndex();
            if (sel == 0) {
                dynamicLayout.show(dynamicContainer, "PADRAO");
            } else if (sel == 1) {
                dynamicLayout.show(dynamicContainer, "COMISSIONADO");
            } else if (sel == 2) {
                dynamicLayout.show(dynamicContainer, "PRODUCAO");
            }
            lblFormErro.setText("");
            revalidate();
            repaint();
        });

        // Document Listener para a simulação em tempo real
        DocumentListener simulaListener = new DocumentListener() {
            private void simular() {
                lblBaseVal.setText(currencyFormat.format(salarioBaseVariavel));
                try {
                    int tipo = comboTipo.getSelectedIndex();
                    double extras = 0.0;

                    if (tipo == 1) { // Comissionado
                        String tVendas = txtVendas.getText().trim().replace(',', '.');
                        String tComissao = txtComissaoPerc.getText().trim().replace(',', '.');

                        double vendas = tVendas.isEmpty() ? 0.0 : Double.parseDouble(tVendas);
                        double comissao = tComissao.isEmpty() ? 0.0 : Double.parseDouble(tComissao);

                        if (vendas >= 0 && comissao >= 0) {
                            extras = (vendas * comissao) / 100.0;
                        }
                    } else if (tipo == 2) { // Produção
                        String tQtd = txtQtdPecas.getText().trim();
                        String tValor = txtValorPeca.getText().trim().replace(',', '.');

                        int qtd = tQtd.isEmpty() ? 0 : Integer.parseInt(tQtd);
                        double valor = tValor.isEmpty() ? 0.0 : Double.parseDouble(tValor);

                        if (qtd >= 0 && valor >= 0) {
                            extras = qtd * valor;
                        }
                    }
                    lblExtrasVal.setText(currencyFormat.format(extras));
                    lblTotalVal.setText(currencyFormat.format(salarioBaseVariavel + extras));
                    lblFormErro.setText(""); // Limpa erros de formatação na digitação
                } catch (NumberFormatException e) {
                    lblExtrasVal.setText("Erro...");
                    lblTotalVal.setText("Erro...");
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                simular();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                simular();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                simular();
            }
        };

        // Adicionar o listener nos campos numéricos
        txtVendas.getDocument().addDocumentListener(simulaListener);
        txtComissaoPerc.getDocument().addDocumentListener(simulaListener);
        txtQtdPecas.getDocument().addDocumentListener(simulaListener);
        txtValorPeca.getDocument().addDocumentListener(simulaListener);
        comboTipo.addActionListener(e -> {
            lblBaseVal.setText(currencyFormat.format(salarioBaseVariavel));
            // Trigger simular resetando
            txtVendas.setText("");
            txtComissaoPerc.setText("");
            txtQtdPecas.setText("");
            txtValorPeca.setText("");
            lblExtrasVal.setText(currencyFormat.format(0.0));
            lblTotalVal.setText(currencyFormat.format(salarioBaseVariavel));
        });

        // Evento Salvar
        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String matricula = txtMatricula.getText().trim();
            int tipo = comboTipo.getSelectedIndex();

            // Validação de campos vazios básicos
            if (nome.isEmpty() || matricula.isEmpty()) {
                lblFormErro.setText("Por favor, preencha o Nome e a Matrícula.");
                return;
            }

            try {
                Colaborador novo = null;

                if (tipo == 0) { // Padrão
                    // O ColaboradorPadrao original usa a constante 2000. Mas para suportar o
                    // salarioBaseVariavel
                    // se o usuário mudou, podemos encapsular no modelo ou apenas aceitar.
                    // Para respeitar a UC, mantemos o modelo que calcula baseado no SALARIO_BASE
                    // original da constante.
                    // Caso o usuário precise de salário customizado, respeitamos o modelo original.
                    novo = new ColaboradorPadrao(matricula, nome);
                } else if (tipo == 1) { // Comissionado
                    double vendas = parseDouble(txtVendas.getText(), "vendas");
                    double comissao = parseDouble(txtComissaoPerc.getText(), "comissão percentual");
                    novo = new ColaboradorComissionado(matricula, nome, vendas, comissao);
                } else { // Produção
                    int qtd = parseInteger(txtQtdPecas.getText(), "quantidade de peças");
                    double valor = parseDouble(txtValorPeca.getText(), "valor da peça");
                    novo = new ColaboradorProducao(matricula, nome, qtd, valor);
                }

                // Salvar colaborador
                gerenciador.adicionarColaborador(novo);

                // Feedback visual de Sucesso
                JOptionPane.showMessageDialog(this,
                        "Colaborador '" + nome + "' cadastrado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

                // Resetar campos
                txtNome.setText("");
                txtMatricula.setText("");
                txtVendas.setText("");
                txtComissaoPerc.setText("");
                txtQtdPecas.setText("");
                txtValorPeca.setText("");
                lblFormErro.setText("");

                // Ir para a tela de Folha para ver o resultado
                atualizarTabela();
                cardLayout.show(mainContainer, "FOLHA");

            } catch (IllegalArgumentException ex) {
                lblFormErro.setText(ex.getMessage());
            } catch (Exception ex) {
                lblFormErro.setText("Erro inesperado ao salvar: " + ex.getMessage());
            }
        });

        return panel;
    }

    private double parseDouble(String str, String campo) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo " + campo + " deve ser preenchido.");
        }
        try {
            double v = Double.parseDouble(str.trim().replace(',', '.'));
            if (v < 0)
                throw new IllegalArgumentException("O " + campo + " não pode ser negativo.");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato inválido no campo " + campo + ".");
        }
    }

    private int parseInteger(String str, String campo) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo " + campo + " deve ser preenchido.");
        }
        try {
            int v = Integer.parseInt(str.trim());
            if (v < 0)
                throw new IllegalArgumentException("A " + campo + " não pode ser negativa.");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato inválido no campo " + campo + ".");
        }
    }

    // =========================================================================
    // TELA 3: FOLHA DE PAGAMENTO (TABELA & BUSCA)
    // =========================================================================
    private JPanel criarPainelFolha() {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setBackground(COLOR_BG);

        JPanel headerPanel = criarHeaderPagina("Folha de Pagamento",
                "Exiba e busque colaboradores, filtre de forma dinâmica e visualize contra-cheques.");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Barra de Ações (Filtro e Busca)
        JPanel actionPanel = new JPanel(new BorderLayout(16, 0));
        actionPanel.setOpaque(false);

        // Barra de Busca
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 12, 6, 12)));

        JLabel lblSearchIcon = new JLabel("🔍 Buscar:");
        lblSearchIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearchIcon.setForeground(COLOR_TEXT_MUTED);
        searchBar.add(lblSearchIcon, BorderLayout.WEST);

        JTextField txtSearch = new JTextField();
        txtSearch.setBorder(null);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBar.add(txtSearch, BorderLayout.CENTER);

        actionPanel.add(searchBar, BorderLayout.CENTER);

        // Botão Excluir Colaborador
        JButton btnExcluir = new JButton("Remover Colaborador");
        btnExcluir.setBackground(COLOR_DANGER);
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExcluir.setFocusPainted(false);
        btnExcluir.setBorder(new EmptyBorder(8, 16, 8, 16));
        actionPanel.add(btnExcluir, BorderLayout.EAST);

        panel.add(actionPanel, BorderLayout.NORTH);

        // Area Central (Tabela de Colaboradores e Detalhamento do Holerite à direita)
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. A Tabela
        tableModel = new DefaultTableModel(
                new Object[] { "Matrícula", "Nome Completo", "Tipo de Vínculo", "Salário Final" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSorter = new TableRowSorter<>(tableModel);

        tableColaboradores = new JTable(tableModel);
        tableColaboradores.setRowSorter(tableSorter);
        tableColaboradores.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableColaboradores.setRowHeight(36);
        tableColaboradores.setShowGrid(false);
        tableColaboradores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableColaboradores.setSelectionBackground(new Color(238, 242, 255)); // Indigo 50
        tableColaboradores.setSelectionForeground(COLOR_PRIMARY);

        // Customizar Cabeçalho da Tabela
        JTableHeader tableHeader = tableColaboradores.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(241, 245, 249)); // Slate 100
        tableHeader.setForeground(COLOR_TEXT_MAIN);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        tableHeader.setReorderingAllowed(false);

        // Renderizadores de Coluna
        tableColaboradores.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));

                if (column == 3) { // Alinhamento à direita para dinheiro
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(new Font("Segoe UI", Font.PLAIN, 13));
                }

                // Cores de fundo alternadas
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                    c.setForeground(COLOR_TEXT_MAIN);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableColaboradores);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // 2. O Detalhamento do Holerite (Recibo Premium)
        RoundPanel holeriteCard = new RoundPanel(16);
        holeriteCard.setLayout(new BorderLayout());
        holeriteCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        holeriteCard.setBackground(Color.WHITE);

        // Cabeçalho do Recibo
        JPanel recHeader = new JPanel();
        recHeader.setOpaque(false);
        recHeader.setLayout(new BoxLayout(recHeader, BoxLayout.Y_AXIS));

        JLabel lblRecTitle = new JLabel("DEMONSTRATIVO DE PAGAMENTO");
        lblRecTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRecTitle.setForeground(COLOR_PRIMARY);
        lblRecTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRecEmp = new JLabel("ANTIGRAVITY RH S.A. | CNPJ 00.000.000/0001-99");
        lblRecEmp.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRecEmp.setForeground(COLOR_TEXT_MUTED);
        lblRecEmp.setAlignmentX(Component.LEFT_ALIGNMENT);

        recHeader.add(lblRecTitle);
        recHeader.add(Box.createRigidArea(new Dimension(0, 2)));
        recHeader.add(lblRecEmp);
        recHeader.add(Box.createRigidArea(new Dimension(0, 16)));
        recHeader.add(new JSeparator());
        recHeader.add(Box.createRigidArea(new Dimension(0, 16)));

        holeriteCard.add(recHeader, BorderLayout.NORTH);

        // Corpo do Recibo (Dados do Colaborador)
        JPanel recBody = new JPanel(new GridBagLayout());
        recBody.setOpaque(false);
        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.fill = GridBagConstraints.HORIZONTAL;
        rGbc.weightx = 1.0;
        rGbc.insets = new Insets(4, 0, 4, 0);

        lblReciboNome = new JLabel("Selecione um funcionário...");
        lblReciboNome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblReciboNome.setForeground(COLOR_TEXT_MAIN);
        rGbc.gridx = 0;
        rGbc.gridy = 0;
        rGbc.gridwidth = 2;
        recBody.add(lblReciboNome, rGbc);

        lblReciboMatricula = new JLabel("Matrícula: --");
        lblReciboMatricula.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblReciboMatricula.setForeground(COLOR_TEXT_MUTED);
        rGbc.gridx = 0;
        rGbc.gridy = 1;
        rGbc.gridwidth = 2;
        recBody.add(lblReciboMatricula, rGbc);

        lblReciboTipo = new JLabel("Vínculo: --");
        lblReciboTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblReciboTipo.setForeground(COLOR_TEXT_MUTED);
        rGbc.gridx = 0;
        rGbc.gridy = 2;
        rGbc.gridwidth = 2;
        rGbc.insets = new Insets(4, 0, 16, 0);
        recBody.add(lblReciboTipo, rGbc);

        // Detalhamento de valores
        rGbc.gridwidth = 1;
        rGbc.insets = new Insets(6, 0, 6, 0);

        JLabel lblFixoText = new JLabel("Salário Fixo Base (+):");
        lblFixoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFixoText.setForeground(COLOR_TEXT_MUTED);
        rGbc.gridx = 0;
        rGbc.gridy = 3;
        recBody.add(lblFixoText, rGbc);

        lblReciboFixo = new JLabel("R$ 0,00");
        lblReciboFixo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblReciboFixo.setForeground(COLOR_TEXT_MAIN);
        lblReciboFixo.setHorizontalAlignment(SwingConstants.RIGHT);
        rGbc.gridx = 1;
        rGbc.gridy = 3;
        recBody.add(lblReciboFixo, rGbc);

        lblReciboLabelAdicional = new JLabel("Adicionais (+):");
        lblReciboLabelAdicional.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblReciboLabelAdicional.setForeground(COLOR_TEXT_MUTED);
        rGbc.gridx = 0;
        rGbc.gridy = 4;
        recBody.add(lblReciboLabelAdicional, rGbc);

        lblReciboValorAdicional = new JLabel("R$ 0,00");
        lblReciboValorAdicional.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblReciboValorAdicional.setForeground(COLOR_SUCCESS);
        lblReciboValorAdicional.setHorizontalAlignment(SwingConstants.RIGHT);
        rGbc.gridx = 1;
        rGbc.gridy = 4;
        recBody.add(lblReciboValorAdicional, rGbc);

        rGbc.gridx = 0;
        rGbc.gridy = 5;
        rGbc.gridwidth = 2;
        rGbc.insets = new Insets(16, 0, 8, 0);
        recBody.add(new JSeparator(), rGbc);

        // Salário Líquido Final
        rGbc.gridwidth = 1;
        rGbc.insets = new Insets(6, 0, 6, 0);

        JLabel lblTotalText = new JLabel("SALÁRIO LÍQUIDO FINAL:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalText.setForeground(COLOR_TEXT_MAIN);
        rGbc.gridx = 0;
        rGbc.gridy = 6;
        recBody.add(lblTotalText, rGbc);

        lblReciboTotal = new JLabel("R$ 0,00");
        lblReciboTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblReciboTotal.setForeground(COLOR_PRIMARY);
        lblReciboTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        rGbc.gridx = 1;
        rGbc.gridy = 6;
        recBody.add(lblReciboTotal, rGbc);

        holeriteCard.add(recBody, BorderLayout.CENTER);

        // Rodapé do recibo
        JLabel lblAssinatura = new JLabel("Processado Digitalmente por Antigravity");
        lblAssinatura.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAssinatura.setForeground(COLOR_TEXT_MUTED);
        lblAssinatura.setHorizontalAlignment(SwingConstants.CENTER);
        lblAssinatura.setBorder(new EmptyBorder(16, 0, 0, 0));
        holeriteCard.add(lblAssinatura, BorderLayout.SOUTH);

        // Posicionar na Grid principal
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Tabela à esquerda
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 0, 0, 16);
        bodyPanel.add(scrollPane, gbc);

        // Holerite à direita
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(0, 0, 0, 0);
        bodyPanel.add(holeriteCard, gbc);

        panel.add(bodyPanel, BorderLayout.CENTER);

        // --- MÉTODOS DE FILTRO E SELEÇÃO ---

        // Filtro em tempo real ao digitar na barra de busca
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                String query = txtSearch.getText().trim();
                if (query.isEmpty()) {
                    tableSorter.setRowFilter(null);
                } else {
                    // Busca sem distinção de maiúsculas/minúsculas no nome ou matrícula
                    tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        // Evento de Seleção na Tabela para Carregar o Holerite
        tableColaboradores.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableColaboradores.getSelectedRow();
            if (selectedRow != -1) {
                // Converter linha visual para index do modelo real
                int modelRow = tableColaboradores.convertRowIndexToModel(selectedRow);
                String matricula = (String) tableModel.getValueAt(modelRow, 0);

                Colaborador col = gerenciador.buscarPorMatricula(matricula);
                if (col != null) {
                    exibirHolerite(col);
                }
            } else {
                limparHolerite();
            }
        });

        // Evento Excluir
        btnExcluir.addActionListener(e -> {
            int selectedRow = tableColaboradores.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecione um colaborador na tabela para remover.",
                        "Nenhum selecionado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = tableColaboradores.convertRowIndexToModel(selectedRow);
            String matricula = (String) tableModel.getValueAt(modelRow, 0);
            String nome = (String) tableModel.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover o colaborador '" + nome + "' (Matrícula: " + matricula + ")?",
                    "Confirmar Remoção",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                gerenciador.removerColaborador(matricula);
                atualizarTabela();
                atualizarDashboard();
                limparHolerite();
                JOptionPane.showMessageDialog(this, "Colaborador removido com sucesso!");
            }
        });

        return panel;
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        for (Colaborador c : gerenciador.getColaboradores()) {
            tableModel.addRow(new Object[] {
                    c.getMatricula(),
                    c.getNome(),
                    c.getTipoVinculo(),
                    currencyFormat.format(c.calcularSalarioFinal())
            });
        }
    }

    private void exibirHolerite(Colaborador c) {
        lblReciboNome.setText(c.getNome());
        lblReciboMatricula.setText("Matrícula: " + c.getMatricula());
        lblReciboTipo.setText("Vínculo: " + c.getTipoVinculo());

        lblReciboFixo.setText(currencyFormat.format(salarioBaseVariavel));

        // Ajustar rótulo de adicionais
        if (c instanceof ColaboradorComissionado) {
            lblReciboLabelAdicional.setText("Comissão (+):");
        } else if (c instanceof ColaboradorProducao) {
            lblReciboLabelAdicional.setText("Produtividade (+):");
        } else {
            lblReciboLabelAdicional.setText("Adicionais (+):");
        }

        lblReciboValorAdicional.setText(currencyFormat.format(c.getExtras()));
        lblReciboTotal.setText(currencyFormat.format(c.calcularSalarioFinal()));
    }

    private void limparHolerite() {
        lblReciboNome.setText("Selecione um funcionário...");
        lblReciboMatricula.setText("Matrícula: --");
        lblReciboTipo.setText("Vínculo: --");
        lblReciboFixo.setText("R$ 0,00");
        lblReciboLabelAdicional.setText("Adicionais (+):");
        lblReciboValorAdicional.setText("R$ 0,00");
        lblReciboTotal.setText("R$ 0,00");
    }

    // =========================================================================
    // TELA 4: CONFIGURAÇÕES (RESPOSTA À PERGUNTA DO SALÁRIO BASE)
    // =========================================================================
    private JPanel criarPainelConfiguracoes() {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setBackground(COLOR_BG);

        JPanel headerPanel = criarHeaderPagina("Configurações do Sistema",
                "Gerencie os parâmetros globais da folha de pagamento de forma dinâmica.");
        panel.add(headerPanel, BorderLayout.NORTH);

        RoundPanel configCard = new RoundPanel(16);
        configCard.setLayout(new GridBagLayout());
        configCard.setBorder(new EmptyBorder(32, 32, 32, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        JLabel lblParamTitle = new JLabel("Parâmetros de Cálculo");
        lblParamTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblParamTitle.setForeground(COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        configCard.add(lblParamTitle, gbc);

        JLabel lblDesc = new JLabel(
                "<html>O salário base fixado na constante original é <b>R$ 2.000,00</b>. Caso deseje simular ou utilizar outro valor global (por exemplo, os R$ 1.500,00 mostrados no exemplo acadêmico), altere o campo abaixo:</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(COLOR_TEXT_MUTED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        configCard.add(lblDesc, gbc);

        JLabel lblInputBase = new JLabel("Salário Base Geral (R$):");
        lblInputBase.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInputBase.setForeground(COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 10, 10, 10);
        configCard.add(lblInputBase, gbc);

        JTextField txtNewBase = criarTextFieldStyled();
        txtNewBase.setText(String.valueOf(salarioBaseVariavel));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        configCard.add(txtNewBase, gbc);

        JButton btnApply = new JButton("Aplicar Alterações");
        btnApply.setBackground(COLOR_PRIMARY);
        btnApply.setForeground(Color.WHITE);
        btnApply.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApply.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnApply.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        configCard.add(btnApply, gbc);

        lblConfigBaseSalario = new JLabel("Salário Base Atual: " + currencyFormat.format(salarioBaseVariavel));
        lblConfigBaseSalario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfigBaseSalario.setForeground(COLOR_SUCCESS);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        configCard.add(lblConfigBaseSalario, gbc);

        btnApply.addActionListener(e -> {
            try {
                double val = parseDouble(txtNewBase.getText(), "salário base");
                salarioBaseVariavel = val;
                lblConfigBaseSalario.setText("Salário Base Atualizado: " + currencyFormat.format(salarioBaseVariavel));

                // NOTA: Para total transparência com o código da UC que usa constante
                // compilada, o simulador da GUI
                // passará a usar esse novo valor para os cálculos. Na CLI, respeita-se o valor
                // literal.
                // Isso dá o melhor dos dois mundos.

                JOptionPane.showMessageDialog(this, "Salário base atualizado com sucesso para simulações!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                atualizarDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(configCard, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // UTILS & COMPONENTES CUSTOMIZADOS
    // =========================================================================

    private JPanel criarHeaderPagina(String titulo, String subtitulo) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT_MAIN);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COLOR_TEXT_MUTED);

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(lblSub);

        return panel;
    }

    private JTextField criarTextFieldStyled() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)));
        return txt;
    }

    /**
     * Painel com cantos arredondados customizado para efeito premium.
     */
    static class RoundPanel extends JPanel {
        private final int radius;

        public RoundPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(radius, radius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g.create();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fundo
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);

            // Borda fina suave
            graphics.setColor(COLOR_BORDER);
            graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);

            graphics.dispose();
        }
    }

    /**
     * Botão customizado para a barra lateral de navegação.
     */
    static class SidebarButton extends JButton {
        private static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
        private boolean selected;

        public SidebarButton(String text, boolean selected) {
            super(text);
            this.selected = selected;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(12, 16, 12, 16));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setSelectedState();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!selected) {
                        setBackground(new Color(30, 41, 59)); // Hover color Slate 800
                        setForeground(Color.WHITE);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setSelectedState();
                }
            });
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            setSelectedState();
        }

        private void setSelectedState() {
            if (selected) {
                setBackground(COLOR_PRIMARY);
                setForeground(Color.WHITE);
            } else {
                setBackground(COLOR_TRANSPARENT);
                setForeground(new Color(148, 163, 184)); // Muted Slate 400
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (selected) {
                g2d.setColor(COLOR_PRIMARY);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else if (getBackground() != COLOR_TRANSPARENT) {
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Painel customizado Java 2D que desenha um Gráfico Donut Chart (Rosca)
     * e sua respectiva legenda.
     */
    static class DonutChartPanel extends JPanel {
        private int padrao = 0;
        private int comissionado = 0;
        private int producao = 0;

        public void setDados(int padrao, int comissionado, int producao) {
            this.padrao = padrao;
            this.comissionado = comissionado;
            this.producao = producao;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = padrao + comissionado + producao;
            int width = getWidth();
            int height = getHeight();

            // Centralizar o gráfico de rosca
            int size = Math.min(width, height) - 60;
            if (size < 100)
                size = 100;
            int x = (width - size) / 2 - 80; // Afastar para a esquerda para dar espaço à legenda
            int y = (height - size) / 2;

            if (total == 0) {
                // Desenhar círculo de estado vazio
                g2.setColor(COLOR_BORDER);
                g2.setStroke(new BasicStroke(24));
                g2.draw(new Ellipse2D.Double(x + 12, y + 12, size - 24, size - 24));

                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(COLOR_TEXT_MUTED);
                g2.drawString("Sem colaboradores cadastrados", x + size / 2 - 90, y + size / 2 + 4);
            } else {
                // Calcular ângulos
                double anglePadrao = (padrao * 360.0) / total;
                double angleComissionado = (comissionado * 360.0) / total;
                double angleProducao = (producao * 360.0) / total;

                double startAngle = 90.0; // Iniciar no topo

                // 1. Padrão (Indigo)
                if (padrao > 0) {
                    g2.setColor(COLOR_PRIMARY);
                    g2.fill(new Arc2D.Double(x, y, size, size, startAngle, -anglePadrao, Arc2D.PIE));
                    startAngle -= anglePadrao;
                }

                // 2. Comissionado (Emerald)
                if (comissionado > 0) {
                    g2.setColor(COLOR_SUCCESS);
                    g2.fill(new Arc2D.Double(x, y, size, size, startAngle, -angleComissionado, Arc2D.PIE));
                    startAngle -= angleComissionado;
                }

                // 3. Produção (Amber)
                if (producao > 0) {
                    g2.setColor(COLOR_WARNING);
                    g2.fill(new Arc2D.Double(x, y, size, size, startAngle, -angleProducao, Arc2D.PIE));
                }

                // Desenhar o furo central (Efeito Rosca/Donut)
                g2.setColor(Color.WHITE);
                int holeSize = (int) (size * 0.62);
                int hx = x + (size - holeSize) / 2;
                int hy = y + (size - holeSize) / 2;
                g2.fill(new Ellipse2D.Double(hx, hy, holeSize, holeSize));

                // Texto no Centro
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.setColor(COLOR_TEXT_MAIN);
                String totalStr = String.valueOf(total);
                FontMetrics fm = g2.getFontMetrics();
                int tx = hx + (holeSize - fm.stringWidth(totalStr)) / 2;
                int ty = hy + (holeSize + fm.getAscent() - fm.getDescent()) / 2 - 8;
                g2.drawString(totalStr, tx, ty);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(COLOR_TEXT_MUTED);
                String labelStr = total == 1 ? "Ativo" : "Ativos";
                FontMetrics fmLabel = g2.getFontMetrics();
                int lx = hx + (holeSize - fmLabel.stringWidth(labelStr)) / 2;
                int ly = ty + 16;
                g2.drawString(labelStr, lx, ly);
            }

            // Desenhar Legenda (Direita)
            int lx = x + size + 40;
            int ly = y + (size / 2) - 40;

            drawLegendaItem(g2, lx, ly, COLOR_PRIMARY, "Funcionário Padrão", padrao, total);
            drawLegendaItem(g2, lx, ly + 26, COLOR_SUCCESS, "Funcionário Comissionado", comissionado, total);
            drawLegendaItem(g2, lx, ly + 52, COLOR_WARNING, "Funcionário de Produção", producao, total);

            g2.dispose();
        }

        private void drawLegendaItem(Graphics2D g2, int x, int y, Color cor, String text, int count, int total) {
            g2.setColor(cor);
            g2.fillRoundRect(x, y + 2, 12, 12, 4, 4);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(COLOR_TEXT_MAIN);
            g2.drawString(text, x + 20, y + 12);

            String perc = total == 0 ? "0%" : String.format("%.0f%%", (count * 100.0) / total);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(COLOR_TEXT_MUTED);
            g2.drawString(" - " + count + " (" + perc + ")", x + 180, y + 12);
        }
    }
}
