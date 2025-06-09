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

package com.vertexcache.sdk.comm

import com.vertexcache.sdk.model.VertexCacheSdkException
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * SSLHelper provides SSL/TLS socket factory utilities for use within the VertexCache SDK.
 *
 * This object supports two distinct TLS configurations:
 *
 * - createVerifiedSocketFactory(pemCert): Validates server certificate against provided PEM cert.
 * - createInsecureSocketFactory(): Disables all certificate validation.
 *
 * All methods throw VertexCacheSdkException on error.
 */
object SSLHelper {

    /**
     * Creates an SSL socket factory that enforces server certificate verification using a provided PEM certificate.
     *
     * @param pemCert PEM-encoded X.509 certificate string
     * @throws VertexCacheSdkException if the certificate is invalid or SSL context creation fails
     */
    @JvmStatic
    fun createVerifiedSocketFactory(pemCert: String): SSLSocketFactory {
        try {
            val cf = CertificateFactory.getInstance("X.509")
            val certInputStream = ByteArrayInputStream(pemCert.toByteArray())
            val cert = cf.generateCertificate(certInputStream) as X509Certificate

            val ks = KeyStore.getInstance(KeyStore.getDefaultType())
            ks.load(null)
            ks.setCertificateEntry("server", cert)

            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(ks)

            val ctx = SSLContext.getInstance("TLS")
            ctx.init(null, tmf.trustManagers, null)
            return ctx.socketFactory

        } catch (e: Exception) {
            throw VertexCacheSdkException("Failed to create secure socket connection")
        }
    }

    /**
     * Creates an SSL socket factory that disables certificate verification.
     *
     * @throws VertexCacheSdkException if SSL context creation fails
     */
    @JvmStatic
    fun createInsecureSocketFactory(): SSLSocketFactory {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val ctx = SSLContext.getInstance("TLS")
            ctx.init(null, trustAllCerts, SecureRandom())
            return ctx.socketFactory

        } catch (e: Exception) {
            throw VertexCacheSdkException("Failed to create non secure socket connection")
        }
    }
}
