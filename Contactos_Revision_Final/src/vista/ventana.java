package vista;

import controlador.logica_ventana;
import recursos.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ventana principal de Gestión de Contactos.
 * Usa la barra nativa de Windows para minimizar, maximizar y cerrar,
 * evitando símbolos especiales que puedan mostrarse como cuadros vacíos.
 */
public class ventana extends JFrame {
    private static final Color BG = new Color(246, 248, 251);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color MUTED = new Color(107, 114, 128);
    private static final Color BORDER = new Color(226, 232, 240);

    public static final String LISTA = "lista";
    public static final String FORM = "formulario";
    public static final String ESTADISTICAS = "estadisticas";

    public JTextField txt_nombres = new JTextField();
    public JTextField txt_telefono = new JTextField();
    public JTextField txt_email = new JTextField();
    public JTextField txt_buscar;
    public JComboBox<String> cmb_categoria = new JComboBox<>();
    public JComboBox<String> cmb_idioma = new JComboBox<>(new String[]{"Español", "English", "Français"});
    public JCheckBox chb_favorito = new JCheckBox();

    public JButton btn_add;
    public JButton btn_modificar;
    public JButton btn_eliminar;
    public JButton btn_exportar;
    public JButton btn_guardar;
    public JButton btn_cancelar;
    public JButton btn_nav_contactos;
    public JButton btn_nav_estadisticas;

    public JTable tablaContactos;
    public DefaultTableModel modeloTabla;
    public JLabel lblTotalContactos;
    public JLabel lblFavoritos;
    public JLabel lblEstado;
    public JLabel lblFormTitulo;
    public JLabel lblProgresoTitulo;
    public JLabel lblProgresoDetalle;
    public JProgressBar barraProgreso;

    private JLabel lblMarca;
    private JLabel lblTituloLista;
    private JLabel lblSubtitulo;
    private JLabel lblTituloEstadisticas;
    private JLabel lblTituloNav;
    private JLabel lblIdioma;
    private JLabel lblNombreCampo;
    private JLabel lblTelefonoCampo;
    private JLabel lblEmailCampo;
    private JLabel lblCategoriaCampo;
    private JLabel lblTituloTotal;
    private JLabel lblTituloFavoritos;
    private JLabel lblEstadoVacio;
    private JPanel cards;
    private CardLayout cardLayout;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ventana().setVisible(true));
    }

    public ventana() {
        super(I18n.t("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 700);
        setLocationRelativeTo(null);
        construirInterfaz();
        new logica_ventana(this);
        actualizarTextos();
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);

        root.add(crearBarraSuperior(), BorderLayout.NORTH);
        root.add(crearMenuLateral(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setBackground(BG);
        cards.setBorder(new EmptyBorder(28, 30, 28, 30));
        cards.add(crearPanelContactos(), LISTA);
        cards.add(crearPanelFormulario(), FORM);
        cards.add(crearPanelEstadisticas(), ESTADISTICAS);
        root.add(cards, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(WHITE);
        footer.setBorder(new EmptyBorder(9, 20, 9, 20));
        lblEstado = label("Listo", 11, Font.PLAIN, MUTED);
        footer.add(lblEstado, BorderLayout.WEST);
        root.add(footer, BorderLayout.SOUTH);
    }

    private JPanel crearBarraSuperior() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);
        top.setBorder(new EmptyBorder(14, 24, 14, 24));
        top.setPreferredSize(new Dimension(100, 64));

        lblMarca = label(I18n.t("app.title"), 17, Font.BOLD, TEXT);
        top.add(lblMarca, BorderLayout.WEST);

        JPanel language = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        language.setOpaque(false);
        lblIdioma = label(I18n.t("language"), 12, Font.PLAIN, MUTED);
        cmb_idioma.setFont(font(Font.PLAIN, 12));
        cmb_idioma.setPreferredSize(new Dimension(125, 32));
        language.add(lblIdioma);
        language.add(cmb_idioma);
        top.add(language, BorderLayout.EAST);
        return top;
    }

    private JPanel crearMenuLateral() {
        JPanel side = new JPanel();
        side.setBackground(WHITE);
        side.setPreferredSize(new Dimension(210, 100));
        side.setBorder(new EmptyBorder(26, 16, 22, 16));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        lblTituloNav = label(I18n.t("nav.section"), 10, Font.BOLD, MUTED);
        lblTituloNav.setBorder(new EmptyBorder(0, 8, 18, 0));

        btn_nav_contactos = navButton(I18n.t("nav.contacts"));
        btn_nav_estadisticas = navButton(I18n.t("nav.stats"));
        btn_nav_contactos.addActionListener(e -> mostrarLista());
        btn_nav_estadisticas.addActionListener(e -> mostrarEstadisticas());

        side.add(lblTituloNav);
        side.add(btn_nav_contactos);
        side.add(Box.createVerticalStrut(8));
        side.add(btn_nav_estadisticas);
        side.add(Box.createVerticalGlue());
        return side;
    }

    private JPanel crearPanelContactos() {
        JPanel panel = transparentPanel(new BorderLayout(0, 20));

        JPanel header = transparentPanel(new BorderLayout());
        JPanel titles = transparentPanel(new GridLayout(2, 1, 0, 4));
        lblTituloLista = label(I18n.t("home.title"), 27, Font.BOLD, TEXT);
        lblSubtitulo = label(I18n.t("home.subtitle"), 13, Font.PLAIN, MUTED);
        titles.add(lblTituloLista);
        titles.add(lblSubtitulo);
        btn_add = primaryButton(I18n.t("button.add"));
        header.add(titles, BorderLayout.WEST);
        header.add(btn_add, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel center = transparentPanel(new BorderLayout(0, 14));
        JPanel toolbar = transparentPanel(new BorderLayout(12, 0));
        txt_buscar = new HintTextField(I18n.t("search.placeholder"));
        txt_buscar.setFont(font(Font.PLAIN, 13));
        txt_buscar.setToolTipText(I18n.t("search.placeholder"));
        txt_buscar.setPreferredSize(new Dimension(280, 40));
        txt_buscar.setBorder(fieldBorder());
        toolbar.add(txt_buscar, BorderLayout.CENTER);

        JPanel actions = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        btn_modificar = secondaryButton(I18n.t("button.edit"));
        btn_eliminar = dangerButton(I18n.t("button.delete"));
        btn_exportar = secondaryButton(I18n.t("button.export"));
        actions.add(btn_modificar);
        actions.add(btn_eliminar);
        actions.add(btn_exportar);
        toolbar.add(actions, BorderLayout.EAST);
        center.add(toolbar, BorderLayout.NORTH);

        String[] columns = {I18n.t("table.name"), I18n.t("table.phone"), I18n.t("table.email"),
                I18n.t("table.category"), I18n.t("table.favorite")};
        modeloTabla = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaContactos = new JTable(modeloTabla);
        tablaContactos.setFont(font(Font.PLAIN, 12));
        tablaContactos.setRowHeight(40);
        tablaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaContactos.setAutoCreateRowSorter(true);
        tablaContactos.setShowVerticalLines(false);
        tablaContactos.setGridColor(BORDER);
        tablaContactos.setSelectionBackground(new Color(219, 234, 254));
        tablaContactos.setSelectionForeground(TEXT);
        tablaContactos.getTableHeader().setFont(font(Font.BOLD, 11));
        tablaContactos.getTableHeader().setBackground(new Color(249, 250, 251));
        tablaContactos.getTableHeader().setForeground(MUTED);
        tablaContactos.getTableHeader().setPreferredSize(new Dimension(0, 38));
        JScrollPane scroll = new JScrollPane(tablaContactos);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        center.add(scroll, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = transparentPanel(new BorderLayout());
        lblEstadoVacio = label(I18n.t("empty"), 11, Font.PLAIN, MUTED);
        bottom.add(lblEstadoVacio, BorderLayout.WEST);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel outer = transparentPanel(new BorderLayout(0, 20));
        JPanel top = transparentPanel(new BorderLayout());
        lblFormTitulo = label(I18n.t("form.new"), 25, Font.BOLD, TEXT);
        btn_cancelar = secondaryButton(I18n.t("button.cancel"));
        top.add(lblFormTitulo, BorderLayout.WEST);
        top.add(btn_cancelar, BorderLayout.EAST);
        outer.add(top, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(28, 30, 28, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(9, 8, 9, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        cmb_categoria.setFont(font(Font.PLAIN, 13));
        chb_favorito.setOpaque(false);
        chb_favorito.setFont(font(Font.PLAIN, 12));
        agregarCampo(card, g, 0, I18n.t("form.name"), txt_nombres, 0);
        agregarCampo(card, g, 1, I18n.t("form.phone"), txt_telefono, 1);
        agregarCampo(card, g, 2, I18n.t("form.email"), txt_email, 2);
        agregarCampo(card, g, 3, I18n.t("form.category"), cmb_categoria, 3);
        chb_favorito.setText(I18n.t("form.favorite"));
        g.gridx = 1; g.gridy = 4; g.weightx = 1;
        card.add(chb_favorito, g);
        btn_guardar = primaryButton(I18n.t("button.save"));
        JPanel saveRow = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        saveRow.add(btn_guardar);
        g.gridx = 1; g.gridy = 5;
        card.add(saveRow, g);
        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = transparentPanel(new BorderLayout(0, 22));
        lblTituloEstadisticas = label(I18n.t("nav.stats"), 27, Font.BOLD, TEXT);
        panel.add(lblTituloEstadisticas, BorderLayout.NORTH);

        JPanel content = transparentPanel(new BorderLayout());
        JPanel statsBody = new JPanel();
        statsBody.setOpaque(false);
        statsBody.setLayout(new BoxLayout(statsBody, BoxLayout.Y_AXIS));
        JPanel cardsStats = transparentPanel(new GridLayout(1, 2, 18, 0));
        lblTotalContactos = label("0", 34, Font.BOLD, BLUE);
        lblFavoritos = label("0", 34, Font.BOLD, BLUE);
        cardsStats.add(statCard(I18n.t("stats.total"), lblTotalContactos, true));
        cardsStats.add(statCard(I18n.t("stats.favorites"), lblFavoritos, false));
        cardsStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
        cardsStats.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsBody.add(cardsStats);
        statsBody.add(Box.createVerticalStrut(20));

        JPanel progressCard = new JPanel();
        progressCard.setLayout(new BoxLayout(progressCard, BoxLayout.Y_AXIS));
        progressCard.setBackground(WHITE);
        progressCard.setBorder(new EmptyBorder(22, 24, 22, 24));
        lblProgresoTitulo = label(I18n.t("stats.progress"), 14, Font.BOLD, TEXT);
        lblProgresoDetalle = label(I18n.t("stats.loading"), 11, Font.PLAIN, MUTED);
        lblProgresoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblProgresoDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressCard.add(lblProgresoTitulo);
        progressCard.add(Box.createVerticalStrut(14));
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setValue(0);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("0%");
        barraProgreso.setFont(font(Font.BOLD, 11));
        barraProgreso.setForeground(BLUE);
        barraProgreso.setBackground(new Color(229, 231, 235));
        barraProgreso.setPreferredSize(new Dimension(500, 22));
        barraProgreso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        barraProgreso.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressCard.add(barraProgreso);
        progressCard.add(Box.createVerticalStrut(10));
        progressCard.add(lblProgresoDetalle);
        progressCard.setPreferredSize(new Dimension(500, 145));
        progressCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
        progressCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsBody.add(progressCard);
        content.add(statsBody, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel statCard(String title, JLabel value, boolean total) {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel caption = label(title, 13, Font.PLAIN, MUTED);
        if (total) lblTituloTotal = caption; else lblTituloFavoritos = caption;
        card.add(caption);
        card.add(Box.createVerticalStrut(12));
        card.add(value);
        return card;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints g, int row, String text,
                              JComponent field, int fieldIndex) {
        JLabel caption = label(text, 12, Font.BOLD, new Color(55, 65, 81));
        if (fieldIndex == 0) lblNombreCampo = caption;
        else if (fieldIndex == 1) lblTelefonoCampo = caption;
        else if (fieldIndex == 2) lblEmailCampo = caption;
        else if (fieldIndex == 3) lblCategoriaCampo = caption;
        field.setFont(font(Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(360, 40));
        field.setBorder(fieldBorder());
        g.gridx = 0; g.gridy = row; g.weightx = 0.25;
        panel.add(caption, g);
        g.gridx = 1; g.weightx = 0.75;
        panel.add(field, g);
    }

    private JPanel transparentPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    private JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font(style, size));
        label.setForeground(color);
        return label;
    }

    private Font font(int style, int size) {
        // Segoe UI está disponible normalmente en Windows; Arial/SansSerif son alternativas seguras.
        for (String name : new String[]{"Segoe UI", "Arial", "SansSerif"}) {
            Font f = new Font(name, style, size);
            if (name.equals("SansSerif") || f.getFamily().equalsIgnoreCase(name)) return f;
        }
        return new Font(Font.SANS_SERIF, style, size);
    }

    private javax.swing.border.Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 11, 8, 11));
    }

    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(font(Font.BOLD, 12));
        b.setForeground(new Color(55, 65, 81));
        b.setBackground(new Color(245, 247, 250));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setBorder(new EmptyBorder(11, 12, 11, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(font(Font.BOLD, 12));
        b.setForeground(WHITE);
        b.setBackground(BLUE);
        b.setBorder(new EmptyBorder(11, 17, 11, 17));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(font(Font.BOLD, 11));
        b.setForeground(new Color(55, 65, 81));
        b.setBackground(WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 12, 8, 12)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = secondaryButton(text);
        b.setForeground(new Color(185, 28, 28));
        return b;
    }

    public void mostrarLista() { cardLayout.show(cards, LISTA); }
    public void mostrarFormulario() { cardLayout.show(cards, FORM); }
    public void mostrarEstadisticas() { cardLayout.show(cards, ESTADISTICAS); }

    public void actualizarTextos() {
        setTitle(I18n.t("app.title"));
        lblMarca.setText(I18n.t("app.title"));
        lblTituloNav.setText(I18n.t("nav.section"));
        lblIdioma.setText(I18n.t("language"));
        btn_nav_contactos.setText(I18n.t("nav.contacts"));
        btn_nav_estadisticas.setText(I18n.t("nav.stats"));
        lblTituloLista.setText(I18n.t("home.title"));
        lblSubtitulo.setText(I18n.t("home.subtitle"));
        btn_add.setText(I18n.t("button.add"));
        btn_modificar.setText(I18n.t("button.edit"));
        btn_eliminar.setText(I18n.t("button.delete"));
        btn_exportar.setText(I18n.t("button.export"));
        btn_guardar.setText(I18n.t("button.save"));
        btn_cancelar.setText(I18n.t("button.cancel"));
        lblFormTitulo.setText(I18n.t("form.new"));
        chb_favorito.setText(I18n.t("form.favorite"));
        lblNombreCampo.setText(I18n.t("form.name"));
        lblTelefonoCampo.setText(I18n.t("form.phone"));
        lblEmailCampo.setText(I18n.t("form.email"));
        lblCategoriaCampo.setText(I18n.t("form.category"));
        lblTituloEstadisticas.setText(I18n.t("nav.stats"));
        lblTituloTotal.setText(I18n.t("stats.total"));
        lblTituloFavoritos.setText(I18n.t("stats.favorites"));
        lblProgresoTitulo.setText(I18n.t("stats.progress"));
        txt_buscar.setToolTipText(I18n.t("search.placeholder"));
        if (txt_buscar instanceof HintTextField) ((HintTextField) txt_buscar).setHint(I18n.t("search.placeholder"));

        int selectedIndex = cmb_categoria.getSelectedIndex();
        cmb_categoria.removeAllItems();
        cmb_categoria.addItem(I18n.t("category.choose"));
        cmb_categoria.addItem(I18n.t("category.family"));
        cmb_categoria.addItem(I18n.t("category.friends"));
        cmb_categoria.addItem(I18n.t("category.work"));
        cmb_categoria.addItem(I18n.t("category.other"));
        if (selectedIndex >= 0 && selectedIndex < cmb_categoria.getItemCount()) {
            cmb_categoria.setSelectedIndex(selectedIndex);
        }

        modeloTabla.setColumnIdentifiers(new String[]{I18n.t("table.name"), I18n.t("table.phone"),
                I18n.t("table.email"), I18n.t("table.category"), I18n.t("table.favorite")});
    }

    private void seleccionarCategoria(String canonical) {
        String[] canonicalValues = {"Familia", "Amigos", "Trabajo", "Otros"};
        String[] keys = {"category.family", "category.friends", "category.work", "category.other"};
        for (int i = 0; i < canonicalValues.length; i++) {
            if (canonicalValues[i].equalsIgnoreCase(canonical)) {
                cmb_categoria.setSelectedItem(I18n.t(keys[i]));
                return;
            }
        }
    }

    private class HintTextField extends JTextField {
        private String hint;
        HintTextField(String hint) { this.hint = hint; }
        void setHint(String hint) { this.hint = hint; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !hasFocus() && hint != null && !hint.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(150, 158, 170));
                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics(getFont());
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint, insets.left + 2, y);
                g2.dispose();
            }
        }
    }

    public void actualizarMensajeVacio(boolean mostrar) {
        lblEstadoVacio.setText(mostrar ? I18n.t("empty") : "");
    }

    public void actualizarProgreso(int value, String detail) {
        barraProgreso.setValue(Math.max(0, Math.min(100, value)));
        barraProgreso.setString(value + "%");
        lblProgresoDetalle.setText(detail);
    }

    public void actualizarTitulosEstadisticas() {
        lblTituloTotal.setText(I18n.t("stats.total"));
        lblTituloFavoritos.setText(I18n.t("stats.favorites"));
    }
}
