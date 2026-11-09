package recursos;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {
    private static Locale locale = new Locale("es");
    private static ResourceBundle bundle = cargar(locale);

    private I18n() { }

    private static ResourceBundle cargar(Locale idioma) {
        return ResourceBundle.getBundle("recursos.mensajes", idioma);
    }

    public static void cambiarIdioma(String codigo) {
        locale = new Locale(codigo);
        bundle = cargar(locale);
    }

    public static String t(String key) {
        try { return bundle.getString(key); }
        catch (Exception ex) { return key; }
    }
}
