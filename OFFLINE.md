# Atlanta Vending Co. — Offline / Air-gapped Lab Bundle

Everything the 11–12 labs pull from the internet, packaged so the class runs
with **no network connection**. Build one USB/zip and each student machine
becomes self-contained.

> **Note:** this file and `scripts/setup-offline.{ps1,sh}` are the reference
> docs/scripts and live in the repo. The ~2.4 GB of artifacts they use (image
> tarballs, Gradle cache, git bundle) are **not** stored in git — the instructor
> builds the USB bundle by placing these scripts next to those artifacts (see the
> last section). Paths like `images/`, `gradle/`, `repo/` are relative to that
> bundle folder, not the repo.

## What's in the bundle
| Path | What | Size |
|------|------|------|
| `images/lab-devservices-images.tar` | Postgres + Kafka images for Dev Services (labs 5–10) | ~430 MB |
| `images/lab-native-builder-image.tar` | Mandrel native-builder image (`ubi9-...:jdk-25`) for lab 11 native builds | ~1.2 GB |
| `gradle/gradle-home.tar` | Full Gradle dependency cache **and** the Gradle 9.5.1 wrapper distribution | ~825 MB |
| `repo/atl-vending.bundle` | The lab git repo with **all lab tags** (lab-01-start … lab-11-start) | ~110 KB |
| `scripts/setup-offline.ps1` | One-shot per-machine setup | — |

## Prerequisite (one-time, needs connectivity OR pre-provisioned)
Each machine needs these installed **before** going offline — do this during a
setup window with internet, or from your existing class-1 laptops (which already
have them):
- **JDK 21**, **git**, and **Podman** on `PATH`.
- A **Podman machine** created and startable: `podman machine init` then
  `podman machine start`. (`init` pulls a machine image from quay.io, so it must
  be done while online. Once created, it works offline forever after.)

### Creating the Podman machine (the one thing that needs the internet once)
`podman machine init` pulls a machine-OS image from quay.io, so it must be run
**while online, once** — after that the machine works offline forever. Do this
per laptop during a setup window with connectivity (your class-1 laptops already
have it).

> **Fully air-gapped fresh laptop** (never had Podman): the reliable way is to
> provision the machine on one "golden" laptop online, then clone that laptop's
> WSL distro to the others:
> ```powershell
> # on the golden laptop (machine already created):
> wsl --export podman-machine-default podman-machine.tar
> # on a fresh laptop:
> wsl --import podman-machine-default C:\podman-machine podman-machine.tar --version 2
> ```
> Validate this on ONE spare laptop before class — WSL/Podman machine
> registration is version-sensitive. The clean, low-risk path remains: create
> the machine online once per laptop.

### Lab 11 — native build (offline)
The Mandrel builder image is in this bundle (loaded by the setup script). Build
a Linux/amd64 native binary fully offline with (note `jar.enabled=false` — Gradle
needs it explicit, unlike Maven's `-Dnative` profile):
```bash
./gradlew build --offline \
  -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false \
  -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman -x test
# -> build/atl-vending-1.0-SNAPSHOT-runner  (ELF x86-64, runs on Debian amd64)
```

## Setup (per machine, OFFLINE)
From this bundle folder, in **PowerShell**:
```powershell
.\scripts\setup-offline.ps1
```
It will: start the Podman machine → `podman load` the images → unpack the Gradle
cache into `~/.gradle` → clone the repo to `%USERPROFILE%\atl-vending` → set the
offline env vars. Pass a path to clone elsewhere: `.\scripts\setup-offline.ps1 C:\labs\atl-vending`.

## Running the labs — always add `--offline`
```powershell
cd $env:USERPROFILE\atl-vending
git checkout lab-01-start
./gradlew quarkusDev --offline
```
The `--offline` flag tells Gradle to use only the seeded cache and never reach
the network. Same for `./gradlew build --offline`.

- Dev Services (Postgres, Kafka) start from the **loaded images** — no registry,
  no pull, no cert errors.
- JWT keys (lab 9+) are git-ignored — generate once per checkout in Git Bash:
  `./scripts/gen-keys.sh`.

## If Dev Services can't find Podman
The reset (works in PowerShell; Git Bash uses `//F //IM`):
```powershell
podman machine stop
taskkill /F /IM win-sshproxy.exe    # "not found" is fine
wsl --shutdown
sleep 5
podman machine start
```

## Verifying a machine is truly offline-ready
Disconnect the network, then:
```powershell
cd $env:USERPROFILE\atl-vending
git checkout lab-06-start
./gradlew build --offline          # should compile + resolve everything from cache
./gradlew quarkusDev --offline     # Dev Services Postgres/Kafka come up from loaded images
```

## Rebuilding this bundle (instructor, needs internet)
See `scripts/build-bundle.md` — or just re-run the steps: `podman save` the
images, `tar` up `~/.gradle/{caches/modules-2,caches/jars-9,wrapper/dists}`, and
`git bundle create atl-vending.bundle --all`.
