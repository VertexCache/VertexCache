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
// ------------------------------------------------------------------------------
package com.vertexcache.sdk.command

import com.vertexcache.sdk.comm.ClientConnector
import com.vertexcache.sdk.model.VertexCacheSdkException

/**
 * BaseCommand defines the foundational structure for all client-issued commands in the VertexCache SDK.
 *
 * It encapsulates common metadata and behaviors shared by all command types, including:
 * - Command type identification (e.g., GET, SET, DEL)
 * - Internal tracking for retries and timestamps
 * - Role-based authorization levels
 *
 * Subclasses should extend this class to implement specific command logic and payload formatting.
 *
 * This abstraction allows the SDK to handle commands in a consistent, extensible, and testable manner.
 */
abstract class CommandBase<T : CommandBase<T>> : CommandInterface {

    companion object {
        private const val RESPONSE_OK = "OK"
        const val COMMAND_SPACER = " "
    }

    private var success: Boolean = false
    private var response: String? = null
    private var error: String? = null

    @Suppress("UNCHECKED_CAST")
    override fun execute(client: ClientConnector): T {
        try {
            val raw = client.send(buildCommand()).trim()
            when {
                raw.startsWith("+") -> {
                    response = raw.substring(1)
                    parseResponse(response!!)
                    if (error == null) {
                        success = true
                    }
                }
                raw.startsWith("-") -> {
                    success = false
                    error = raw.substring(1)
                }
                else -> {
                    success = false
                    error = "Unexpected response: $raw"
                }
            }
        } catch (e: VertexCacheSdkException) {
            success = false
            error = e.message
        }

        return this as T  // ✅ Cast to generic T
    }

    protected abstract fun buildCommand(): String

    protected open fun parseResponse(responseBody: String) {
        // Default: do nothing — override if needed
    }

    fun setFailure(response: String) {
        success = false
        error = response
    }

    fun setSuccess() {
        success = true
        response = RESPONSE_OK
        error = null
    }

    fun setSuccess(response: String) {
        success = true
        this.response = response
        error = null
    }

    override fun getStatusMessage(): String? {
        return if (isSuccess()) getResponse() else getError()
    }

    override fun isSuccess(): Boolean = success
    override fun getResponse(): String? = response
    override fun getError(): String? = error
}
