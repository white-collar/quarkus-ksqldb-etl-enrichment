#!/usr/bin/env bash
set -e
curl -X POST -H "Content-Type: application/json" http://localhost:8083/orders -d '{"orderId":"o500","userId":"u1","productId":"p100","amount":1200.00,"timestamp":'$(date +%s)'}'
curl -X POST -H "Content-Type: application/json" http://localhost:8083/orders -d '{"orderId":"o501","userId":"u2","productId":"p200","amount":120.00,"timestamp":'$(date +%s)'}'
echo "\nProduced sample orders"
