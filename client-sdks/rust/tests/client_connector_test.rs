// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// ------------------------------------------------------------------------------
use std::io::Cursor;
use vertexcache_sdk::comm::gcm_crypto_helper::GcmCryptoHelper;
use vertexcache_sdk::comm::key_parser_helper::KeyParserHelper;
use vertexcache_sdk::comm::client_connector::ClientConnector;
use vertexcache_sdk::comm::message_codec::{
    MessageCodec, DEFAULT_PROTOCOL_VERSION, PROTOCOL_VERSION_AES_GCM, PROTOCOL_VERSION_RSA_OAEP_SHA256,
};
use vertexcache_sdk::model::client_option::ClientOption;
use vertexcache_sdk::model::encryption_mode::EncryptionMode;

#[test]
fn test_protocol_version_none_mode_should_be_ignored() {
    let mut option = ClientOption::new();
    option.set_encryption_mode(EncryptionMode::NONE);

    let connector = ClientConnector::new(option);
    let resolved_version = connector.resolve_protocol_version();

    assert_eq!(resolved_version, DEFAULT_PROTOCOL_VERSION);
}

#[test]
fn test_protocol_version_symmetric_mode_should_embed_correct_version() {
    let mut option = ClientOption::new();
    option.set_encryption_mode(EncryptionMode::SYMMETRIC);
    option.set_shared_encryption_key(Some("00000000000000000000000000000000".to_string()));

    let connector = ClientConnector::new(option);
    let resolved_version = connector.resolve_protocol_version();

    assert_eq!(resolved_version, PROTOCOL_VERSION_AES_GCM);
}

#[test]
fn test_protocol_version_asymmetric_mode_should_embed_correct_version() {
    let mut option = ClientOption::new();
    option.set_encryption_mode(EncryptionMode::ASYMMETRIC);
    option.set_public_key(Some("-----BEGIN PUBLIC KEY-----\nMFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK7m8GboIry9a1W1P6g0UCeHPCXnKMV0\nMxB8F3uowcdUokTQvO0g7th3pduDDgWkYbWX3XP4sz9tB09J74s3pHECAwEAAQ==\n-----END PUBLIC KEY-----".to_string()));

    let connector = ClientConnector::new(option);
    let resolved_version = connector.resolve_protocol_version();

    assert_eq!(resolved_version, PROTOCOL_VERSION_RSA_OAEP_SHA256);
}


#[test]
fn test_client_connector_initialization_should_succeed() {
    let mut option = ClientOption::new();
    option.set_server_host("localhost".to_string());
    option.set_server_port(50505);
    option.set_enable_tls_encryption(false);
    option.set_connect_timeout(2000);
    option.set_read_timeout(2000);

    let connector = ClientConnector::new(option);
    assert!(!connector.is_connected());
}
