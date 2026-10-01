#!/usr/bin/env bash

set -eu

CONTAINER=house-share-server-keycloak-1
BASE=/opt/keycloak
REALM=house-share

docker exec $CONTAINER mkdir -p $BASE/realm
docker exec $CONTAINER $BASE/bin/kc.sh export --dir $BASE/realm --realm $REALM --users different_files
docker cp $CONTAINER:$BASE/realm ./docker/keycloak
docker exec $CONTAINER rm -r $BASE/realm