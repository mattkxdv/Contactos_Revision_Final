package controlador;

import modelo.persona;
import modelo.personaDAO;
import recursos.I18n;
import vista.ventana;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class logica_ventana implements ActionListener {
    private final ventana v;
    private final personaDAO dao = new personaDAO(new persona());
    private final List<persona> contactos = new ArrayList<>();
    private final List<persona> visibles = new ArrayList<>();
    private persona editando;

    public logica_ventana(ventana v) {
        this.v = v;
        v.btn_add.addActionListener(this);
        v.btn_modificar.addActionListener(this);
        v.btn_eliminar.addActionListener(this);
        v.btn_exportar.addActionListener(this);
        v.btn_guardar.addActionListener(this);
        v.btn_cancelar.addActionListener(this);
        v.cmb_idioma.addActionListener(e -> cambiarIdioma());
        v.txt_buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        cargar();
    }

    private void cambiarIdioma() {
        String selected = (String) v.cmb_idioma.getSelectedItem();
        if (selected == null) return;
        I18n.cambiarIdioma(selected.equals("English") ? "en" : selected.equals("Français") ? "fr" : "es");
        v.actualizarTextos();
        filtrar();
        actualizarEstadisticas();
        v.actualizarProgreso(100, I18n.t("stats.completed"));
    }

    private void cargar() {
        try {
            contactos.clear();
            contactos.addAll(dao.leerArchivo());
            filtrar();
            actualizarEstadisticas();
            v.actualizarProgreso(100, I18n.t("stats.completed"));
            v.lblEstado.setText(I18n.t("status.loaded"));
        } catch (IOException | RuntimeException ex) {
            v.actualizarProgreso(0, I18n.t("stats.error"));
            v.lblEstado.setText(I18n.t("status.error"));
            JOptionPane.showMessageDialog(v, I18n.t("error.load") + "\n" + ex.getMessage(),
                    I18n.t("app.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrar() {
        v.modeloTabla.setRowCount(0);
        visibles.clear();
        String q = v.txt_buscar.getText().trim().toLowerCase();
        for (persona p : contactos) {
            String content = (p.getNombre() + " " + p.getTelefono() + " " + p.getEmail()).toLowerCase();
            if (content.contains(q)) {
                visibles.add(p);
                v.modeloTabla.addRow(new Object[]{p.getNombre(), p.getTelefono(), p.getEmail(),
                        traducirCategoria(p.getCategoria()), p.isFavorito() ? I18n.t("table.yes") : I18n.t("table.no")});
            }
        }
        v.lblEstado.setText(MessageFormat.format(I18n.t("status.results"), visibles.size(), contactos.size()));
        v.actualizarMensajeVacio(visibles.isEmpty());
    }

    private String traducirCategoria(String categoria) {
        if ("Familia".equalsIgnoreCase(categoria)) return I18n.t("category.family");
        if ("Amigos".equalsIgnoreCase(categoria)) return I18n.t("category.friends");
        if ("Trabajo".equalsIgnoreCase(categoria)) return I18n.t("category.work");
        if ("Otros".equalsIgnoreCase(categoria)) return I18n.t("category.other");
        return categoria;
    }

    private void actualizarEstadisticas() {
        int favorites = 0;
        for (persona p : contactos) if (p.isFavorito()) favorites++;
        v.lblTotalContactos.setText(String.valueOf(contactos.size()));
        v.lblFavoritos.setText(String.valueOf(favorites));
    }

    private persona seleccionado() {
        int row = v.tablaContactos.getSelectedRow();
        if (row < 0) return null;
        int modelRow = v.tablaContactos.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= visibles.size()) return null;
        return visibles.get(modelRow);
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == v.btn_add) abrirNuevo();
        else if (source == v.btn_modificar) abrirEdicion();
        else if (source == v.btn_eliminar) eliminar();
        else if (source == v.btn_exportar) exportar();
        else if (source == v.btn_guardar) guardar();
        else if (source == v.btn_cancelar) cancelar();
    }

    private void abrirNuevo() {
        editando = null;
        limpiarFormulario();
        v.lblFormTitulo.setText(I18n.t("form.new"));
        v.mostrarFormulario();
        v.txt_nombres.requestFocusInWindow();
    }

    private void abrirEdicion() {
        persona p = seleccionado();
        if (p == null) { mensaje(I18n.t("select")); return; }
        editando = p;
        v.txt_nombres.setText(p.getNombre());
        v.txt_telefono.setText(p.getTelefono());
        v.txt_email.setText(p.getEmail());
        seleccionarCategoria(p.getCategoria());
        v.chb_favorito.setSelected(p.isFavorito());
        v.lblFormTitulo.setText(I18n.t("form.edit"));
        v.mostrarFormulario();
    }

    private void seleccionarCategoria(String canonical) {
        String[] values = {"Familia", "Amigos", "Trabajo", "Otros"};
        String[] keys = {"category.family", "category.friends", "category.work", "category.other"};
        for (int i = 0; i < values.length; i++) {
            if (values[i].equalsIgnoreCase(canonical)) {
                v.cmb_categoria.setSelectedItem(I18n.t(keys[i]));
                return;
            }
        }
        v.cmb_categoria.setSelectedIndex(0);
    }

    private String categoriaCanonicaSeleccionada() {
        String visible = (String) v.cmb_categoria.getSelectedItem();
        if (visible == null) return "";
        if (visible.equals(I18n.t("category.family"))) return "Familia";
        if (visible.equals(I18n.t("category.friends"))) return "Amigos";
        if (visible.equals(I18n.t("category.work"))) return "Trabajo";
        if (visible.equals(I18n.t("category.other"))) return "Otros";
        return "";
    }

    private void guardar() {
        String name = v.txt_nombres.getText().trim();
        String phone = v.txt_telefono.getText().trim();
        String email = v.txt_email.getText().trim();
        String category = categoriaCanonicaSeleccionada();
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || category.isEmpty()) {
            mensaje(I18n.t("required"));
            return;
        }

        boolean favorite = v.chb_favorito.isSelected();
        if (editando == null) {
            persona nuevo = new persona(name, phone, email, category, favorite);
            if (!dao.escribirArchivo(nuevo)) { mensaje(I18n.t("error.save")); return; }
            contactos.add(nuevo);
            mensaje(I18n.t("saved"));
        } else {
            persona anterior = new persona(editando.getNombre(), editando.getTelefono(), editando.getEmail(),
                    editando.getCategoria(), editando.isFavorito());
            editando.setNombre(name);
            editando.setTelefono(phone);
            editando.setEmail(email);
            editando.setCategoria(category);
            editando.setFavorito(favorite);
            try {
                dao.actualizarContactos(contactos);
                mensaje(I18n.t("updated"));
            } catch (IOException ex) {
                editando.setNombre(anterior.getNombre());
                editando.setTelefono(anterior.getTelefono());
                editando.setEmail(anterior.getEmail());
                editando.setCategoria(anterior.getCategoria());
                editando.setFavorito(anterior.isFavorito());
                mensaje(I18n.t("error.update") + "\n" + ex.getMessage());
                return;
            }
        }
        filtrar();
        actualizarEstadisticas();
        cancelar();
    }

    private void eliminar() {
        persona p = seleccionado();
        if (p == null) { mensaje(I18n.t("select")); return; }
        String question = MessageFormat.format(I18n.t("confirm.delete"), p.getNombre());
        int answer = JOptionPane.showConfirmDialog(v, question, I18n.t("confirm.title"), JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        int index = contactos.indexOf(p);
        contactos.remove(p);
        try {
            dao.actualizarContactos(contactos);
            filtrar();
            actualizarEstadisticas();
            mensaje(I18n.t("deleted"));
        } catch (IOException ex) {
            if (index >= 0) contactos.add(index, p);
            filtrar();
            actualizarEstadisticas();
            mensaje(I18n.t("error.delete") + "\n" + ex.getMessage());
        }
    }

    private void exportar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(I18n.t("button.export"));
        chooser.setSelectedFile(new File("contactos_exportados.csv"));
        if (chooser.showSaveDialog(v) != JFileChooser.APPROVE_OPTION) return;
        File destination = chooser.getSelectedFile();
        if (!destination.getName().toLowerCase().endsWith(".csv")) {
            destination = new File(destination.getAbsolutePath() + ".csv");
        }
        if (destination.exists() && JOptionPane.showConfirmDialog(v, I18n.t("confirm.overwrite"),
                I18n.t("confirm.title"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(destination), StandardCharsets.UTF_8))) {
            writer.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
            writer.newLine();
            for (persona p : contactos) { writer.write(p.datosContacto()); writer.newLine(); }
            mensaje(I18n.t("export.success"));
        } catch (IOException ex) {
            mensaje(I18n.t("export.error") + "\n" + ex.getMessage());
        }
    }

    private void cancelar() {
        limpiarFormulario();
        editando = null;
        v.mostrarLista();
    }

    private void limpiarFormulario() {
        v.txt_nombres.setText("");
        v.txt_telefono.setText("");
        v.txt_email.setText("");
        v.cmb_categoria.setSelectedIndex(0);
        v.chb_favorito.setSelected(false);
    }

    private void mensaje(String message) { JOptionPane.showMessageDialog(v, message); }
}
