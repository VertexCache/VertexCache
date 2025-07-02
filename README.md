<p align="center">
  <img src="https://github.com/jasonlam604/VertexCache/blob/main/etc/assets/vertexcache-logo-192x192.png" alt="VertexCache Logo" width="120" height="120"/>
</p>

<h1 align="center">VertexCache</h1>

**VertexCache** is a fast, secure, open-source in-memory caching system built for modern workloads.  
Designed as a developer-first solution, VertexCache combines multi-index lookups, wire-efficient messaging, and built-in encryption — all optimized for performance and control.

Lightweight by design. Secure by default. High-performance TCP clients in your language of choice.

---

### Our Mission

**To do one thing and do it well — caching — guided by the core tenets of Security, Flexibility, and Simplicity.**

---

### Why VertexCache?

- **Multi-Index Caching** — Query by primary, secondary, or tertiary keys  
- **Multi-Tenant Support** — Isolate tenants and enforce role-based access with the AuthModule  
- **Custom Binary Protocol (VCMP)** — Low-latency, wire-efficient messaging  
- **Encrypted Communication** — TLS, RSA, AES-GCM built-in  
- **Pluggable Eviction** — LRU, LFU, TinyLFU, ARC, FIFO, and more  
- **Minimal Config** — Simple `.env`-style setup, no YAML or XML required  
- **First-Class Developer SDKs** — Official high-performance **TCP clients**, not REST wrappers:  
  - **C#, Elixir, Go, Java, Kotlin, Node.js, PHP, Python, Ruby, Rust**  
- **Optional REST API** — Lightweight HTTP access for environments where TCP isn't suitable  
- **Fully Open-Source** — Apache 2.0 license  

---

### Available Modules

VertexCache is modular — extend functionality only when needed:

- **AdminModule** — Powerful admin commands: `purge`, `metrics`, `config`, `reload`, `reset`, `session`, `shutdown`, `status`  
- **AlertModule** — Webhook alerts for external monitoring; integrates with SmartModule  
- **AuthModule** — Secure, role-based client authentication with multi-tenant support  
- **ClusterModule** — Hot-standby clustering for high availability  
- **MetricModule** — Real-time performance metrics and cache insights  
- **RateLimiterModule** — Built-in traffic rate limiting  
- **RestAPIModule** — Optional REST API for simplified HTTP integration  
- **SmartModule** — Advanced features including indexing and future TTL optimization  

---

### Load Testing & Benchmarking

- **VertexBench** — Official load testing suite for stress testing, QPS analysis, and real-world scenario validation  
Ideal for tuning configurations, validating cluster performance, and measuring throughput under load.

---

### Built for Real-World Complexity

Under the hood, VertexCache prioritizes interoperability, fault-tolerance, and security:

- **Binary Protocol Enforcement** — VCMP ensures minimal overhead with predictable framing  
- **Layered Encryption Stack** — Combine transport security (TLS) with message-layer RSA or AES-GCM  
- **Cluster-Ready by Design** — Hot-standby, promotable secondaries with automatic or manual failover  
- **Modular Activation** — Deploy only what you need: SmartModule, AdminModule, Rate Limiting, Alerts  
- **Cross-Language SDK Consistency** — Identical protocol handling across all official TCP clients  
- **Predictable Operational Behavior** — Console-driven diagnostics, detailed metrics, and admin commands  

All engineered to deliver fast, controlled caching — without sacrificing security or operational clarity.

---

### Get Started

- [Download the latest release](https://github.com/jasonlam604/VertexCache/releases)  
- [Quick Start Guide](https://github.com/vertexcache/VertexCache/wiki/Quick-Start-Guide)  
- [Full Documentation & Wiki](https://github.com/vertexcache/VertexCache/wiki)  

---

### Learn More

- `.env`-style configuration for server, console, logging, and encryption  
- Built-in support for TLS + key-based encryption (RSA, AES-GCM)  
- Multi-tenant isolation and role enforcement via the AuthModule  
- Structured metrics with fine-grained logging via Log4J  
- Command set includes `PING`, `SET`, `GET`, `DEL`, and indexed lookups  
- Real-world benchmark data for eviction algorithms  
- [What’s Inside the Distribution](https://github.com/VertexCache/VertexCache/wiki/Installation#Whats-Inside)  

Explore these topics in detail in the [VertexCache Wiki](https://github.com/vertexcache/VertexCache/wiki)

---

Maintained by **Jason Lam** – [github.com/jasonlam604](https://github.com/jasonlam604)  
Licensed under the [Apache License 2.0](https://github.com/VertexCache/VertexCache/blob/main/LICENSE)  
