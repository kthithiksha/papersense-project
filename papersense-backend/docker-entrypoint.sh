#!/bin/sh
set -e

# Handle Render's DATABASE_URL or SPRING_DATASOURCE_URL
TARGET_URL="${SPRING_DATASOURCE_URL:-${DATABASE_URL:-}}"

if [ -n "$TARGET_URL" ]; then
  case "$TARGET_URL" in
    jdbc:*)
      export SPRING_DATASOURCE_URL="$TARGET_URL"
      ;;
    postgres://*|postgresql://*)
      # Strip prefix
      NO_PREFIX=$(echo "$TARGET_URL" | sed -E 's#^postgres(ql)?://##')
      
      # Extract host and dbname (everything after @)
      HOST_DB=$(echo "$NO_PREFIX" | sed -E 's#^.*@##')
      
      export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOST_DB}"
      
      # Extract user and pass if embedded in URL
      USER_PASS=$(echo "$NO_PREFIX" | grep '@' | cut -d'@' -f1 || true)
      if [ -n "$USER_PASS" ]; then
        DB_USER=$(echo "$USER_PASS" | cut -d':' -f1)
        DB_PASS=$(echo "$USER_PASS" | cut -d':' -f2-)
        [ -n "$DB_USER" ] && export DB_USERNAME="$DB_USER"
        [ -n "$DB_PASS" ] && export DB_PASSWORD="$DB_PASS"
      fi
      ;;
  esac
fi

echo "Starting Spring Boot with Datasource URL: ${SPRING_DATASOURCE_URL:-default}"
exec java ${JAVA_OPTS:-} -jar /app/papersense-backend.jar
