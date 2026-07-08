# ============================================================================
#  Atlanta Vending Co. — OFFLINE lab setup (Windows / PowerShell)
#  Run this ONCE per student machine. Requires: JDK 21, Podman, git already
#  installed, and the Podman machine created (see README-OFFLINE.md).
#  After this, the labs build & run with NO internet using  ./gradlew --offline
# ============================================================================
$ErrorActionPreference = 'Stop'
$bundle = Split-Path -Parent $PSScriptRoot          # the offline-bundle root
$target = if ($args.Count -ge 1) { $args[0] } else { "$env:USERPROFILE\atl-vending" }

function Step($m) { Write-Host "`n==> $m" -ForegroundColor Cyan }

# --- 0. sanity ---
Step "Checking tools"
foreach ($t in 'java','podman','git') {
    if (-not (Get-Command $t -ErrorAction SilentlyContinue)) { throw "$t not found on PATH" }
    Write-Host "   $t : ok"
}

# --- 1. Podman machine up ---
Step "Ensuring the Podman machine is running"
$running = (podman machine list --format '{{.Running}}') -match 'true'
if (-not $running) { podman machine start }
podman info --format 'Podman {{.Version.Version}} ready' | Write-Host

# --- 2. Load the Dev Services images (Postgres + Kafka) ---
Step "Loading container images (Postgres, Kafka) — no registry needed"
podman load -i (Join-Path $bundle 'images\lab-devservices-images.tar')
if (Test-Path (Join-Path $bundle 'images\lab-native-builder-image.tar')) {
    Write-Host "   loading native builder image (for Lab 11)..."
    podman load -i (Join-Path $bundle 'images\lab-native-builder-image.tar')
}
podman images --format '   {{.Repository}}:{{.Tag}}' | Where-Object { $_ -match 'postgres|kafka|mandrel' }

# --- 3. Seed the Gradle cache + wrapper distribution ---
Step "Seeding the Gradle cache + wrapper (so nothing downloads)"
$gradleHome = "$env:USERPROFILE\.gradle"
New-Item -ItemType Directory -Force -Path $gradleHome | Out-Null
tar -xf (Join-Path $bundle 'gradle\gradle-home.tar') -C $gradleHome
Write-Host "   extracted to $gradleHome"

# --- 4. Get the lab repo (all lab tags) from the git bundle ---
Step "Creating the lab repo at $target"
if (Test-Path $target) {
    Write-Host "   $target already exists — skipping clone"
} else {
    git clone (Join-Path $bundle 'repo\atl-vending.bundle') $target
}

# --- 5. Env for offline Testcontainers/Podman (persistent) ---
Step "Setting offline-friendly environment variables"
[Environment]::SetEnvironmentVariable('DOCKER_HOST','npipe:////./pipe/docker_engine','User')
[Environment]::SetEnvironmentVariable('TESTCONTAINERS_RYUK_DISABLED','true','User')

Write-Host "`nDONE." -ForegroundColor Green
Write-Host @"

Next steps (all OFFLINE):
  cd $target
  git checkout lab-01-start
  .\scripts\gen-keys.sh          # (Git Bash) generate JWT keys once
  .\gradlew quarkusDev --offline

ALWAYS pass --offline so Gradle never touches the network:
  .\gradlew build --offline
  .\gradlew quarkusDev --offline

If Dev Services can't find Podman, reset it:
  podman machine stop; taskkill /F /IM win-sshproxy.exe; wsl --shutdown; sleep 5; podman machine start
"@
