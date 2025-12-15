#!/usr/bin/env bash
set -e
curl -X POST -H "Content-Type: application/json" http://localhost:8081/users -d '{"userId":"u1","name":"Eugene","status":"active","country":"UA"}'
curl -X POST -H "Content-Type: application/json" http://localhost:8081/users -d '{"userId":"u2","name":"Anna","status":"active","country":"DE"}'
echo "\nProduced sample users"
