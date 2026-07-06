#!/usr/bin/env bash
# Lab 09 — generate the RSA key pair used for JWT signing/verification.
# Regenerates src/main/resources/{privateKey,publicKey}.pem.
#
# The committed keys are DEMO ONLY — never reuse them for anything real.
set -euo pipefail
cd "$(dirname "$0")/.."

openssl genpkey -algorithm RSA -out src/main/resources/privateKey.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/privateKey.pem -out src/main/resources/publicKey.pem

echo "Wrote src/main/resources/privateKey.pem and publicKey.pem"
echo "Grab a demo token once the app is running in dev mode:"
echo '  curl -s "http://localhost:8080/dev/token?role=OPERATOR"'
