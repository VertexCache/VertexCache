<!--
  Title: VertexCache
  Description: VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a range of algorithms and offers multi-index caching capabilities, allowing for efficient data retrieval using one primary key and two secondary keys. 
 
  Author: jasonlam604
  -->
<meta name='keywords' content='in-memory cache, caching, java, data structure, database'>

# What is VertexCache?
VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a 
range of algorithms and offers multi-index caching capabilities, allowing for efficient data retrieval using one primary 
key and two secondary keys.

VertexCache is implemented in Java and uses a straightforward, string-based protocol call VCMP (VertexCacheProtocolMessage) for message delivery, which is transmitted over the wire as bytes.

# Getting Started

## Run It Now!
- Prerequisites: Java Version 21.0.2 (this was the latest it was compiled with)
- Install: Visit [releases](https://github.com/jasonlam604/VertexCache/releases) and download the latest release.

### MacOS/Linux!
On MacOS or Linux first start the server by invoking at command line ~/run_console.sh

### Windows
On Windows first start the server by invoking at command line ~/run_server.bat

# Quick Overview of what is inside a Release

## Individual File Explanation
Visit [releases](https://github.com/jasonlam604/VertexCache/releases) and download the latest release.

Unzip the contents, the file structure will containt the following:

```
/root
|
|-/logs
|
|-/vertex-cache-config
|  |
|  |-/console
|  |  |-log4j2-vertexcache-console.xml
|  |  |-test_server_certificate.pem
|  |  |-vertex-cache-console.properties
|  |
|  |-/server
|     |-log4j2-vertexcache-server.xml
|     |-test_server_certificate.crt
|     |-test_server_certificate.der
|     |-test_server_keystore.jks
|     |-vertex-cache-server.properties
|
|-README.txt
|
|-run_console.bat
|-run_console.sh
|
|-run_server.bat
|-run_server.sh
|
|-vertex-cache-console-X.X.X.jar (where X.X.X is the latest release version)
|-vertex-cache-server-X.X.X.jar (where X.X.X is the latest release version)
|

```




---

# Features

* [Security](#Security) 
  * No Encryption / Plain Transport
  * Encryption during Transport via TLS (optional)
  * Encryption at the message layer using Public/Private keys (optional)
  * Encryption during Transport and message layer (optional)
* [Eviction Policies and Cache Algorithms](#eviction-policies-and-cache-algorithms)
* [Interactive Console](#interactive-console)
* [Logging](#logging)
* [Easy Configuration](#easy-configuration)

# Security

# Eviction Policies and Cache Algorithms

# Interactive Console

# Logging

# Easy Configuration

## Property File location

## Property File Example


---

# VertexCache Developer Guide 

## Overview

## Source Code Layout

## Compiling & Running
