#!/bin/sh
set -eu

# Render provides PostgreSQL URLs as postgresql://..., while Spring expects
# the JDBC equivalent. Local MySQL configuration remains unchanged.
if [ -n "${DATABASE_URL:-}" ] && [ -z "${SPRING_DATASOURCE_URL:-}" ]; then
  case "$DATABASE_URL" in
    postgresql://*) export SPRING_DATASOURCE_URL="jdbc:${DATABASE_URL}" ;;
    postgres://*) export SPRING_DATASOURCE_URL="jdbc:postgresql://${DATABASE_URL#postgres://}" ;;
    *) export SPRING_DATASOURCE_URL="$DATABASE_URL" ;;
  esac
fi

exec java ${JAVA_OPTS:-} -jar /app/papersense-backend.jar
