// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// ------------------------------------------------------------------------------
use vertexcache_sdk::comm::client_connector::ClientConnector;
use vertexcache_sdk::command::ping_command::PingCommand;
use vertexcache_sdk::model::client_option::ClientOption;
use vertexcache_sdk::model::encryption_mode::EncryptionMode;

#[test]
fn test_ping_command_should_succeed_with_insecure_tls() {
    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(50505);
    option.set_enable_tls_encryption(true);
    option.set_verify_certificate(false);
    option.set_connect_timeout(2000);
    option.set_read_timeout(2000);

    let mut sdk = VertexCacheSDK::new(option);

    sdk.open_connection().expect("Failed to connect to server");
    let ping_result = sdk.ping();

    assert!(ping_result.success, "Ping failed: {}", ping_result.message);
}
