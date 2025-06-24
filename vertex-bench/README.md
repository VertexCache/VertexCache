<p align="center">
  <img src="https://github.com/jasonlam604/VertexCache/blob/main/etc/assets/vertexcache-logo-192x192.png" alt="VertexCache Logo" width="100" height="100"/>
</p>

<h1 align="center">VertexBench</h1>

**VertexBench** is the official load testing and benchmarking utility for [VertexCache](https://github.com/VertexCache/VertexCache).  
It provides a native, framework-free environment for measuring real-world server performance under controlled and repeatable workloads.

---

### What It Does

- Simulates client load using the official Java SDK
- Measures throughput, latency, and error rates across configurable scenarios
- Supports:
    - GET/SET/DEL ratio tuning
    - Key/value size variation
    - TTL and hot key stress patterns
    - Warm-up and ramp-up periods
- Outputs metrics to stdout or file for analysis

---

### Use It for More Than Benchmarks

VertexBench is not just for internal validation — it's built for **you** to test your own environment.  
System performance depends on real-world variables: CPU, memory, JVM tuning, network latency, and GC behavior.  
**Use VertexBench to experiment, validate tuning, and find optimal settings for your deployment.**

- See [JVM Tuning Tips](https://github.com/VertexCache/VertexCache/wiki/JVM-Tuning-Tips) to get the most out of your hardware

---

### Not Included

Eviction algorithm performance comparisons (e.g. LRU vs LFU vs TinyLFU) are covered by unit tests in the main VertexCache server repo:  
[vertex-cache-server/src/test/java/com/vertexcache/core/cache/perf](https://github.com/VertexCache/VertexCache/tree/main/vertex-cache-server/src/test/java/com/vertexcache/core/cache/perf)

See results here:
- [Performance Benchmarks (Eviction Algorithms)](https://github.com/VertexCache/VertexCache/wiki/Performance-Benchmarks)
- [Load Testing Results (VertexBench)](https://github.com/VertexCache/VertexCache/wiki/Load-Testing-Results)

---

### Project Status

VertexBench is under active development and aligned with upcoming VertexCache releases.  
Planned features include:
- Custom workload scenario scripting
- Report exports with percentile latency breakdowns
- Distributed client simulation

---

### Learn More

- [VertexCache Wiki – Performance Testing](https://github.com/VertexCache/VertexCache/wiki/Performance-Testing)
- [VertexCache Java SDK](https://github.com/VertexCache/VertexCache/tree/main/sdk-java)

---

Maintained by **Jason Lam** – [github.com/jasonlam604](https://github.com/jasonlam604)  
Part of the [VertexCache Project](https://github.com/VertexCache/VertexCache)  
Licensed under the [Apache License 2.0](https://github.com/VertexCache/VertexCache/blob/main/LICENSE)
