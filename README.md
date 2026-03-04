# Títol del Projecte: CLI Agenda Java

Aplicació d'agenda per consola desenvolupada en Java 21 per a la gestió de tasques, esdeveniments 4
i notes. El projecte implementa persistència en base de dades SQL, arquitectura per capes, 
organització per funcionalitats, execució mitjançant Docker i testing amb JUnit 5.

Projecte desenvolupat com a treball final en equip aplicant bones pràctiques de disseny, modularitat 
i separació de responsabilitats.

---

## Funcionalitats

### Gestió de Tasques
- Crear, actualitzar i eliminar tasques
- Llistar totes les tasques
- Llistar tasques pendents i completades
- Marcar tasques com a completades

### Gestió d'Esdeveniments
- Crear, llistar, actualitzar i eliminar esdeveniments
- Llistar propers esdeveniments

### Gestió de Notes
- Crear, llistar, actualitzar i eliminar notes

### Característiques Tècniques
- Arquitectura en capes
- Organització per features (task, event, note)
- Patró DAO (Data Access Object)
- Ús de DTOs (Data Transfer Object) i Mappers
- Gestió d'excepcions personalitzada
- Aplicació CLI amb menús interactius
- Base de dades en contenidor Docker
- Scripts automàtics de creació i càrrega de dades
- Persistència en base de dades SQL
- Projecte gestionat amb Maven

---

## Tecnologíes utilitzades

|       Tecnologia       |           Ús          |
| ---------------------- | --------------------- |
| Java 21                | Lògica de negoci      |
| Maven                  | Gestió del projecte   |
| noSQL                  | Persistència de dades |
| MongoDB    (en Docker) | Base de dades         |
| Docker                 | Conteniderització     |
| JUnit 5                | Testing               |

---

## Requisits Previs

Abans d'executar el projecte, assegura't de tenir instal·lat:

- **Java 21** o superior
- **Maven** (per a la gestió de dependències i compilació)
- **Docker** i **Docker Compose** (per a la base de dades)
- **Git** (per clonar el repositori)

---

## Instruccions d'Instal·lació i Execució

### 1. Clonar el repositori

git clone https://github.com/aquinoajimenez/Project-Tasca-S3.04---Developers-Team.git

### 2. Configurar la base de dades amb Docker

Configurar la infraestructura de MongoDB mitjançant Docker per garantir un entorn local 
unificat sense instal·lacions manuals.
- El fitxer docker-compose.yml aixeca un contenidor amb MongoDB 6.0.
- L'script mongo-init.js inicialitza la base de dades automàticament al primer inici.
- La col·lecció users es configura amb validació estricta d'esquema i índexs únics per a 
email i username.
- La col·lecció tasks inclou validació d'esquema, enums per a priority i status, i una 
referència a l'usuari.
- Es creen automàticament un usuari de prova (Juan Garcia) i una tasca simulada per a 
proves locals.

Prova-ho localment:
- Obrir la terminal a la carpeta arrel del projecte.
- Executar docker compose up -d.
- Verificar a Docker Desktop que el contenidor cli_agenda_mongo estigui en funcionament.

### 3. Compilar el projecte amb Maven
mvn clean compile

### 4. Executar l'aplicació

mvn exec:java

### 5. Executar els tests

mvn test

### 6. Aturar la base de dades

docker-compose down

---

## Estructura del projecte

agenda_cli/
├── gitignore
├── docker-compose.yml
├── mongo-init.js
├── pom.xml
├── README.md
└── src/
    └── main/
    │   └── java/
    │      └── cli/
    │         └── agenda/
    │				├── Main.java
    │				├── application/
    │				│   ├── config/
    │				│   └── menu/
    │				├── common/	
    │				│   ├── exception/
    │				│   ├── mapper/
    │				│   ├── repository/
    │				│   └── utils/
    │				├── tasks/
    │				│   ├── dto/
    │				│   ├── exception/
    │				│   ├── mapper/
    │				│   ├── model/
    │				│   ├── repository/
    │				│   ├── service/
    │				│   └── cli/
    │				├── note/
    │				└── event/
    │				└── infrastructure/	
    │				├── mongo/
    │				│	├── codecs/
    │				│	└── repository/	
    │				│		├── task/
    │				│		├── note/
    │				│		└── event/
    │				└── config/
    └── test/
        └── java/
            └── cli/
                └── agenda/
			├── task/
			│   ├── service/
			│   └── repository/
			├── note/
			│   └── service/
			└── common/
    				└── utils/

---

## Autors

Projecte desenvolupat per:

* Adrià Quiñoa Jiménez
* Eduard Cantos Font
* Marc Fabregat

---

## Llicència

Aquest projecte **no disposa actualment de llícència definida**.
