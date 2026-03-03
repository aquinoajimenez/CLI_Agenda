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

_Pendents_

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

_Descripció dels principals paquets i mòduls_

---

## Autors

Projecte desenvolupat per:

* Adrià Quiñoa Jiménez
* Eduard Cantos Font
* Marc Fabregat

---

## Llicència

Aquest projecte **no disposa actualment de llícència definida**.
