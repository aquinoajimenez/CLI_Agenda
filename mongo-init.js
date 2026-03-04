db = db.getSiblingDB('cli_agenda_db');

// ==========================================
// COLECCIÓN USERS
// ==========================================
db.createCollection("users", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["username", "password", "name", "surname", "email"],
      properties: {
        username: { bsonType: "string", minLength: 1, description: "Mandatory, non-empty string" },
        password: { bsonType: "string", minLength: 1, description: "Mandatory, non-empty string" },
        name: { bsonType: "string", minLength: 1, description: "Mandatory, non-empty string" },
        surname: { bsonType: "string", minLength: 1, description: "Mandatory, non-empty string" },
        email: { bsonType: "string", minLength: 1, description: "Mandatory, non-empty string" }
      }
    }
  }
});

db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });

var testUser = db.users.insertOne({
  username: "juanelgrande",
  password: "passwd123",
  name: "Juan",
  surname: "Garcia",
  email: "juan.garcia@cliagenda.com"
});
var userId = testUser.insertedId;


// ==========================================
// COLECCIÓN TASKS
// ==========================================
db.createCollection("tasks", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["user_id", "text", "priority", "status", "created_at"],
      properties: {
        user_id: {
          bsonType: "objectId",
          description: "Mandatory user ID reference"
        },
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
  user_id: userId,
  text: "Learn how to connect Java with MongoDB",
  due_date: new Date("2026-03-15T23:59:00Z"),
  priority: "HIGH",
  status: "PENDING",
  created_at: new Date()
});

db.tasks.createIndex({ "user_id": 1 });

print("✅ Base de datos CLI-Agenda inicializada con éxito (Users y Tasks blindados).");