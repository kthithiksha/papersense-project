#!/bin/sh
set -e

if [ -n "${DATABASE_URL:-}" ]; then
  # Convert postgres:// or postgresql:// to jdbc:postgresql://
  CLEAN_URL=$(echo "$DATABASE_URL" | sed -E 's#^postgres(ql)?://##')
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${CLEAN_URL}"
fi

exec java ${JAVA_OPTS:-} -jar /app/papersense-backend.jar
