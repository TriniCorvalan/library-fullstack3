#!/bin/sh
set -eu

: "${TNS_ADMIN:=/app/wallet}"

if [ -n "${ORACLE_WALLET_BASE64:-}" ]; then
  umask 077
  wallet_archive="$(mktemp)"
  trap 'rm -f "$wallet_archive"' EXIT HUP INT TERM

  if ! printf '%s' "$ORACLE_WALLET_BASE64" | base64 --decode > "$wallet_archive"; then
    echo "Unable to decode the Oracle wallet secret." >&2
    exit 1
  fi

  listing="$(jar tf "$wallet_archive")"

  if printf '%s\n' "$listing" | grep -Eq '(^/|(^|/)\.\.(/|$))'; then
    echo "The Oracle wallet archive contains an unsafe path." >&2
    exit 1
  fi

  if ! printf '%s\n' "$listing" | grep -Eq '(^|/)tnsnames\.ora$'; then
    echo "The Oracle wallet archive does not contain tnsnames.ora." >&2
    exit 1
  fi

  mkdir -p "$TNS_ADMIN"
  (
    cd "$TNS_ADMIN"
    jar xf "$wallet_archive"
  )
  rm -f "$wallet_archive"
  trap - EXIT HUP INT TERM
  unset ORACLE_WALLET_BASE64
elif [ ! -f "$TNS_ADMIN/tnsnames.ora" ]; then
  echo "Provide ORACLE_WALLET_BASE64 or mount a wallet with tnsnames.ora at $TNS_ADMIN." >&2
  exit 1
fi

exec java -jar /app/library.jar
