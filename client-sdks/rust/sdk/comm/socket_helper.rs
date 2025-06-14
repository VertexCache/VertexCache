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

use std::net::TcpStream;
use std::time::Duration;
use crate::model::client_option::ClientOption;
use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;
use crate::comm::ssl_helper::SSLHelper;
use crate::comm::read_write_stream::ReadWriteStream;

/// Establishes a TLS-encrypted socket connection to the VertexCache server using the provided client options.
///
/// Depending on the `verify_certificate` flag in `ClientOption`, this method creates either a verified
/// or insecure TLS connector. It performs a full TLS handshake and returns a connected and secured stream.
///
/// # Arguments
/// * `option` - The client connection and TLS configuration
///
/// # Returns
/// * `Result<TlsStream<TcpStream>, VertexCacheSdkException>` - A connected secure stream
///


pub struct SocketHelper;

impl SocketHelper {pub fn create_secure_socket(
                           option: &ClientOption,
                       ) -> Result<Box<dyn ReadWriteStream>, VertexCacheSdkException> {
                           let address = format!("{}:{}", option.server_host(), option.server_port());

                           // Connect TCP socket with timeout
                           let tcp_stream = TcpStream::connect_timeout(
                               &address
                                   .parse()
                                   .map_err(|_| VertexCacheSdkException::new("Invalid server address"))?,
                               Duration::from_millis(option.connect_timeout() as u64),
                           )
                           .map_err(|_| VertexCacheSdkException::new("Failed to connect TCP socket"))?;

                           tcp_stream
                               .set_read_timeout(Some(Duration::from_millis(option.read_timeout() as u64)))
                               .ok();
                           tcp_stream
                               .set_write_timeout(Some(Duration::from_millis(option.read_timeout() as u64)))
                               .ok();

                           // TLS connector
                           let connector = if option.verify_certificate() {
                               let cert = option
                                   .tls_certificate()
                                   .ok_or_else(|| VertexCacheSdkException::new("Failed to create Secure Socket"))?;
                               SSLHelper::create_verified_tls_connector(cert)
                           } else {
                               SSLHelper::create_insecure_tls_connector()
                           }?;

                           // TLS handshake
                           let tls_stream = connector
                               .connect(option.server_host(), tcp_stream)
                               .map_err(|_| VertexCacheSdkException::new("TLS handshake failed"))?;

                           Ok(Box::new(tls_stream))
                       }

                       /// Establishes a plain (non-TLS) TCP socket connection to the VertexCache server using the provided client options.
                       ///
                       /// This method connects a standard TCP socket and applies the specified connect and read timeouts.
                       /// It is typically used for development or environments where encryption is not required.
                       ///
                       /// # Arguments
                       /// * `option` - The client connection configuration
                       ///
                       /// # Returns
                       /// * `Result<TcpStream, VertexCacheSdkException>` - A connected plain socket stream
                       ///
                       pub fn create_socket_non_tls(
                           option: &ClientOption,
                       ) -> Result<Box<dyn ReadWriteStream>, VertexCacheSdkException> {
                           let address = format!("{}:{}", option.server_host(), option.server_port());

                           // Connect with timeout
                           let tcp_stream = TcpStream::connect_timeout(
                               &address
                                   .parse()
                                   .map_err(|_| VertexCacheSdkException::new("Invalid server address"))?,
                               Duration::from_millis(option.connect_timeout() as u64),
                           )
                           .map_err(|_| VertexCacheSdkException::new("Failed to connect Non Secure Socket"))?;

                           // Set timeouts
                           tcp_stream
                               .set_read_timeout(Some(Duration::from_millis(option.read_timeout() as u64)))
                               .ok();
                           tcp_stream
                               .set_write_timeout(Some(Duration::from_millis(option.read_timeout() as u64)))
                               .ok();

                           Ok(Box::new(tcp_stream))
                       }

}