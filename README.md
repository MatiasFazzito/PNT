# Programación en Nuevas Tecnologías (PNT) — BurgerFlow

Repositorio correspondiente a la materia **Programación en Nuevas Tecnologías (PNT)** de la carrera **Analista en Sistemas** de **Asociación ORT Argentina**.

## 👩‍🏫 Docente

**Anabella Muñoz**

## 👥 Integrantes del grupo

- **Luciano Vivinetto**
- **Matias Meta**
- **Matias Fazzito**

## 🎓 Carrera

**Analista en Sistemas** — **Asociación ORT Argentina**

---

Este repositorio contiene la aplicación **BurgerFlow** (Spring Boot 3 + HTML/JS/CSS), desarrollada como proyecto integrador durante la cursada.

---

## 🛠️ Requisitos Previos

Antes de comenzar, asegurate de tener instalado en tu computadora:

- [Git](https://git-scm.com/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (debe estar iniciado antes de correr el proyecto).

---

## 🚀 Cómo levantar el proyecto con Docker

El proyecto está completamente contenedorizado con **Docker Compose**, lo que levanta de forma automática la aplicación Spring Boot y la base de datos de manera integrada.

### 1. Clonar el repositorio
Abrí una terminal y ejecutá:
```bash
git clone <URL_DEL_REPOSITORIO>
cd <NOMBRE_DE_LA_CARPETA>
```

### 2. Iniciar el entorno
Asegurate de tener **Docker Desktop iniciado** y ejecutá:
```bash
docker compose up --build -d
```
*(Si usás una versión antigua de Docker, el comando es `docker-compose up --build -d`).*

### 3. Acceder a la aplicación
Abrí tu navegador e ingresá a:
```text
http://localhost:8080
```

### 4. Ver logs en tiempo real (Opcional)
Si querés monitorear los logs del backend o la base de datos:
```bash
docker compose logs -f
```

### 5. Detener el proyecto
Para apagar los contenedores cuando termines de trabajar:
```bash
docker compose down
```
---

## 💻 Ejecución Directa con Maven (Modo Local)

Si querés levantar la aplicación Spring Boot directamente desde tu terminal utilizando el wrapper de Maven:

- **En Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```
- **En Windows (CMD / PowerShell):**
  ```cmd
  mvnw.cmd spring-boot:run
  ```

---

## 📌 Comandos Útiles de Git

Para mantener tu copia local actualizada y subir cambios al repositorio:

| Acción | Comando |
| :--- | :--- |
| **Traer cambios remotos** | `git pull` |
| **Preparar cambios para guardar** | `git add .` |
| **Guardar cambios localmente** | `git commit -m "Descripción de lo realizado"` |
| **Subir cambios a GitHub** | `git push origin main` |
| **Ver estado de los archivos** | `git status` |