# Runbook — `inventory-platform`

Guía rápida y clara para levantar la demo localmente en Windows, Linux y macOS.
(Aclaracion: las pruebas se realizaron en un sistema operativo Windows.)
---

## 0) Requisitos mínimos

* **Java JDK**: 17 (o la versión indicada en el `pom.xml` padre)
* **Maven**: 3.8+ (recomendado 3.9.x)
* **Docker** y **Docker Compose v2**
* **Puertos libres**: `9092` (Kafka), `8081` (order), `8082` (inventory), `8083` (sync)
* **RAM** sugerida: 2–4 GB libres

> Verifica Java/Maven:
> `java -version` • `mvn -version`
> Verifica Docker/Compose:
> `docker --version` • `docker compose version`

---

## 1) Levantar Kafka con Docker Compose

En la raíz del proyecto (donde está `docker-compose.yml`):

```bash
docker compose up -d
```

* Esto levanta **Kafka** (y Zookeeper si corresponde).
* Para ver el estado: `docker compose ps`
* Para ver logs: `docker compose logs -f`

---

Si se presenta un error de :
```bash
unable to get image 'bitnami/zookeeper:3': error during connect: Get "http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine/v1.51/images/bitnami/zookeeper:3/json": open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified.
```
Es necesario iniciar el Docker Engine, abriendo el Docker Desktop es suficiente.

## 2) Compilar y empaquetar los servicios

Desde la **raíz** del repo:

```bash
mvn -q -DskipTests -pl order-service,inventory-service,sync-service -am clean package
```

Esto construye los 3 JARs en cada `target/`.

---

## 3) Ejecución por SO

### A) Windows (PowerShell)

1. Crear carpeta de logs (si no existe):

```powershell
New-Item -ItemType Directory -Force -Path "logs" | Out-Null
```

2. Levantar cada servicio:

```powershell
# Order (8081)
Start-Process -FilePath "java" -ArgumentList '-jar order-service/target/order-service-0.0.1.jar --server.port=8081 --spring.kafka.bootstrap-servers=localhost:9092' -RedirectStandardOutput "logs/order-service.log" -NoNewWindow -PassThru | ForEach-Object { $_.Id | Out-File "logs/order-service.pid" -Encoding ascii }

# Inventory (8082)
Start-Process -FilePath "java" -ArgumentList '-jar inventory-service/target/inventory-service-0.0.1.jar --server.port=8082 --spring.kafka.bootstrap-servers=localhost:9092' -RedirectStandardOutput "logs/inventory-service.log" -NoNewWindow -PassThru | ForEach-Object { $_.Id | Out-File "logs/inventory-service.pid" -Encoding ascii }

# Sync (8083)
Start-Process -FilePath "java" -ArgumentList '-jar sync-service/target/sync-service-0.0.1.jar --server.port=8083 --spring.kafka.bootstrap-servers=localhost:9092 --inventory.api.base.url=http://localhost:8082' -RedirectStandardOutput "logs/sync-service.log" -NoNewWindow -PassThru | ForEach-Object { $_.Id | Out-File "logs/sync-service.pid" -Encoding ascii }
```

* Logs: `logs/*.log`
* PIDs: `logs/*.pid`

**Detener (PowerShell):**

```powershell
Get-Content logs/order-service.pid | ForEach-Object { Stop-Process -Id $_ -ErrorAction SilentlyContinue }
Get-Content logs/inventory-service.pid | ForEach-Object { Stop-Process -Id $_ -ErrorAction SilentlyContinue }
Get-Content logs/sync-service.pid | ForEach-Object { Stop-Process -Id $_ -ErrorAction SilentlyContinue }
```

---

### B) Linux / macOS (bash/zsh)

1. Crear carpeta de logs:

```bash
mkdir -p logs
```

2. Levantar servicios (en background, con logs y PID):

```bash
# Order (8081)
nohup java -jar order-service/target/order-service-0.0.1.jar \
  --server.port=8081 \
  --spring.kafka.bootstrap-servers=localhost:9092 \
  > logs/order-service.log 2>&1 & echo $! > logs/order-service.pid

# Inventory (8082)
nohup java -jar inventory-service/target/inventory-service-0.0.1.jar \
  --server.port=8082 \
  --spring.kafka.bootstrap-servers=localhost:9092 \
  > logs/inventory-service.log 2>&1 & echo $! > logs/inventory-service.pid

# Sync (8083)
nohup java -jar sync-service/target/sync-service-0.0.1.jar \
  --server.port=8083 \
  --spring.kafka.bootstrap-servers=localhost:9092 \
  --inventory.api.base.url=http://localhost:8082 \
  > logs/sync-service.log 2>&1 & echo $! > logs/sync-service.pid
```

* Logs: `tail -f logs/<service>.log`
* PIDs: `cat logs/<service>.pid`

**Detener (bash/zsh):**

```bash
xargs kill -9 < logs/order-service.pid 2>/dev/null || true
xargs kill -9 < logs/inventory-service.pid 2>/dev/null || true
xargs kill -9 < logs/sync-service.pid 2>/dev/null || true
```

---

## 4) Apagar y limpiar

* **Servicios Java**: ver secciones de “Detener” según SO.
* **Kafka** (Docker):

  ```bash
  docker compose down
  ```
* (Opcional) borrar volúmenes si los hubiera:

  ```bash
  docker compose down -v
  ```

---

## 5) Notas y Troubleshooting

* Si **Kafka** no está listo, los servicios pueden reintentar. Esperá unos segundos o revisá `docker compose logs -f`.
* Si un puerto está ocupado, cambiá `--server.port` y el `--inventory.api.base.url` en `sync-service`.
* Verifica que `JAVA_HOME` apunte a tu JDK y que `java` esté en `PATH`.

---
