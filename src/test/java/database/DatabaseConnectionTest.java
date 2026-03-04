package database;

import cli.agenda.infrastructure.database.DatabaseConnection;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {
    @Test
    @DisplayName("Debe conectar a MongoDB y garantizar que es un Singleton")
    void testDatabaseConnectionIsSingletonAndValid() {

        DatabaseConnection connection1 = DatabaseConnection.INSTANCE;
        MongoDatabase db1 = connection1.getDatabase();

        assertNotNull(db1, "La base de datos NO debería ser null. ¿Está Docker encendido?");

        assertEquals("cli_agenda_db", db1.getName(), "El nombre de la base de datos no coincide");

        DatabaseConnection connection2 = DatabaseConnection.INSTANCE;
        MongoDatabase db2 = connection2.getDatabase();

        assertSame(connection1, connection2, "¡Alerta! El Singleton ha fallado, hay más de una instancia");
        assertSame(db1, db2, "Se han creado dos objetos MongoDatabase distintos");
    }
}
