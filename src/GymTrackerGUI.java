import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz gráfica principal para el sistema de seguimiento de ejercicios
 * Diseño reorganizado: página principal con lista simple y detalle de ejercicio
 * Estilo: Gymshark (Dark Mode + Cyan Accent)
 * Aspecto: Mobile (9:16)
 */
public class GymTrackerGUI extends JFrame {
    // Colores estilo Gymshark
    private static final Color COLOR_FONDO_OSCURO = new Color(18, 18, 18); // #121212
    private static final Color COLOR_FONDO_PANEL = new Color(30, 30, 30); // #1E1E1E
    private static final Color COLOR_ACCENT = new Color(102, 199, 244); // Gymshark Blue/Cyan
    private static final Color COLOR_ACCENT_HOVER = new Color(130, 210, 250);
    private static final Color COLOR_SECUNDARIO = new Color(50, 50, 50); // Dark Grey
    private static final Color COLOR_SECUNDARIO_HOVER = new Color(70, 70, 70);
    private static final Color COLOR_BORDE = new Color(60, 60, 60);
    private static final Color COLOR_TEXTO_CLARO = Color.WHITE;
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(180, 180, 180);

    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 14);

    private EjercicioDAO ejercicioDAO;
    private DefaultTableModel tableModelDetalle;
    private JTable tablaDetalle;
    private JList<String> listaEjercicios;
    private DefaultListModel<String> listModel;

    // Panel principal (card layout)
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Componentes del formulario
    private JTextField nombreField;
    private JComboBox<String> grupoMuscularCombo;
    private JTextField pesoField;
    private JTextField repeticionesField;
    private JTextField fechaField;
    private JButton btnGuardar;

    // Componentes del detalle de ejercicio
    private JTextField pesoNuevoField;
    private JTextField repeticionesNuevoField;
    private JTextField fechaNuevoField;
    private JLabel tituloEjercicioLabel;
    private String ejercicioSeleccionadoNombre;
    private String grupoActual;
    private JLabel tituloListaLabel;

    public GymTrackerGUI() {
        try {
            ejercicioDAO = new EjercicioDAO();
            DatabaseManager.getInstance().initializeDatabase();

            setTitle("GymTracker");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Tamaño formato móvil (9:16 aprox)
            setMinimumSize(new Dimension(360, 640));
            setSize(450, 800);
            setLocationRelativeTo(null);
            getContentPane().setBackground(COLOR_FONDO_OSCURO);

            // Custom UI properties for smoother look
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);

            crearMenuBar();
            crearInterfaz();

            // Asegurar que la ventana sea visible
            setVisible(true);
            toFront();
            requestFocus();

        } catch (Exception e) {
            System.err.println("Error en constructor de GymTrackerGUI: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al inicializar la aplicación: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_FONDO_PANEL);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        // Menú Ejercicios
        JMenu menuEjercicios = new JMenu("Ejercicios");
        menuEjercicios.setForeground(COLOR_TEXTO_CLARO);
        menuEjercicios.setFont(FUENTE_NORMAL);

        JMenuItem itemNuevo = new JMenuItem("Nuevo Ejercicio");
        itemNuevo.setForeground(COLOR_TEXTO_CLARO);
        itemNuevo.setBackground(COLOR_FONDO_PANEL);
        itemNuevo.addActionListener(e -> mostrarFormulario());
        menuEjercicios.add(itemNuevo);

        JMenuItem itemLista = new JMenuItem("Lista de Ejercicios");
        itemLista.setForeground(COLOR_TEXTO_CLARO);
        itemLista.setBackground(COLOR_FONDO_PANEL);
        itemLista.addActionListener(e -> mostrarCategorias());
        menuEjercicios.add(itemLista);

        menuBar.add(menuEjercicios);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setForeground(COLOR_TEXTO_CLARO);
        menuAyuda.setFont(FUENTE_NORMAL);

        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.setForeground(COLOR_TEXTO_CLARO);
        itemAcerca.setBackground(COLOR_FONDO_PANEL);
        itemAcerca.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "GymTracker v2.0\nEstilo Gymshark",
                    "Acerca de", JOptionPane.INFORMATION_MESSAGE);
        });
        menuAyuda.add(itemAcerca);

        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    private void crearInterfaz() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_FONDO_OSCURO);

        // Página principal - Lista simple de ejercicios
        // Página Categorías (Nueva Principal)
        JPanel panelCategorias = crearPanelCategorias();
        cardPanel.add(panelCategorias, "CATEGORIAS");

        // Página Lista de Ejercicios
        JPanel panelLista = crearPanelListaEjercicios();
        cardPanel.add(panelLista, "LISTA_EJERCICIOS");

        // Página detalle de ejercicio
        JPanel panelDetalle = crearPanelDetalleEjercicio();
        JScrollPane scrollDetalle = new JScrollPane(panelDetalle);
        scrollDetalle.setBorder(null);
        scrollDetalle.getViewport().setBackground(COLOR_FONDO_OSCURO);
        scrollDetalle.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        estilizarScrollBar(scrollDetalle);
        cardPanel.add(scrollDetalle, "DETALLE");

        // Página formulario - con scroll
        JPanel panelFormulario = crearPanelFormulario();
        JScrollPane scrollFormulario = new JScrollPane(panelFormulario);
        scrollFormulario.setBorder(null);
        scrollFormulario.getViewport().setBackground(COLOR_FONDO_OSCURO); // Fix background match
        scrollFormulario.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        estilizarScrollBar(scrollFormulario);
        cardPanel.add(scrollFormulario, "FORMULARIO");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "CATEGORIAS");
    }

    private JPanel crearPanelCategorias() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO_OSCURO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("CATEGORÍAS", SwingConstants.CENTER);
        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(COLOR_TEXTO_CLARO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        gridPanel.setBackground(COLOR_FONDO_OSCURO);

        String[] categorias = { "Pecho", "Espalda", "Piernas", "Brazos", "Hombros", "Core", "Glúteos", "Otro" };

        for (String cat : categorias) {
            JButton btnCat = new JButton(cat);
            btnCat.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnCat.setBackground(COLOR_FONDO_PANEL);
            btnCat.setForeground(COLOR_ACCENT);
            btnCat.setFocusPainted(false);
            btnCat.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
            btnCat.setPreferredSize(new Dimension(0, 100)); // Altura fija para botones

            btnCat.addActionListener(e -> mostrarListaEjerciciosPorGrupo(cat));

            // Hover effect
            btnCat.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnCat.setBackground(COLOR_SECUNDARIO);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnCat.setBackground(COLOR_FONDO_PANEL);
                }
            });

            gridPanel.add(btnCat);
        }

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO_OSCURO);
        estilizarScrollBar(scroll);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelListaEjercicios() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO_OSCURO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header con botón volver y título dynamic
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_FONDO_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JButton btnVolver = crearBoton("←", COLOR_SECUNDARIO);
        btnVolver.setPreferredSize(new Dimension(50, 50));
        btnVolver.addActionListener(e -> mostrarCategorias());
        headerPanel.add(btnVolver, BorderLayout.WEST);

        tituloListaLabel = new JLabel("EJERCICIOS", SwingConstants.CENTER);
        tituloListaLabel.setFont(FUENTE_TITULO);
        tituloListaLabel.setForeground(COLOR_TEXTO_CLARO);
        headerPanel.add(tituloListaLabel, BorderLayout.CENTER);
        // Spacer para centrar título
        headerPanel.add(Box.createHorizontalStrut(50), BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Lista de ejercicios
        listModel = new DefaultListModel<>();
        listaEjercicios = new JList<>(listModel);
        listaEjercicios.setFont(FUENTE_NORMAL);
        listaEjercicios.setBackground(COLOR_FONDO_OSCURO);
        listaEjercicios.setForeground(COLOR_TEXTO_CLARO);
        listaEjercicios.setSelectionBackground(COLOR_FONDO_OSCURO);
        listaEjercicios.setSelectionForeground(COLOR_TEXTO_CLARO);

        // Remove default borders
        listaEjercicios.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Custom Renderer
        listaEjercicios.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(isSelected ? COLOR_SECUNDARIO : COLOR_FONDO_PANEL);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 10, 0),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(isSelected ? COLOR_ACCENT : COLOR_BORDE, 1),
                                BorderFactory.createEmptyBorder(15, 15, 15, 15))));

                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 16));
                label.setForeground(isSelected ? COLOR_ACCENT : COLOR_TEXTO_CLARO);

                card.add(label, BorderLayout.CENTER);

                JLabel arrow = new JLabel("›");
                arrow.setFont(new Font("Segoe UI", Font.BOLD, 20));
                arrow.setForeground(isSelected ? COLOR_ACCENT : Color.GRAY);
                card.add(arrow, BorderLayout.EAST);

                return card;
            }
        });

        // Listener
        listaEjercicios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1 || evt.getClickCount() == 2) {
                    String ejercicioSeleccionado = listaEjercicios.getSelectedValue();
                    if (ejercicioSeleccionado != null) {
                        mostrarDetalleEjercicio(ejercicioSeleccionado);
                        listaEjercicios.clearSelection();
                    }
                }
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaEjercicios);
        scrollLista.setBorder(null);
        scrollLista.getViewport().setBackground(COLOR_FONDO_OSCURO);
        estilizarScrollBar(scrollLista);
        panel.add(scrollLista, BorderLayout.CENTER);

        // Panel de botones flotante en el bottom
        JPanel panelBotones = new JPanel(new GridLayout(1, 1, 15, 0));
        panelBotones.setBackground(COLOR_FONDO_OSCURO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnNuevo = crearBoton("Nuevo Ejercicio", COLOR_ACCENT);
        btnNuevo.addActionListener(e -> mostrarFormulario());
        panelBotones.add(btnNuevo);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelDetalleEjercicio() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO_OSCURO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior con título y botón volver
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBackground(COLOR_FONDO_OSCURO);

        JButton btnVolver = crearBoton("←", COLOR_SECUNDARIO);
        btnVolver.setPreferredSize(new Dimension(50, 50));
        btnVolver.addActionListener(e -> mostrarListaEjerciciosPorGrupo(grupoActual));
        panelSuperior.add(btnVolver, BorderLayout.WEST);

        tituloEjercicioLabel = new JLabel("", SwingConstants.CENTER);
        tituloEjercicioLabel.setFont(FUENTE_TITULO);
        tituloEjercicioLabel.setForeground(COLOR_ACCENT);
        panelSuperior.add(tituloEjercicioLabel, BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de historial del ejercicio
        String[] columnas = { "Peso", "Reps", "Fecha" }; // Simplified columns for mobile
        tableModelDetalle = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDetalle = new JTable(tableModelDetalle);
        tablaDetalle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalle.setFont(FUENTE_NORMAL);
        tablaDetalle.setRowHeight(45);
        tablaDetalle.setBackground(COLOR_FONDO_PANEL);
        tablaDetalle.setForeground(COLOR_TEXTO_CLARO);
        tablaDetalle.setGridColor(COLOR_BORDE);
        tablaDetalle.setShowVerticalLines(false);
        tablaDetalle.setSelectionBackground(COLOR_SECUNDARIO);
        tablaDetalle.setSelectionForeground(COLOR_ACCENT);

        // Estilizar header de la tabla
        JTableHeader header = tablaDetalle.getTableHeader();
        header.setFont(FUENTE_LABEL);
        header.setBackground(COLOR_FONDO_OSCURO);
        header.setForeground(COLOR_TEXTO_SECUNDARIO);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ACCENT));

        JScrollPane scrollTabla = new JScrollPane(tablaDetalle);
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollTabla.getViewport().setBackground(COLOR_FONDO_PANEL);
        estilizarScrollBar(scrollTabla);
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Panel para agregar nuevo registro
        JPanel panelAgregar = crearPanelAgregarRegistro();
        panel.add(panelAgregar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelAgregarRegistro() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titulo = new JLabel("NUEVO REGISTRO", SwingConstants.CENTER);
        titulo.setFont(FUENTE_LABEL);
        titulo.setForeground(COLOR_ACCENT);
        panel.add(titulo, gbc);

        // Campos
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Peso Layout
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        panel.add(crearLabel("Peso (kg)"), gbc);

        gbc.gridx = 1;
        pesoNuevoField = crearTextField();
        pesoNuevoField.setText("0.0");
        panel.add(crearControlNumerico(pesoNuevoField, 0.5, true), gbc);

        // Reps Layout
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(crearLabel("Repeticiones"), gbc);

        gbc.gridx = 1;
        repeticionesNuevoField = crearTextField();
        repeticionesNuevoField.setText("0");
        panel.add(crearControlNumerico(repeticionesNuevoField, 1, false), gbc);

        // Fecha
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(crearLabel("Fecha"), gbc);

        gbc.gridx = 1;
        fechaNuevoField = crearTextField();
        fechaNuevoField.setText(LocalDate.now().toString());
        panel.add(fechaNuevoField, gbc);

        // Botón guardar
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);

        JButton btnGuardarNuevo = crearBoton("GUARDAR", COLOR_ACCENT);
        btnGuardarNuevo.addActionListener(e -> guardarNuevoRegistro());
        panel.add(btnGuardarNuevo, gbc);

        return panel;
    }

    // Helper para crear controles +/-
    private JPanel crearControlNumerico(JTextField field, double step, boolean isDouble) {
        JPanel container = new JPanel(new BorderLayout(5, 0));
        container.setBackground(COLOR_FONDO_PANEL);

        container.add(field, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 2, 0));
        btnPanel.setBackground(COLOR_FONDO_PANEL);

        JButton minus = crearAccionBoton("-", COLOR_SECUNDARIO);
        minus.addActionListener(e -> {
            try {
                double val = Double.parseDouble(field.getText());
                val = Math.max(0, val - step);
                field.setText(isDouble ? String.format("%.1f", val) : String.valueOf((int) val));
            } catch (Exception ex) {
            }
        });

        JButton plus = crearAccionBoton("+", COLOR_SECUNDARIO);
        plus.addActionListener(e -> {
            try {
                double val = Double.parseDouble(field.getText());
                val += step;
                field.setText(isDouble ? String.format("%.1f", val) : String.valueOf((int) val));
            } catch (Exception ex) {
            }
        });

        btnPanel.add(minus);
        btnPanel.add(plus);
        container.add(btnPanel, BorderLayout.EAST);

        return container;
    }

    private JButton crearAccionBoton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(COLOR_TEXTO_CLARO);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(40, 40));
        return btn;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Título de sección
        JLabel tituloSeccion = new JLabel("NUEVO EJERCICIO", SwingConstants.CENTER);
        tituloSeccion.setFont(FUENTE_TITULO);
        tituloSeccion.setForeground(COLOR_ACCENT);
        gbc.gridy = 0;
        panel.add(tituloSeccion, gbc);

        // Nombre
        gbc.gridy++;
        panel.add(crearLabel("Nombre del ejercicio"), gbc);
        gbc.gridy++;
        nombreField = crearTextField();
        panel.add(nombreField, gbc);

        // Grupo muscular
        gbc.gridy++;
        panel.add(crearLabel("Grupo Muscular"), gbc);
        gbc.gridy++;
        String[] gruposMusculares = { "Pecho", "Espalda", "Piernas", "Brazos", "Hombros", "Core", "Glúteos", "Otro" };
        grupoMuscularCombo = crearComboBox(gruposMusculares);
        panel.add(grupoMuscularCombo, gbc);

        // Peso
        gbc.gridy++;
        panel.add(crearLabel("Peso (kg)"), gbc);
        gbc.gridy++;
        pesoField = crearTextField();
        pesoField.setText("0.0");
        panel.add(crearControlNumerico(pesoField, 0.5, true), gbc);

        // Reps
        gbc.gridy++;
        panel.add(crearLabel("Repeticiones"), gbc);
        gbc.gridy++;
        repeticionesField = crearTextField();
        repeticionesField.setText("0");
        panel.add(crearControlNumerico(repeticionesField, 1, false), gbc);

        // Fecha
        gbc.gridy++;
        panel.add(crearLabel("Fecha"), gbc);
        gbc.gridy++;
        fechaField = crearTextField();
        fechaField.setText(LocalDate.now().toString());
        panel.add(fechaField, gbc);

        // Botones
        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 10, 0);

        btnGuardar = crearBoton("GUARDAR EJERCICIO", COLOR_ACCENT);
        btnGuardar.addActionListener(e -> guardarEjercicio());
        panel.add(btnGuardar, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        JButton btnCancelar = crearBoton("CANCELAR", COLOR_SECUNDARIO);
        btnCancelar.addActionListener(e -> {
            if (grupoActual != null)
                mostrarListaEjerciciosPorGrupo(grupoActual);
            else
                mostrarCategorias();
        });
        panel.add(btnCancelar, gbc);

        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_LABEL);
        label.setForeground(COLOR_TEXTO_SECUNDARIO);
        return label;
    }

    private JTextField crearTextField() {
        JTextField field = new JTextField();
        field.setFont(FUENTE_NORMAL);
        field.setBackground(COLOR_FONDO_OSCURO);
        field.setForeground(COLOR_TEXTO_CLARO);
        field.setCaretColor(COLOR_ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        field.setPreferredSize(new Dimension(0, 45));
        return field;
    }

    private JComboBox<String> crearComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FUENTE_NORMAL);
        combo.setBackground(COLOR_FONDO_OSCURO);
        combo.setForeground(COLOR_TEXTO_CLARO);
        combo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        ((JComponent) combo.getRenderer()).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        combo.setPreferredSize(new Dimension(0, 45));
        return combo;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setBackground(color);
        boton.setForeground(COLOR_TEXTO_CLARO);
        boton.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(0, 50));

        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (color == COLOR_ACCENT)
                    boton.setBackground(COLOR_ACCENT_HOVER);
                else
                    boton.setBackground(COLOR_SECUNDARIO_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private void estilizarScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = COLOR_SECUNDARIO;
                this.trackColor = COLOR_FONDO_OSCURO;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
        });
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }

    private void cargarListaEjercicios(String grupo) {
        listModel.clear();
        List<String> nombres;
        if (grupo == null || grupo.isEmpty()) {
            nombres = ejercicioDAO.obtenerNombresEjercicios();
        } else {
            nombres = ejercicioDAO.obtenerNombresEjerciciosPorGrupo(grupo);
        }

        for (String nombre : nombres) {
            listModel.addElement(nombre);
        }
    }

    private void mostrarCategorias() {
        limpiarFormulario();
        grupoActual = null;
        cardLayout.show(cardPanel, "CATEGORIAS");
    }

    private void mostrarListaEjerciciosPorGrupo(String grupo) {
        grupoActual = grupo;
        cargarListaEjercicios(grupo);

        if (tituloListaLabel != null) {
            tituloListaLabel.setText(grupo != null ? grupo.toUpperCase() : "EJERCICIOS");
        }

        cardLayout.show(cardPanel, "LISTA_EJERCICIOS");
    }

    private void mostrarFormulario() {
        limpiarFormulario();
        cardLayout.show(cardPanel, "FORMULARIO");
    }

    private void mostrarDetalleEjercicio(String nombreEjercicio) {
        ejercicioSeleccionadoNombre = nombreEjercicio;

        // Actualizar título
        if (tituloEjercicioLabel != null) {
            tituloEjercicioLabel.setText(nombreEjercicio.toUpperCase());
        }

        cargarHistorialEjercicio(nombreEjercicio);
        cargarUltimoRegistroEnCampos(nombreEjercicio);
        cardLayout.show(cardPanel, "DETALLE");
    }

    private void cargarUltimoRegistroEnCampos(String nombreEjercicio) {
        List<Ejercicio> ejercicios = ejercicioDAO.obtenerEjerciciosPorNombre(nombreEjercicio);

        if (!ejercicios.isEmpty()) {
            Ejercicio ultimoEjercicio = ejercicios.get(0);
            if (pesoNuevoField != null)
                pesoNuevoField.setText(String.format("%.1f", ultimoEjercicio.getPeso()));
            if (repeticionesNuevoField != null)
                repeticionesNuevoField.setText(String.valueOf(ultimoEjercicio.getRepeticiones()));
            if (fechaNuevoField != null)
                fechaNuevoField.setText(LocalDate.now().toString());
        } else {
            if (pesoNuevoField != null)
                pesoNuevoField.setText("0.0");
            if (repeticionesNuevoField != null)
                repeticionesNuevoField.setText("0");
            if (fechaNuevoField != null)
                fechaNuevoField.setText(LocalDate.now().toString());
        }
    }

    private void cargarHistorialEjercicio(String nombreEjercicio) {
        tableModelDetalle.setRowCount(0);
        List<Ejercicio> ejercicios = ejercicioDAO.obtenerEjerciciosPorNombre(nombreEjercicio);

        for (Ejercicio ejercicio : ejercicios) {
            Object[] fila = {
                    ejercicio.getPeso() + " kg",
                    ejercicio.getRepeticiones(),
                    ejercicio.getFecha() != null ? ejercicio.getFecha().toString() : "-"
            };
            tableModelDetalle.addRow(fila);
        }
    }

    private void guardarNuevoRegistro() {
        if (ejercicioSeleccionadoNombre == null || ejercicioSeleccionadoNombre.isEmpty()) {
            mostrarMensaje("Error: No hay ejercicio seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double peso = 0.0;
        try {
            peso = Double.parseDouble(pesoNuevoField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor, ingresa un peso válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int repeticiones = 0;
        try {
            repeticiones = Integer.parseInt(repeticionesNuevoField.getText().trim());
            if (repeticiones <= 0) {
                mostrarMensaje("Las repeticiones deben ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor, ingresa un número válido de repeticiones.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fecha = null;
        try {
            String fechaStr = fechaNuevoField.getText().trim();
            if (!fechaStr.isEmpty()) {
                fecha = LocalDate.parse(fechaStr);
            }
        } catch (Exception e) {
            mostrarMensaje("Formato de fecha inválido. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Ejercicio> ejercicios = ejercicioDAO.obtenerEjerciciosPorNombre(ejercicioSeleccionadoNombre);
        String grupoMuscular = ejercicios.isEmpty() ? "Otro" : ejercicios.get(0).getGrupoMuscular();

        Ejercicio nuevoEjercicio = new Ejercicio(ejercicioSeleccionadoNombre, grupoMuscular, peso, repeticiones, fecha);
        boolean exito = ejercicioDAO.insertarEjercicio(nuevoEjercicio);

        if (exito) {
            mostrarMensaje("Registro guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarHistorialEjercicio(ejercicioSeleccionadoNombre);
            cargarUltimoRegistroEnCampos(ejercicioSeleccionadoNombre);
        } else {
            mostrarMensaje("Error al guardar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEjercicio() {
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarMensaje("Por favor, ingresa el nombre del ejercicio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double peso = 0.0;
        try {
            peso = Double.parseDouble(pesoField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor, ingresa un peso válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int repeticiones = 0;
        try {
            repeticiones = Integer.parseInt(repeticionesField.getText().trim());
            if (repeticiones <= 0) {
                mostrarMensaje("Las repeticiones deben ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor, ingresa un número válido de repeticiones.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String grupoMuscular = (String) grupoMuscularCombo.getSelectedItem();
        LocalDate fecha = null;

        try {
            String fechaStr = fechaField.getText().trim();
            if (!fechaStr.isEmpty() && !fechaStr.equals("Sin fecha")) {
                fecha = LocalDate.parse(fechaStr);
            }
        } catch (Exception e) {
            mostrarMensaje("Formato de fecha inválido. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ejercicio ejercicio = new Ejercicio(nombre, grupoMuscular, peso, repeticiones, fecha);
        boolean exito = ejercicioDAO.insertarEjercicio(ejercicio);

        if (exito) {
            mostrarMensaje("Ejercicio guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            mostrarListaEjerciciosPorGrupo(grupoMuscular);
        } else {
            mostrarMensaje("Error al guardar el ejercicio.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        if (nombreField != null)
            nombreField.setText("");
        if (grupoMuscularCombo != null)
            grupoMuscularCombo.setSelectedIndex(0);
        if (pesoField != null)
            pesoField.setText("0.0");
        if (repeticionesField != null)
            repeticionesField.setText("0");
        if (fechaField != null)
            fechaField.setText(LocalDate.now().toString());
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
