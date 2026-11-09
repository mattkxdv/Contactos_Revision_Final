package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class personaDAO {
    private static final String HEADER = "NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO";
    private final Path archivo = Paths.get("C:/gestionContactos/datosContactos.csv");

    public personaDAO(persona ignorada) { preparar(); }

    private void preparar() {
        try {
            Files.createDirectories(archivo.getParent());
            if (Files.notExists(archivo)) {
                Files.write(archivo, (HEADER + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo preparar el archivo de contactos", ex);
        }
    }

    public boolean escribirArchivo(persona p) {
        if (p == null) return false;
        try {
            Files.write(archivo, (p.datosContacto() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException ex) { return false; }
    }

    public List<persona> leerArchivo() throws IOException {
        List<persona> result = new ArrayList<>();
        if (Files.notExists(archivo)) return result;
        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.equalsIgnoreCase(HEADER)) continue;
                String[] fields = line.split(";", -1);
                if (fields.length >= 5) {
                    result.add(new persona(fields[0], fields[1], fields[2], fields[3], Boolean.parseBoolean(fields[4])));
                }
            }
        }
        return result;
    }

    public void actualizarContactos(List<persona> contactos) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(HEADER);
            writer.newLine();
            for (persona p : contactos) {
                writer.write(p.datosContacto());
                writer.newLine();
            }
        }
    }
}
