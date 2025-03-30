using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using VertexCache.Sdk;
using VertexCache.Sdk.Core;
using VertexCache.Sdk.Crypto;
using VertexCache.Sdk.Transport;
using VertexCache.SdkClient.Config;
using VertexCache.SdkClient.ConsoleApp;
using VertexCache.SdkClient;

class Program
{
    static async Task Main(string[] args)
    {
        var rawPublicKey = Env.GetString("public_key")
            ?? throw new InvalidOperationException("Missing env var: public_key");

        var rawCert = Env.GetString("tls_certificate")
            ?? throw new InvalidOperationException("Missing env var: tls_certificate");

        var options = new VertexCacheSdkOptions
        {
            ServerHost = Env.GetString("server_host", "127.0.0.1")!,
            ServerPort = Env.GetInt("server_port", 50505),
            EnableEncryptionTransport = Env.GetBool("enable_encrypt_transport", false),
            EnableVerifyCertificate = Env.GetBool("enable_verify_certificate", true),
            EnableEncryption = Env.GetBool("enable_encrypt_message", false),
            TimeoutMs = Env.GetInt("timeout_ms", 3000),
            MaxRetries = 0,
            PublicKey = PemLoader.LoadFromFileOrRaw(rawPublicKey!)!,
            CertificatePem = PemLoader.LoadFromFileOrRaw(rawCert!)!
        };

        var loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddSimpleConsole(o =>
            {
                o.SingleLine = true;
                o.TimestampFormat = "HH:mm:ss ";
            });
        });

        var logger = loggerFactory.CreateLogger("VCacheClient");

        using var client = new VCachePersistentClient(options, logger);
        var runner = new CliRunner(client, options);
        await runner.RunInteractiveAsync();
    }
}
