<p align="center">
  <img src="https://github.com/jasonlam604/VertexCache/blob/main/etc/assets/vertexcache-logo-192x192.png" alt="VertexCache Logo" width="100" height="100"/>
</p>

<h2 align="center">VertexBench Distribution</h2>

This directory contains shell scripts and related files **packaged with the official VertexBench release**.  
These scripts are designed for easy execution of the VertexBench load testing and benchmarking tool on Unix and Windows environments.

---

### Included Scripts
See the [VertexBench Wiki](https://github.com/VertexCache/VertexCache/wiki/Load-Testing-with-VertexBench) for a full breakdown of usage and configuration options.

| File                   | Description                                     |
|------------------------|-------------------------------------------------|
| `run_vertexbench.sh`   | Launches VertexBench (Linux/macOS)              |
| `run_vertexbench.bat`  | Launches VertexBench (Windows)                  |
| `VERSION`              | VertexBench version identifier                  |

---

### Quick Start

Refer to the [VertexBench Wiki](https://github.com/VertexCache/VertexCache/wiki/Load-Testing-with-VertexBench) for complete setup instructions.

On Unix/macOS:

```bash
chmod +x run_vertexbench.sh
./run_vertexbench.sh
```

On Windows:

```
run_vertexbench.bat
```

---

### Important

The provided scripts include an example command for running VertexBench using the Java executable:

```bash
java -Xms1g -Xmx2g -XX:+UseG1GC -XX:+AlwaysPreTouch -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags -jar vertex-bench-1.0.0.jar '{
  "testName": "get-only",
  "threads": 100,
  "duration": 60,
  "percentageReads": 70,
  "percentageWrites": 25,
  "totalKeyCount": 5000,
  "clientId": "sdk-client-java",
  "clientToken": "need-valid-token-here",
  "serverHost": "localhost",
  "serverPort": 50505,
  "enableTlsEncryption": true,
  "enablePreload": true,
  "encryptionMode": "ASYMMETRIC",
  "publicKey": "Need-Valid-Key-Here"
}'
```

You must update the relevant test parameters such as:

- `clientToken` – Provide a valid token for your test environment
- `serverHost` / `serverPort` – Set to your VertexCache server address
- `publicKey` – Supply a valid public key if encryption is enabled

Additional parameters can be adjusted as needed for your specific workload. Full details are provided in the [VertexBench Wiki](https://github.com/VertexCache/VertexCache/wiki/Load-Testing-with-VertexBench).

---

### About VertexBench

**VertexBench** is the official load testing and performance benchmarking tool for VertexCache.  
It allows simulation of real-world workloads to validate system performance, tuning, and clustering behavior.

- GitHub: [github.com/vertexcache/vertexcache](https://github.com/vertexcache/vertexcache)
- Documentation: [VertexBench Wiki](https://github.com/VertexCache/VertexCache/wiki/Load-Testing-with-VertexBench)

---

> Released under the [Apache License 2.0](https://github.com/VertexCache/VertexCache/blob/main/LICENSE)  
> Maintained by **Jason Lam** – [github.com/jasonlam604](https://github.com/jasonlam604)
