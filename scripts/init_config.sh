#!/bin/bash

source ./scripts/common/prompt-utils.sh
source ./scripts/common/copy-utils.sh

# Call the confirmation prompt
confirm_execution "This will initialize the vertex-cache-config folder, removing any existing files beforehand. Are you sure you want to proceed? (yes/no): "

# Create VertexCache Server and Console Client Config folders and setup content
mkdir -p ./vertex-cache-config/console
mkdir -p ./vertex-cache-config/server

# Copy over example .env files
copy_file "./etc/config/env-example-server" "./vertex-cache-config/server/.env"
copy_file "./etc/config/env-example-console" "./vertex-cache-config/console/.env"

# Copy over the Test TLS Certs
copy_file "./etc/config/test_server_keystore.jks" "./vertex-cache-config/server/test_server_keystore.jks"
copy_file "./etc/config/test_server_certificate.pem" "./vertex-cache-config/console/test_server_certificate.pem"

# Copy over the Log4J Configuration Files
copy_file "./etc/config/log4j2-vertexcache-server.xml" "./vertex-cache-config/server/log4j2-vertexcache-server.xml"
copy_file "./etc/config/log4j2-vertexcache-console.xml" "./vertex-cache-config/console/log4j2-vertexcache-console.xml"