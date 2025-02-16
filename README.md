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
* [Quick Start in 60 Seconds](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#getting-started-in-60-seconds)
  * [Prerequisites](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#prerequisites)
  * [Start Server](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#start-server)
    * [Start Server on MacOS/Linux](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#start-server-on-macoslinux)
    * [Start Server on Windows](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#start-server-on-windows)
    * [Server Started Output](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#server-started-output)
  * [Start Console Client](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#start-console-client)
    * [Start Console Client on MacOS/Linux](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#start-console-client-on-macoslinux)
    * [Start Console Client on Windows](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#start-console-client-on-windows)
    * [Console Client Started Output](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#console-client-started-output)
* [Test It](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#test-it)
* [Logs](https://github.com/jasonlam604/VertexCache/wiki/Quick-Start-Guide#logs)

**Features and Configuration**
* [Security](#Security) 
  * No Encryption / Plain Transport
  * Encryption during Transport via TLS (optional)
  * Encryption at the message layer using Public/Private keys (optional)
  * Encryption during Transport and message layer (optional)
* [Eviction Policies and Cache Algorithms](#eviction-policies-and-cache-algorithms)
* [Interactive Console](#interactive-console)
* [Logging](#logging)
* [Easy Configuration](#easy-configuration)
  * VertexCache Server Configuration
  * VertexCache Console Configuration

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
   
