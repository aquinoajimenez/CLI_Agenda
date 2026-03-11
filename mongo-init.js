print("🚀 Iniciant inicialització de la base de dades...");

// Crear/Usar base de dades principal
db = db.getSiblingDB('cli_agenda_db');

// ==========================================
// COLECCIÓ TASKS
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
// COLECCIÓ NOTES
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
          enum: ["WORK", "UNIVERSITY", "SOCIAL"],
          description: "Optional category (WORK, UNIVERSITY, SOCIAL)"
        },
        created_at: { bsonType: "date", description: "Mandatory creation date" },
        updated_at: { bsonType: "date", description: "Optional last update date" }
      }
    }
  }
});

db.notes.insertOne({
  title: "Ideas for the final project",
  content: "Remember to apply DAO pattern correctly.",
  category: "UNIVERSITY",
  created_at: new Date(),
  updated_at: new Date()
});

// ==========================================
// COLECCIÓ EVENTS
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
  title: "Daily Sync Meeting",
  description: "Review CLI Agenda progress with the team",
  start_date: new Date("2026-03-06T10:00:00Z"),
  end_date: new Date("2026-03-06T10:30:00Z"),
  location: "Discord",
  created_at: new Date(),
  updated_at: new Date()
});

db.events.insertOne({
  title: "Dinner with friends",
  description: "Surprise birthday",
  start_date: new Date("2026-04-12T20:00:00Z"),
  end_date: new Date("2026-04-12T23:59:59Z"),
  location: "Downtown restaurant",
  created_at: new Date(),
  updated_at: new Date()
});

// ==========================================
// ÍNDEXS
// ==========================================
db.tasks.createIndex({ "status": 1 });
db.events.createIndex({ "start_date": 1 });
db.notes.createIndex({ "category": 1 });

print("✅ Base de dades 'cli_agenda_db' inicialitzada correctament (sense autenticació).");