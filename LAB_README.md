# Atlanta Vending Co. — Quarkus Bootcamp Reference App

The complete reference solution for the **NobleProg Quarkus 2-Day Bootcamp**
(Norfolk Southern). Every one of the 11 labs is merged into this single,
runnable Quarkus application. It is configured to run on **Podman** (no Docker).

- Package root: `co.atlvending`
- Quarkus 3.37.x · Java 21 · Maven
- Container runtime: **Podman** (rootless)

---

## Prerequisites (already set up on this machine)

| Tool | Location |
|------|----------|
| JDK 21 | `C:\Program Files\Java\jdk-21.0.11` |
| Maven 3.9.16 | `C:\Users\andre\dev\apache-maven-3.9.16\bin\mvn` |
| Podman 6.0.0 (portable) | `C:\Users\andre\dev\podman\podman.exe` |
| Podman machine | `podman-machine-default` (rootless, WSL2) |

### One-time shell setup (each new terminal)

No Testcontainers env vars are needed — the Podman/Ryuk settings are baked into
the project (`src/main/resources/testcontainers.properties`). You only need JDK,
Maven/Gradle, and Podman on your PATH (already added to your user PATH):

```bash
export JAVA_HOME="/c/Program Files/Java/jdk-21.0.11"
export PATH="/c/Users/andre/dev/podman:/c/Users/andre/dev/apache-maven-3.9.16/bin:$PATH"
```

PowerShell equivalent:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.11"
$env:Path="C:\Users\andre\dev\podman;C:\Users\andre\dev\apache-maven-3.9.16\bin;$env:Path"
```

### Make sure Podman is running
```bash
podman machine start        # if not already running
podman info                 # should print host details, rootless=true
```
The Podman machine does **not** auto-start after a reboot or logoff — run
`podman machine start` at the beginning of each session (it's the first thing to
check if anything container-related fails).

### Podman troubleshooting (read this if a lab hangs)
**Symptom:** Dev Services hangs or times out starting Postgres, or you see
`Could not find a valid Docker environment`, or Hibernate reports
`Datasource '<default>' ... URL is not set`. These almost always mean the
Podman ↔ Windows pipe went stale (common after sleep/VPN/idle), **not** a bug in
your code.

**Fix — reset the Podman machine (WSL):**
```powershell
podman machine stop
wsl --shutdown
Start-Sleep -Seconds 5      # let WSL fully tear down BEFORE starting again
podman machine start
```
- The `Start-Sleep` matters: running `podman machine start` too soon gives
  `Error: connection "podman-machine-default" not found`. If you see that, just
  run `podman machine start` once more — it succeeds after WSL settles.
- This does **not** delete anything — your cached images and the machine survive.

**Cert error pulling the postgres image** (corporate proxy / self-signed TLS)?
Mark docker.io insecure inside the machine, once per machine:
```powershell
podman machine ssh "sudo mkdir -p /etc/containers/registries.conf.d && printf '[[registry]]\nlocation = \"docker.io\"\ninsecure = true\n' | sudo tee /etc/containers/registries.conf.d/99-insecure.conf"
```
(Or pre-pull with `podman pull --tls-verify=false docker.io/library/postgres:16-alpine`.)

### Generate the JWT keys (first time after cloning)
The RSA key pair is **not** committed (it must never live in a public repo).
Generate it once before the first run:
```bash
./scripts/gen-keys.sh       # writes src/main/resources/{privateKey,publicKey}.pem
```

> **How Podman is wired in.** Quarkus Dev Services uses Testcontainers
> (docker-java) to auto-start Postgres and Kafka. With `podman machine` running,
> Podman forwards the Docker API on the `//./pipe/docker_engine` named pipe, so
> docker-java finds it automatically — the most common "it can't find Docker"
> error just means the Podman machine is stopped (`podman machine start`).
>
> The one incompatibility is **Ryuk** (Testcontainers' reaper), which won't run
> on rootless Podman. Ryuk can only be disabled with the
> `TESTCONTAINERS_RYUK_DISABLED=true` **environment variable** (Testcontainers
> ignores a `ryuk.disabled` properties entry). It's set in three places so every
> workflow is covered without any manual shell setup:
> - **Maven** `test`/`verify` → surefire/failsafe `<environmentVariables>` in `pom.xml`
> - **Gradle** `test`/`build` → `tasks.withType(Test) { environment ... }` in `build.gradle`
> - **Dev mode / IDE** → a persistent user environment variable on this machine
>
> Dev Services clean up their own containers, so Ryuk isn't needed.

---

## Run it

This project ships **both** a Maven and a Gradle build — use whichever you prefer.
They share the same `src/`, `application.properties`, and Podman wiring.

**Maven:**
```bash
cd C:/Users/andre/Quarkus/atl-vending
./mvnw quarkus:dev
```

**Gradle:**
```bash
cd C:/Users/andre/Quarkus/atl-vending
./gradlew quarkusDev
```

On startup Quarkus (via Podman) spins up a throwaway **Postgres** and a
**Redpanda (Kafka)** container automatically — no manual `podman run` needed.
Open the Dev UI at <http://localhost:8080/q/dev-ui>.

---

## What each lab maps to

| Lab | Feature | Key files |
|-----|---------|-----------|
| 01 | Dev mode / hot reload | `GreetingResource` (`/welcome`) |
| 02 | Jakarta REST + validation + exception mapper | `api/MachineResource`, `api/ProductResource`, `api/exception/*` |
| 03 | CDI services + `@InjectMock` | `service/InventoryService`, `service/PricingService`, `MachineResourceMockTest` |
| 04 | Type-safe config + profiles | `config/VendingConfig`, `StartupLogger`, `application.properties` |
| 05 | Panache + Postgres (Dev Services) | `domain/Machine`, `domain/Product`, `import.sql` |
| 06 | Testing (REST Assured, Testcontainers) | `src/test/java/...` |
| 07 | Reactive SSE with Mutiny | `api/SaleResource` (`/sales/feed`), `messaging/SaleEventBus` |
| 08 | Kafka sale events + restock monitor | `messaging/SaleEvent`, `RestockMonitor`, `RestockAlertLogger`, `api/AlertResource` (GET `/restock-alerts`) |
| 09 | JWT security (OPERATOR / ADMIN) | `@RolesAllowed` on `MachineResource`, `api/TokenResource`, `*.pem` |
| 10 | Observability (metrics/health/tracing) | `health/InventoryReadiness`, metrics in `SaleResource` |
| 11 | Native + Kubernetes | `application.properties` (jib, kubernetes, native.container-runtime=podman) |

> **Persistence note:** the reference app uses **imperative** Hibernate ORM +
> Panache throughout (one coherent persistence model). Lab 07's reactive piece
> is demonstrated with Mutiny + Server-Sent Events over an in-memory broadcast
> bus, so the app stays a single runnable whole rather than mixing classic and
> reactive Panache.

---

## Demo script (copy/paste)

```bash
# --- REST + persistence (Labs 2, 5) ---
curl localhost:8080/machines
curl localhost:8080/machines/1
curl localhost:8080/machines/9999          # 404 + JSON error
curl "localhost:8080/machines?status=OFFLINE"
curl localhost:8080/products

# validation (Lab 2)
curl -X POST -H 'Content-Type: application/json' -d '{"sku":""}' localhost:8080/products   # 400

# --- Reactive SSE live feed (Lab 7) ---
# terminal 1:
curl -N localhost:8080/sales/feed
# terminal 2:
curl -X POST -H 'Content-Type: application/json' \
  -d '{"machineId":1,"productSku":"COKE-12","priceCents":250}' localhost:8080/sales

# --- Kafka restock alerts (Lab 8) ---
# Each slot starts at 8 units; an alert fires once stock drops to 5 or below.
# Post several sales for one slot, then read the alerts that made the full
# Kafka round-trip (POST /sales -> "sales" topic -> RestockMonitor ->
# "restock-alerts" topic -> RestockAlertLogger):
for i in $(seq 1 6); do
  curl -s -o /dev/null -X POST -H 'Content-Type: application/json' \
    -d '{"machineId":1,"productSku":"WATER-16","priceCents":150}' localhost:8080/sales
done
curl localhost:8080/restock-alerts     # JSON list of alerts (remaining 5,4,3,2 ...)

# --- JWT security (Lab 9) ---
curl -i -X POST -H 'Content-Type: application/json' \
  -d '{"sku":"COKE-12","quantity":5}' localhost:8080/machines/1/restock   # 401 (anonymous)

# grab a signed token from the dev-only helper, then retry:
TOKEN=$(curl -s "localhost:8080/dev/token?role=OPERATOR")
curl -i -X POST -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"sku":"COKE-12","quantity":5}' localhost:8080/machines/1/restock   # 204

# --- Observability (Lab 10) ---
curl localhost:8080/q/health
curl localhost:8080/q/metrics | grep sales_recorded
```

## Build / test / package

**Maven:**
```bash
./mvnw verify                       # unit + integration tests (uses Podman Dev Services)
./mvnw package                      # runnable JAR in target/quarkus-app/
./mvnw package -Dquarkus.container-image.build=true   # build image with Jib (no daemon)
./mvnw package -Pnative -Dquarkus.native.container-build=true   # native (Podman builder)
```

**Gradle** (equivalent tasks):
```bash
./gradlew build                     # compile + test (uses Podman Dev Services)
./gradlew quarkusBuild              # runnable JAR in build/quarkus-app/
./gradlew build -Dquarkus.container-image.build=true            # build image with Jib
./gradlew build -Dquarkus.package.jar.type=uber-jar            # uber-jar, etc.
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true  # native
```

> Both builds read the same `src/main/resources/application.properties`, so the
> Podman env vars above apply identically. Maven outputs to `target/`, Gradle to
> `build/` — both are git-ignored.

## JWT keys
`src/main/resources/{privateKey,publicKey}.pem` are **demo-only** RSA keys and
are **git-ignored** (never committed to this public repo). Generate them with
`scripts/gen-keys.sh` (requires openssl) before the first build/run.
