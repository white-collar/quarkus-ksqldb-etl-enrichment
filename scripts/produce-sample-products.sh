#!/usr/bin/env bash
set -e
curl -X POST -H "Content-Type: application/json" http://localhost:8082/products -d '{"productId":"p100","name":"iPhone 15","category":"mobile","price":1200.00}'
curl -X POST -H "Content-Type: application/json" http://localhost:8082/products -d '{"productId":"p200","name":"Kindle","category":"ebook","price":120.00}'
echo "\nProduced sample products"
