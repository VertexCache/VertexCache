<!--
  Title: VertexCache
  Description: VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a range of algorithms and offers multi-index caching capabilities, allowing for efficient data retrieval using one primary key and two secondary keys. 
 
  Author: jasonlam604
  -->
<meta name='keywords' content='in-memory cache, caching, java, data structure, database'>

# VertexCache

## What is VertexCache?
VertexCache is a straightforward in-memory caching system designed with a strong emphasis on security. It supports a 
range of algorithms and offers multi-index caching capabilities, allowing for efficient data retrieval using one primary 
key and two secondary keys.

VertexCache is implemented in Java and uses a straightforward, string-based protocol call VCMP (VertexCacheProtocolMessage) for message delivery, which is transmitted over the wire as bytes.

## Features

* Security 
  * No Encryption / Plain Transport
  * Encryption during Transport via TLS (optional)
  * Encryption at the message layer using Public/Private keys (optional)
  * Encryption during Transport and message layer (optional)
* Logging that utilized Log4J
* Built in Console for interactivity
* Configuration managed by a properties file

## Security

## Available Caching Algorithms


## Property File Configuration

### Example
