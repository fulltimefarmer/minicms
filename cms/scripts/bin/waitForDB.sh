#!/bin/sh

# Configuration
project_path=$(pwd)
project_name="${project_path##*/}"
MAX_WAIT_TIME=300  # Maximum wait time in seconds (5 minutes)
RETRY_INTERVAL=2   # Wait interval between retries

# Initialize counters
seconds=0
attempts=0

echo "Waiting for PostgreSQL to start..."

# Wait for PostgreSQL container to be running first
until docker ps --filter "name=${project_name}-postgres-1" --filter "status=running" --format "{{.Names}}" | grep -q "${project_name}-postgres-1"; do
  >&2 echo "Waiting for PostgreSQL container to start... (${seconds}s)"
  sleep $RETRY_INTERVAL
  seconds=$(expr $seconds + $RETRY_INTERVAL)
  
  if [ $seconds -ge $MAX_WAIT_TIME ]; then
    >&2 echo "ERROR: PostgreSQL container failed to start within ${MAX_WAIT_TIME} seconds"
    exit 1
  fi
done

echo "PostgreSQL container is running, checking database connection..."

# Wait for PostgreSQL to be ready to accept connections
until docker container exec $project_name-postgres-1 pg_isready -U dbuser -d minicms >/dev/null 2>&1; do
  attempts=$(expr $attempts + 1)
  >&2 echo "PostgreSQL is starting, attempt #${attempts} (waited ${seconds}s)..."
  sleep $RETRY_INTERVAL
  seconds=$(expr $seconds + $RETRY_INTERVAL)
  
  if [ $seconds -ge $MAX_WAIT_TIME ]; then
    >&2 echo "ERROR: PostgreSQL failed to start within ${MAX_WAIT_TIME} seconds"
    exit 1
  fi
done

# Final verification with actual database query
if docker container exec $project_name-postgres-1 psql -U dbuser -d minicms -c "SELECT 1;" >/dev/null 2>&1; then
  >&2 echo "✅ PostgreSQL is ready! (Total wait time: ${seconds}s)"
  exit 0
else
  >&2 echo "ERROR: PostgreSQL container is running but database is not accessible"
  exit 1
fi
