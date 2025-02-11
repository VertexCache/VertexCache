<!--
  Title: VertexCache
  Description: VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a range of algorithms and offers multi-index caching capabilities, allowing for efficient data retrieval using one primary key and two secondary keys. 
 
  Author: jasonlam604
  -->
<meta name='keywords' content='in-memory cache, caching, java, data structure, database'>

# What is VertexCache
VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a 
range of algorithms and offers **multi-index** caching capabilities, allowing for efficient data retrieval using one primary 
key and **two secondary keys (optional)**.

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

**Beyond Getting Started**
* VertexCache Server Configuration
* VertexCache Console Configuration
* Reading Log File and Configuration
* Using Native Clients like PHP
* Understanding the Message Protocol VCMP abbreviated for VertexCacheProtocolMessage

**Releases**
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

**Available Clients**
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
 * What's in a release, the file structure
 * MonoRepo and Fat JAR approach
 * Technology stack used
 * Message Protocol - VCMP otherwise know as VertexCacheProtocolMessage
 * Design Patterns used for ease of adding new Commands and new Caching Algorithms

**How to Contribute**
 * Developer Guide for VertexCache server changes
 * Developer Guide to add a new client
 * Developer Guide to update an existing client
 * Log an issue or bug

---   

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
You will given a VertexCache Prompt

```console
VertexCache Console, localhost:50505> _
```

The first part **VertexCache Console** is just indicating this is the VertexCache Console and **localhost:50505** is where the console running in this case localhost and on the default port of 50505.  Yes
this is configurable, see the property configuration file section.


# Test It

## Ping
In the VertexCache Console type:

Request:
```console
VertexCache Console, localhost:50505> ping
```

Response:
```console
VertexCache Console, localhost:50505> ping
+PONG
```

## Set, Get and Remove with Primary Index and Value
Let's move beyond a simple "Hello World" example. Instead, let's consider a scenario where we want to store basic user information in the cache after a successful login to avoid unnecessary I/O calls to the database.

Example User Json Object:
```json
{
  "first_name": "John",
  "last_name": "doe",
  "email": "john.doe@fake-domain.com",
  "username": "rocketman"
}
```

As well, let's say the associated unique ID you have to the user in your database is the following UUID *0194ed3a-5d8f-7689-8b57-3a72cd2da3d8*

Request to **set** the User Object in Cache:
Request and Response:
```console
VertexCache Console, localhost:50505> set 0194ed3a-5d8f-7689-8b57-3a72cd2da3d8 {"first_name":"John","last_name":"doe","email":"john.doe@fake-domain.com","username":"rocketman"}
+OK
```

A successful response of *+OK* is expected, with no return value other than an acknowledgment that it was set.




## Set, Get and Remove with Primary and Secondary Indexes 


## Server Output / Logs
**Note**: The server output is displayed because the default configuration file (~/vertex-cache-config/server/vertex-cache-server.properties) has *enable_verbose* set to *true*.
