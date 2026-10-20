
package es.ad03;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.vibur.dbcp.ViburDBCPDataSource;

public class AD03TA01Ejercicio5 {

    public static void main(String[] args) {

        // Creamos y configuramos el pool de conexiones Vibur
        ViburDBCPDataSource pool = new ViburDBCPDataSource();

        pool.setJdbcUrl("jdbc:hsqldb:file:./datos/bdformacion");
        pool.setUsername("SA");
        pool.setPassword("");

        // Configuramos el tamaño del pool
        pool.setPoolInitialSize(1);
        pool.setPoolMaxSize(5);

        try {
            // Iniciamos el pool antes de pedir la conexión
            pool.start();

            // Obtenemos una conexión del pool
            try (Connection conexion = pool.getConnection()) {

                // Comprobamos si la conexión es válida
                System.out.println(
                    "Conexión válida: " + conexion.isValid(2));

                try (Statement sentencia = conexion.createStatement()) {

                    // Creamos la tabla si todavía no existe
                    sentencia.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS comprobacion (" +
                        "id INTEGER PRIMARY KEY, " +
                        "mensaje VARCHAR(100))");

                    // Insertamos una fila o actualizamos la existente
                    sentencia.executeUpdate(
                        "MERGE INTO comprobacion " +
                        "USING (VALUES (1, 'Conexion HSQLDB mediante Vibur correcta')) " +
                        "AS datos(id, mensaje) " +
                        "ON comprobacion.id = datos.id " +
                        "WHEN MATCHED THEN UPDATE SET mensaje = datos.mensaje " +
                        "WHEN NOT MATCHED THEN INSERT (id, mensaje) " +
                        "VALUES (datos.id, datos.mensaje)");

                    // Consultamos los datos guardados
                    try (ResultSet resultado = sentencia.executeQuery(
                            "SELECT id, mensaje FROM comprobacion")) {

                        while (resultado.next()) {
                            System.out.println(
                                resultado.getInt("id") + " - " +
                                resultado.getString("mensaje"));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());

        } finally {
            // Cerramos el pool al terminar
            pool.close();
        }
    }
}