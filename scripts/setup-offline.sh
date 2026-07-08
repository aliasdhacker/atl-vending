#!/usr/bin/env bash
# Atlanta Vending Co. — OFFLINE lab setup (Git Bash). Run once per machine.
# Requires JDK 21, Podman, git on PATH and a Podman machine already created.
set -euo pipefail
BUNDLE="$(cd "$(dirname "$0")/.." && pwd)"
TARGET="${1:-$HOME/atl-vending}"

echo "==> Checking tools"
for t in java podman git tar; do command -v "$t" >/dev/null || { echo "$t not on PATH"; exit 1; }; done

echo "==> Ensuring Podman machine is running"
podman machine list --format '{{.Running}}' | grep -q true || podman machine start

echo "==> Loading container images (Postgres, Kafka)"
podman load -i "$BUNDLE/images/lab-devservices-images.tar"
[ -f "$BUNDLE/images/lab-native-builder-image.tar" ] && podman load -i "$BUNDLE/images/lab-native-builder-image.tar" || true

echo "==> Seeding Gradle cache + wrapper into ~/.gradle"
mkdir -p "$HOME/.gradle"
tar -xf "$BUNDLE/gradle/gradle-home.tar" -C "$HOME/.gradle"

echo "==> Cloning lab repo to $TARGET"
[ -d "$TARGET" ] || git clone "$BUNDLE/repo/atl-vending.bundle" "$TARGET"

echo ""
echo "DONE. Next (all OFFLINE):"
echo "  cd \"$TARGET\""
echo "  git checkout lab-01-start"
echo "  ./scripts/gen-keys.sh          # generate JWT keys once"
echo "  ./gradlew quarkusDev --offline"
echo ""
echo "Always pass --offline. If Dev Services can't find Podman, reset:"
echo "  podman machine stop; taskkill //F //IM win-sshproxy.exe; wsl --shutdown; sleep 5; podman machine start"
