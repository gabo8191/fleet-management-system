
# 🚚 Sistema de Gestión de Flota - TransLogistics

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1%2B-brightgreen)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-orange)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-24.0%2B-blue)](https://www.docker.com/)
[![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-Enabled-brightgreen)](www.github.com/features/actions)


Aplicación web para la gestión eficiente de vehículos, conductores, viajes y mantenimientos en flotas de transporte.

## 🌟 Características Principales
- **Gestión de Vehículos**: Registro y seguimiento de vehículos (placa, modelo, año, tipo).
- **Asignación de Conductores**: Asignación flexible de conductores a vehículos.
- **Seguimiento de Viajes**: Registro y actualización en tiempo real de viajes.
- **Mantenimientos**: Programación de mantenimientos preventivos y correctivos.
- **Seguridad**: Autenticación de usuarios y control de acceso basado en roles.

## 🛠️ Tecnologías Utilizadas
- **Backend**: 
  - Java 17 | Spring Boot 3 | Hibernate | Maven
- **Frontend**: 
  - Thymeleaf | Bootstrap 5 | HTML5/CSS3
- **Base de Datos**: 
  - PostgreSQL | Docker
- **DevOps**: 
  - GitHub Actions | Docker Compose |

## 🚀 Instalación
### Requisitos Previos
- Java 17+
- Maven 3.9+
- Docker 24.0+
- PostgreSQL 16+

### Ejecución con Docker
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/tu-repositorio.git
   cd tu-repositorio
   ```
2. Inicia los servicios:
   ```bash
   docker-compose up -d
   ```
3. Accede a la aplicación: http://localhost:8080

### Instalación Manual
1. Configura la base de datos (via Docker o local):
   ```bash
   docker-compose up -d fleet-management-db
   ```
2. Ejecuta la aplicación:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## ⚙️ Configuración
Archivo `application.properties`:
```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/fleet-management
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Thymeleaf
spring.thymeleaf.cache=false
```

Variables de entorno recomendadas para producción:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/fleet-prod
export SPRING_DATASOURCE_PASSWORD=your_secure_password
```

## 📚 Documentación
- **Diagrama de Clases**:
- [CHANGELOG.md](CHANGELOG.md) 

## 🤝 Contribución
1. Haz fork del proyecto
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Realiza commits siguiendo [Conventional Commits](https://www.conventionalcommits.org/):
   ```bash
   git commit -m "feat: add new vehicle registration endpoint"
   ```
4. Abre un Pull Request contra la rama `develop`


## ✨ Equipo de Desarrollo
| Nombre           | Rol                                 |
|------------------|-------------------------------------|
| Gabriel Castillo | Desarrollador Principal             |
| Sebastian Cañon  | Desarrollador Secundario            |
| Oscar Gonzalez   | Frontend Principal                  |
| Jhon Castro      | Frontend Secundario                 |
| Juan Zarate      | Analista de documentación y gestión |