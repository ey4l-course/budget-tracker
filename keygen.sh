#!/bin/bash

mkdir -p ~/keys
openssl genrsa -out ~/keys/private_key.pem 2048
openssl rsa -in ~/keys/private_key.pem -pubout -out ./keys/public_key.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in ~/keys/private_key.pem -out ~/keys/private_key_pkcs8.pem
chmod 400 ~/keys/private_key.pem ~/keys/private_key_pkcs8.pem
echo "Done! Files are in ~/keys"
