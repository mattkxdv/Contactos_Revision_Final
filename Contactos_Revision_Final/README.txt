GESTIÓN DE CONTACTOS - VERSIÓN REVISADA

Esta versión integra:
- Ventana redimensionable con controles nativos de minimizar, maximizar y cerrar.
- Formularios de agregar y modificar dentro de la misma ventana.
- Búsqueda de contactos por nombre, teléfono y correo.
- Eliminación con confirmación.
- Exportación CSV en UTF-8.
- Sección de estadísticas con total, favoritos y barra de progreso al 100% cuando termina la carga.
- Internacionalización en español, inglés y francés mediante ResourceBundle.
- Tipografía Segoe UI con alternativas Arial/SansSerif.
- Sin emojis ni símbolos especiales como iconos de interfaz.

REQUISITOS
- JDK 8 o superior.
- Visual Studio Code con Extension Pack for Java (o cualquier IDE Java).

ESTRUCTURA
src/
  controlador/logica_ventana.java
  modelo/persona.java
  modelo/personaDAO.java
  recursos/I18n.java
  recursos/mensajes_es.properties
  recursos/mensajes_en.properties
  recursos/mensajes_fr.properties
  vista/ventana.java

EJECUCIÓN
1. Abra la carpeta del proyecto en VS Code.
2. Asegúrese de que la carpeta src esté reconocida como código fuente Java.
3. Ejecute el archivo src/vista/ventana.java con Run Java.

DATOS
La aplicación conserva la ruta de datos C:/gestionContactos/datosContactos.csv.
Antes de reemplazar archivos de un proyecto existente, haga una copia de seguridad de su carpeta y del CSV.

NOTA DE VALIDACIÓN
El código se compiló con javac en este entorno. La comprobación visual y de interacción final debe hacerse en Windows, en el equipo donde se ejecutará la aplicación.
