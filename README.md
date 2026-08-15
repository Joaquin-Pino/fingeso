# PGT — Plataforma de Gestión de Tesistas

Guía para levantar el proyecto completo en local: base de datos, backend y frontend.

## Servicios

| Servicio | Tecnología | Puerto |
|---|---|---|
| Base de datos | PostgreSQL | 5432 |
| Backend | Spring Boot (Java 21, Maven) | 8080 |
| Frontend | Vue 3 + Vite | 5173 |

Los tres deben estar corriendo al mismo tiempo para usar la app completa. Backend y frontend van en dos terminales separadas (cada uno se queda "pegado" mientras corre).

---

## 1. PostgreSQL

### Instalar

- **Fedora / RHEL**
  ```bash
  sudo dnf install postgresql-server postgresql-contrib
  sudo postgresql-setup --initdb
  sudo systemctl enable --now postgresql
  ```
- **Ubuntu / Debian**
  ```bash
  sudo apt update
  sudo apt install postgresql postgresql-contrib
  sudo systemctl enable --now postgresql
  ```
- **macOS (Homebrew)**
  ```bash
  brew install postgresql@16
  brew services start postgresql@16
  ```
- **Windows**: instalador oficial desde https://www.postgresql.org/download/windows/ (o usa WSL y sigue las instrucciones de Ubuntu de arriba).

### Crear la base de datos y el usuario

El backend, por defecto (`fingeso/src/main/resources/application.properties`), espera:
- host `localhost:5432`, base `fingeso`, usuario `postgres`, password `postgres`.

```bash
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';"
sudo -u postgres createdb fingeso
```

En macOS/Windows, si tu instalación no crea un rol `postgres` con superusuario por defecto, usa el usuario que te haya dejado el instalador y ajusta `application.properties` en consecuencia.

### Verificar que está arriba

```bash
pg_isready -h localhost -p 5432
psql -h localhost -U postgres -d fingeso -c '\dt'
```

### Arrancar (si no quedó como servicio del sistema)

```bash
# Fedora/Ubuntu
sudo systemctl start postgresql

# macOS
brew services start postgresql@16
```

---

## 2. Backend (Spring Boot)

### Instalar

Solo necesitas el JDK — el proyecto trae Maven Wrapper (`./mvnw`), no hace falta instalar Maven aparte.

- **Fedora / RHEL**: `sudo dnf install java-21-openjdk`
- **Ubuntu / Debian**: `sudo apt install openjdk-21-jdk`
- **macOS**: `brew install openjdk@21`
- **Windows**: instalador de [Adoptium Temurin 21](https://adoptium.net/) o `winget install EclipseAdoptium.Temurin.21.JDK`

Verifica con `java -version` (necesitas 21 o superior).

### Configurar (opcional)

- Si tu PostgreSQL usa otro usuario/password/puerto, edítalo en `fingeso/src/main/resources/application.properties` (`spring.datasource.*`).
- Las notificaciones por correo (OP-10, al subir un avance) son opcionales para desarrollo local: si no defines `MAIL_USERNAME` / `MAIL_PASSWORD` como variables de entorno, el sistema sigue funcionando normal, solo no logra enviar el correo (queda anotado en el log, no rompe la subida).

### Arrancar

```bash
cd fingeso
./mvnw spring-boot:run
```

Queda escuchando en `http://localhost:8080`. En el primer arranque, si la base está vacía, `DataSeeder` crea automáticamente usuarios y tesis de prueba (ver tabla más abajo).

---

## 3. Frontend (Vue 3 + Vite)

### Instalar

- **Fedora / RHEL**: `sudo dnf install nodejs`
- **Ubuntu / Debian**: `sudo apt install nodejs npm` (o usa [nvm](https://github.com/nvm-sh/nvm) para una versión más reciente)
- **macOS**: `brew install node`
- **Windows**: instalador oficial desde https://nodejs.org/

Necesitas Node 20 o superior (`node -v`). npm viene incluido con Node.

### Instalar dependencias del proyecto (solo la primera vez, o cuando cambie `package.json`)

```bash
cd fingeso/front
npm install
```

### Arrancar

```bash
npm run dev
```

Queda escuchando en `http://localhost:5173`. En dev, Vite reenvía automáticamente `/api/*` al backend en `localhost:8080` (configurado en `vite.config.js`), así que no hay problemas de CORS aunque el backend no tenga CORS configurado.

Más detalle específico del frontend (estructura de carpetas, variables de entorno) en [`fingeso/front/README.md`](front/README.md).

---

## Orden recomendado para levantar todo

1. PostgreSQL corriendo (paso 1).
2. Terminal 1: `cd fingeso && ./mvnw spring-boot:run` — déjala abierta.
3. Terminal 2: `cd fingeso/front && npm run dev` — déjala abierta.
4. Abre `http://localhost:5173` en el navegador.

## Usuarios de prueba (sembrados automáticamente)

Password para todos: **`Password123!`**

| Nombre | Email | Rol |
|---|---|---|
| Ana Rojas | ana.rojas@usach.cl | Profesor |
| Carlos Muñoz | carlos.munoz@usach.cl | Profesor |
| Diego Fernández | diego.fernandez@usach.cl | Tesista |
| Valentina Soto | valentina.soto@usach.cl | Tesista |
| Martín Herrera | martin.herrera@usach.cl | Tesista |

---

## Problemas comunes

**Error 500 al subir un avance, log dice `violates check constraint "historial_cambio_tipo_cambio_check"`**
Tu base de datos local se creó (con `ddl-auto=update`) antes de que el enum `TipoCambio` incluyera `ENTREGA_AVANCE`, y Hibernate no actualiza los *check constraints* de columnas ya existentes. Arréglalo con:
```sql
ALTER TABLE historial_cambio DROP CONSTRAINT historial_cambio_tipo_cambio_check;
ALTER TABLE historial_cambio ADD CONSTRAINT historial_cambio_tipo_cambio_check
  CHECK (tipo_cambio IN ('REASIGNACION_GUIA','MODIFICACION_TITULO','CAMBIO_ESTADO','CAMBIO_COGUIA','ENTREGA_AVANCE'));
```
O, si no te importa perder los datos locales, `DROP DATABASE fingeso; CREATE DATABASE fingeso;` y vuelve a arrancar el backend — recrea el esquema desde cero y `DataSeeder` vuelve a sembrar los datos de prueba.

**Un Tesista recibe 403 al listar/ver tesis**
Asegúrate de estar sobre una rama que incluya el fix de `SecurityConfig` (`hasAnyRole("PROFESOR", "TESISTA")` en `/api/tesis/**`); la versión original solo permitía `PROFESOR`.

**Puerto 8080 o 5173 ya en uso**
Revisa si quedó un proceso de una corrida anterior: `lsof -i :8080` / `lsof -i :5173`, y mátalo antes de volver a arrancar.

**Login da 401**
Verifica que el email sea exactamente el de la tabla de usuarios de prueba y la contraseña `Password123!` (sensible a mayúsculas/símbolos).

**Ejecutar solo los tests del backend** (no requiere Postgres corriendo, usa H2 en memoria)
```bash
cd fingeso
./mvnw test
```
