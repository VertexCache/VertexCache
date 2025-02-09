<!--
  Title: VertexCache
  Description: VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a range of algorithms and offers multi-index caching capabilities, allowing for efficient data retrieval using one primary key and two secondary keys. 
 
  Author: jasonlam604
  -->
<meta name='keywords' content='in-memory cache, caching, java, data structure, database'>

# What is VertexCache
VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a 
range of algorithms and offers multi-index caching capabilities, allowing for efficient data retrieval using one primary 
key and two secondary keys.

VertexCache is implemented in Java and uses a straightforward, string-based protocol call VCMP (VertexCacheProtocolMessage) for message delivery, which is transmitted over the wire as bytes.

**Getting Started Fast**
* [Quick Start in 60 Seconds](#quick-start-in-60-seconds)
  * [Prerequisites](#prerequisites)
  * [Start Server](#start-server)
    * [Start Server on MacOS/Linux](#start-server-on-macoslinux)
    * [Start Server on Windows](#start-server-on-windows)
    * [Server Started Output](#server-started-output)
  * [Start Console Client](#start-console-client)
    * [Start Console Client on MacOS/Linux](#start-console-client-on-macoslinux)
    * [Start Console Client on Windows](#start-console-client-on-windows)
    * [Console Client Started Output](#console-client-started-output)
* [Test It](#test-it)

**Releases**
* What's Inside
* Release History

**Features**
* [Security](#Security) 
  * No Encryption / Plain Transport
  * Encryption during Transport via TLS (optional)
  * Encryption at the message layer using Public/Private keys (optional)
  * Encryption during Transport and message layer (optional)
* [Eviction Policies and Cache Algorithms](#eviction-policies-and-cache-algorithms)
* [Interactive Console](#interactive-console)
* [Logging](#logging)
* [Easy Configuration](#easy-configuration)
* [Client Libraries](#client-libraries)
  * C#
  * Elixir
  * Go
  * Java
  * Lua
  * Node
  * PHP
  * Python
  * Rust   

**Architecture and Design Overview**
 * Coming Soon

**How to Contribute**
 * Contribute to the Sever Code
 * Create a Client
 * 

   

# Quick Start in 60 seconds
The following section will guide you through running the VertexCache Server locally and using the Console Client to quickly test cache commands. This quick start setup includes secure transport via TLS and message encryption using public/private keys.

To make the setup as seamless as possible, test certificates and keys are provided for convenience.

⚠️ Warning: DO NOT use these test certificates and keys in a production environment, as they are publicly shared in this GitHub repository.

## Prerequisites
- Prerequisites: Java Version 21.0.2 (this was the latest it was compiled with)
- Install: Visit [releases](https://github.com/jasonlam604/VertexCache/releases) and download the latest release.

### Start Server

#### Start Server on MacOS/Linux
Instructions for MacOS/Linux VertexCache server.
On MacOS or Linux first ensure the script is runnable

```bash
chmod +x run_server.sh
```

Now Execute the file
```bash
run_server.sh
```

---

#### Start Server on Windows
```bash
run_server.bat
```
---
#### Server Started Output
```console
VertexCache:
  Version: 1.0.0
  Port: 50505
  Cache Eviction Policy: LRU (Least Recently Used), Least Recently Used
  Cache Size (only applies when eviction is not NONE): 100000
  Transport Layer Encryption Enabled: Yes
  Message Layer Encryption Enabled: Yes
  Config file set: Yes
  Config file loaded with no errors: Yes
  Config file location: ./vertex-cache-config/server/vertex-cache-server.properties
  Status: Server Started
```

### Start Console Client

#### Start Console Client on MacOS/Linux
On MacOS or Linux first ensure the script is runnable

```bash
chmod +x run_console.sh
```

Now Execute the file
```bash
run_console.sh
```
---
#### Start Console Client on Windows
```bash
run_console.bat
```
---
#### Console Client Started Output
```console
VertexCache Console:
  Version: 1.0.0
  Host: localhost
  Port: 50505
  Message Layer Encryption Enabled: Yes
  Transport Layer Encryption Enabled: Yes
  Transport Layer Verify Certificate: Yes
  Config file set: Yes
  Config file loaded with no errors: Yes
  Config file location: ./vertex-cache-config/console/vertex-cache-console.properties
Status: OK, Console Client Started
```

# Test It

## Ping

## Set Primary Index and Value

## Get By Primary Index

## Remove Primary Index

## Set Primary and Secondary Indexes 

## Get By Seconardy Indexes


