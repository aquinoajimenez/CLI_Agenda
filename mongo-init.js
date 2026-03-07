// === ESPERAR A QUE MONGODB ESTÉ LISTO ===
sleep(5000); // Espera 5 segundos

// === AUTENTICACIÓN ===
db = db.getSiblingDB('admin');
var authResult = db.auth('root', 'rootpassword');
print("Autenticación: " + (authResult ? "OK" : "FALLÓ"));

if (!authResult) {
  print("❌ No se pudo autenticar. Abortando inicialización.");
  quit(1);
}

db = db.getSiblingDB('cli_agenda_db');

db.dropDatabase();

// ==========================================
// COLECCIÓN TASKS
// ==========================================
db.createCollection("tasks", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["text", "priority", "status", "created_at"],
      properties: {
        text: {
          bsonType: "string",
          minLength: 1,
          description: "Mandatory task text (cannot be empty)"
        },
        due_date: {
          bsonType: "date",
          description: "Optional due date"
        },
        priority: {
          enum: ["LOW", "MEDIUM", "HIGH"],
          description: "Mandatory priority (LOW, MEDIUM, HIGH)"
        },
        status: {
          enum: ["PENDING", "COMPLETED"],
          description: "Mandatory status (PENDING, COMPLETED)"
        },
        created_at: {
          bsonType: "date",
          description: "Mandatory creation date"
        }
      }
    }
  }
});

db.tasks.insertOne({
  text: "Learn how to connect Java with MongoDB",
  due_date: new Date("2026-03-15T23:59:00Z"),
  priority: "HIGH",
  status: "PENDING",
  created_at: new Date()
});

// ==========================================
// COLECCIÓN: NOTES
// ==========================================
db.createCollection("notes", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["title", "created_at"],
      properties: {
        title: { bsonType: "string", minLength: 1, description: "Mandatory note title" },
        content: { bsonType: "string", description: "Optional note content" },
        category: {
          enum: ["TRABAJO", "UNIVERSIDAD", "SOCIAL"],
          description: "Optional category (TRABAJO, UNIVERSIDAD, SOCIAL)"
        },
        created_at: { bsonType: "date", description: "Mandatory creation date" },
        updated_at: { bsonType: "date", description: "Optional last update date" }
      }
    }
  }
});

db.notes.insertOne({
  title: "Ideas para el proyecto final",
  content: "Recuerda aplicar el patrón DAO correctamente.",
  category: "UNIVERSIDAD",
  created_at: new Date(),
  updated_at: new Date()
});

// ==========================================
// COLECCIÓN: EVENTS
// ==========================================
db.createCollection("events", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["title", "start_date", "end_date", "created_at"],
      properties: {
        title: { bsonType: "string", minLength: 1, description: "Mandatory event title" },
        description: { bsonType: "string", description: "Optional event description" },
        start_date: { bsonType: "date", description: "Mandatory start date" },
        end_date: { bsonType: "date", description: "Mandatory end date" },
        location: { bsonType: "string", description: "Optional event location" },
        created_at: { bsonType: "date", description: "Mandatory creation date" },
        updated_at: { bsonType: "date", description: "Optional last update date" }
      }
    }
  }
});

db.events.insertOne({
  title: "Reunión de Sincronización (Daily)",
  description: "Revisar los avances de la CLI Agenda con el equipo",
  start_date: new Date("2026-03-06T10:00:00Z"),
  end_date: new Date("2026-03-06T10:30:00Z"),
  location: "Discord",
  created_at: new Date(),
  updated_at: new Date()
});

db.events.insertOne({
  title: "Cena con amigos",
  description: "Cumpleaños sorpresa",
  start_date: new Date("2026-04-12T20:00:00Z"),
  end_date: new Date("2026-04-12T23:59:59Z"),
  location: "Restaurante centro",
  created_at: new Date(),
  updated_at: new Date()
});

db.tasks.createIndex({ "status": 1 });
db.events.createIndex({ "start_date": 1 });
db.notes.createIndex({ "category": 1 });

print("✅ Base de datos CLI-Agenda inicializada con éxito.");