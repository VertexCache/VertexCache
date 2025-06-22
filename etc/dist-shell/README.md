<p align="center">
  <img src="https://github.com/jasonlam604/VertexCache/blob/main/etc/assets/vertexcache-logo-192x192.png" alt="VertexCache Logo" width="100" height="100"/>
</p>

<h2 align="center">VertexCache Server Distribution</h2>

This directory contains shell scripts and related files **packaged with the official VertexCache server release**.  
These scripts are designed for easy execution of the VertexCache server and admin console in both Unix and Windows environments.

---

### Included Scripts

| File              | Description                                   |
|-------------------|-----------------------------------------------|
| `run_server.sh`   | Launches the VertexCache server (Linux/macOS) |
| `run_server.bat`  | Launches the VertexCache server (Windows)     |
| `run_console.sh`  | Starts the admin console client (Linux/macOS) |
| `run_console.bat` | Starts the admin console client (Windows)     |
| `VERSION`         | Server version identifier                     |


---

### Quick Start

On Unix/macOS:

```bash
chmod +x run_server.sh
./run_server.sh
```

On Windows:

```
run_server.bat
```

---

### About VertexCache

**VertexCache** is a high-performance, secure in-memory caching system with support for multi-level indexing (primary, idx1, idx2), clustering, and encrypted communication over a custom binary protocol (VCMP).

- GitHub: [github.com/vertexcache/vertexcache](https://github.com/vertexcache/vertexcache)
- Docs: [VertexCache Wiki](https://github.com/VertexCache/VertexCache/wiki)

---

> Released under the [Apache License 2.0](https://github.com/VertexCache/VertexCache/blob/main/LICENSE)  
> Maintained by **Jason Lam** – [github.com/jasonlam604](https://github.com/jasonlam604)
