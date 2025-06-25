#!/bin/bash

java -Xms1g -Xmx2g -XX:+UseG1GC -XX:+AlwaysPreTouch -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags -jar vertex-bench.jar '{
  "testName": "get-only",
  "threads": 100,
  "duration": 60,
  "percentageReads": 70,
  "percentageWrites": 25,
  "totalKeyCount": 5000,
  "clientId": "sdk-client-java",
  "clientToken": "ea143c- a valid token goes here ffe4a6c7",
  "serverHost": "localhost",
  "serverPort": 50505,
  "enableTlsEncryption": true,
  "enablePreload": true,
  "encryptionMode": "ASYMMETRIC",
  "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqyx+GpSo6UmkrvXknkmpS----- This is one line with \n --------+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----\n"
}'