#!/bin/bash

# Note this is setup  VertexCache Server and VertexCache Console Client
#
# Config Setup for Client-SDKs are individual managed in each of the SDKs
#

# Enable Common Scripts for use
source ./scripts/common/prompt-utils.sh
source ./scripts/common/copy-utils.sh

# Call the confirmation prompt
confirm_execution "This will initialize the vertex-cache-config folder, removing any existing files beforehand. Are you sure you want to proceed? (yes/no): "

# Create VertexCache Server and Console Client Config folders and setup content
mkdir -p ./vertex-cache-config/console
mkdir -p ./vertex-cache-config/server

# Copy over example .env files
copy_file "./etc/config/env-example-server" "./vertex-cache-config/server/.env"
copy_file "./etc/config/env-example-server" "./vertex-cache-config/server/.env.node-b"
copy_file "./etc/config/env-example-server" "./vertex-cache-config/server/.env.node-c"

sed -i '' 's/^cluster_node_id=.*/cluster_node_id=node-b/' ./vertex-cache-config/server/.env.node-b
sed -i '' 's/^cluster_node_id=.*/cluster_node_id=node-c/' ./vertex-cache-config/server/.env.node-c


copy_file "./etc/config/env-example-client" "./vertex-cache-config/console/.env"

# Copy over the Test TLS Certs
copy_file "./etc/config/test_tls_certificate.pem" "./vertex-cache-config/server/test_tls_certificate.pem"
copy_file "./etc/config/test_tls_private_key.pem" "./vertex-cache-config/server/test_tls_private_key.pem"

copy_file "./etc/config/test_tls_certificate.pem" "./vertex-cache-config/console/test_tls_certificate.pem"

# Copy over the Test Public/Private Keys, for VertexCache Server and Console Client
copy_file "./etc/config/test_private_key.pem" "./vertex-cache-config/server/test_private_key.pem"
copy_file "./etc/config/test_public_key.pem" "./vertex-cache-config/server/test_public_key.pem"
copy_file "./etc/config/test_public_key.pem" "./vertex-cache-config/console/test_public_key.pem"

# Copy over the Log4J Configuration Files
copy_file "./etc/config/log4j2-vertexcache-server.xml" "./vertex-cache-config/server/log4j2-vertexcache-server.xml"
copy_file "./etc/config/log4j2-vertexcache-console.xml" "./vertex-cache-config/console/log4j2-vertexcache-console.xml"

