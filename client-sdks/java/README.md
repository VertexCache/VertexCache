# VertexCache Java Client SDK

The official Java SDK for communicating with a VertexCache server over TCP.

This SDK provides a lightweight, pluggable way to issue cache commands (`GET`, `SET`, `DEL`, etc.), with built-in support for:
- TLS encryption (secure and insecure modes)
- Symmetric (AES-GCM) and asymmetric (RSA) encryption
- Authentication via client ID and token
- Multi-index support (idx1, idx2)

---

## 🔧 Getting Started

### Requirements
- Java 21 or newer
- A running instance of [VertexCache](https://github.com/VertexCache/VertexCache)

---

### Installation

#### Option 1: Local Build (current)
Until Maven Central or Gradle support is available, install the SDK locally:

```bash
git clone https://github.com/VertexCache/VertexCache.git
cd VertexCache/java-sdk
mvn install
```

Then add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.vertexcache</groupId>
    <artifactId>vertexcache-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or to your `build.gradle` (with local Maven repo resolution):

```groovy
dependencies {
    implementation 'com.vertexcache:vertexcache-sdk:1.0.0'
}
```

#### Option 2: Maven Central / Gradle Plugin (coming soon)
We will be publishing to Maven Central and Gradle Plugin Portal in an upcoming release. Stay tuned!

---

## ✅ Quick Example

```java
ClientOption options = new ClientOption();
        options.setServerHost("localhost");
        options.setServerPort(50505);
        options.setClientId("my-app");
        options.setClientToken("secure-token");
        options.setEnableTlsEncryption(true);
        options.setVerifyCertificate(false); // For development
        options.setEncryptionMode(EncryptionMode.SYMMETRIC);
        options.setSharedEncryptionKey("base64-encoded-shared-key");

        VertexCacheSDK sdk = new VertexCacheSDK(options);

        sdk.set("hello", "world");
        GetResult result = sdk.get("hello");
        System.out.println("value = " + result.getValue());

        sdk.close();
```

---

## 🧪 Live Test Suite

This SDK includes a live integration test suite (`VertexCacheSDKLiveTest`) to help you:
- See how the SDK works in practice
- Validate end-to-end interactions with a real VertexCache server
- Understand IDENT, TLS, encryption, and command flows

> ⚠️ These tests require a locally running VertexCache server.  
> They use a test public key and self-signed certificate preconfigured server-side.  
> For documentation, see the [Test Setup Notes](https://github.com/VertexCache/VertexCache/wiki/SDK-Integration-Testing).

---

## 📚 Documentation & Resources

- 📘 **Full Wiki**: [VertexCache SDK Docs](https://github.com/VertexCache/VertexCache/wiki)
- 🌐 **Website**: [www.vertexcache.com](https://www.vertexcache.com)
- 💻 **Core Server Repo**: [github.com/VertexCache/VertexCache](https://github.com/VertexCache/VertexCache)

---

## 🛡 Security Notes

- Always use `verifyCertificate = true` and trusted certs in production.
- Never reuse test keys or tokens in deployed environments.
- Consider using SYMMETRIC mode for efficiency and ASYMMETRIC for stronger separation of keys.

---

## 📦 License

Apache 2.0  
Copyright © VertexCache
