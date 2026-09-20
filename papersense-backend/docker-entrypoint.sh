#!/bin/sh
set -e

if [ -n "${DATABASE_URL:-}" ] && [ -z "${SPRING_DATASOURCE_URL:-}" ]; then
  case "$DATABASE_URL" in
    postgresql://*) export SPRING_DATASOURCE_URL="jdbc:${DATABASE_URL}" ;;
    postgres://*) export SPRING_DATASOURCE_URL="jdbc:postgresql://${DATABASE_URL#postgres://}" ;;
    *) export SPRING_DATASOURCE_URL="$DATABASE_URL" ;;
  esac
fi

exec java ${JAVA_OPTS:-} -jar /app/papersense-backend.jar
