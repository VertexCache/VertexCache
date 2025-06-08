// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

use native_tls::{Certificate, TlsConnector};
use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;

pub struct SSLHelper;

impl SSLHelper {
    /// Creates a verified TLS connector using a custom PEM certificate string.
    pub fn create_verified_tls_connector(pem_cert: &str) -> Result<TlsConnector, VertexCacheSdkException> {
        if pem_cert.trim().is_empty() || !pem_cert.contains("BEGIN CERTIFICATE") {
            return Err(VertexCacheSdkException::new("Invalid certificate format"));
        }

        let cert = Certificate::from_pem(pem_cert.as_bytes())
            .map_err(|_| VertexCacheSdkException::new("Failed to parse PEM certificate"))?;

        let connector = TlsConnector::builder()
            .add_root_certificate(cert)
            .build()
            .map_err(|_| VertexCacheSdkException::new("Failed to build secure connector"))?;

        Ok(connector)
    }

    /// Creates an insecure TLS connector that disables certificate verification.
    pub fn create_insecure_tls_connector() -> Result<TlsConnector, VertexCacheSdkException> {
        TlsConnector::builder()
            .danger_accept_invalid_certs(true)
            .danger_accept_invalid_hostnames(true)
            .build()
            .map_err(|_| VertexCacheSdkException::new("Failed to build insecure connector"))
    }

}
