package cli.agenda.infrastructure.database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public enum DatabaseConnection {
    INSTANCE;

    private MongoClient client;
    private MongoDatabase database;

    DatabaseConnection() {
        try{
            String uri = "mongodb://root:rootpassword@localhost:27017/?authSource=admin";
            this.client = MongoClients.create(uri);
            this.database = this.client.getDatabase("cli_agenda_db");

            System.out.println("Conexión a MongoDB establecida con éxito.");
        }catch(MongoException e){
            System.err.println("Error de MongoDB al conectar con Docker: " + e.getMessage());
        }catch (IllegalArgumentException e){
            System.err.println("Error de formato en la URI de conexión: " + e.getMessage());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
