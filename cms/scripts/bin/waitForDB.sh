#!/bin/sh

project_path=$(pwd)
project_name="${project_path##*/}"

seconds=1
until sudo docker container exec $project_name-postgres-1 psql -U dbuser -d minicms -c "select 1" > /dev/null 2>&1; do
  >&2 echo "Postgres is starting, already wait for $seconds seconds..."
  sleep 1
  seconds=$(expr $seconds + 1)
done
>&2 echo "Postgres is started"
exit 0
