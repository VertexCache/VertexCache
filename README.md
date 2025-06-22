<p align="center">
  <img src="https://github.com/jasonlam604/VertexCache/blob/main/etc/assets/vertexcache-logo-192x192.png" alt="VertexCache Logo" width="120" height="120"/>
</p>

<h1 align="center">VertexCache</h1>

**VertexCache** is a high-performance, open-source in-memory caching system built for speed, security, and developer control.  
Designed with modern workloads in mind, it supports powerful multi-index lookups (primary, secondary, tertiary), encrypted communication, and flexible configuration — all backed by a custom binary protocol for low-latency messaging.

VertexCache is lightweight, secure by default, and includes official SDKs for multiple languages.

---

### Key Features

- Multi-index caching — Efficient retrieval via primary, secondary, and tertiary keys
- Custom binary protocol (VCMP) — Built for performance, security, and wire efficiency
- Encrypted communication — TLS, RSA, and AES-GCM built-in
- Pluggable eviction algorithms — LRU, LFU, TinyLFU, ARC, FIFO, and more
- Minimal config — Simple `.env`-style configuration, no XML or YAML required
- Built-in console client — Interactive shell for admin access
- Developer SDKs — Official clients in Java, Python, Go, C#, Rust, Elixir, PHP, and more
- Fully open-source — Apache 2.0 license

---

### Get Started

- [Download the latest release](https://github.com/jasonlam604/VertexCache/releases)
- [Quick Start Guide](https://github.com/vertexcache/VertexCache/wiki/Quick-Start-Guide)
- [Full Documentation & Wiki](https://github.com/vertexcache/VertexCache/wiki)

---

### Learn More

- Configuration: env-based setup for server, console, logging, and encryption
- Encryption: TLS + key-based encryption (RSA, AES-GCM) — easily toggled per environment
- Metrics & Logging: Fine-grained logs with Log4J and structured metrics support
- Commands: PING, SET, GET, DEL plus indexed queries
- Eviction: Fully pluggable caching algorithms with real-world benchmarks
- Distribution Contents: See [Installation → What’s Inside](https://github.com/VertexCache/VertexCache/wiki/Installation#Whats-Inside) for included files and layout

Explore these topics in detail in the [VertexCache Wiki](https://github.com/vertexcache/VertexCache/wiki)

---

Maintained by **Jason Lam** – [github.com/jasonlam604](https://github.com/jasonlam604)  
Licensed under the [Apache License 2.0](https://github.com/VertexCache/VertexCache/blob/main/LICENSE)
