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
package com.vertexcache.sdk.command.impl

import com.vertexcache.sdk.command.CommandBase
import com.vertexcache.sdk.model.VertexCacheSdkException

class GetCommand(private val key: String) : CommandBase<GetCommand>() {

    private var value: String? = null

    init {
        if (key.isBlank()) {
            throw VertexCacheSdkException("GET command requires a non-empty key")
        }
    }

    override fun buildCommand(): String = "GET $key"

    override fun parseResponse(responseBody: String) {
        when {
            responseBody.equals("(nil)", ignoreCase = true) -> {
                setSuccess("No matching key found, +(nil)")
                value = null  // <- explicit
            }
            responseBody.startsWith("ERR", ignoreCase = true) -> {
                setFailure("GET failed: $responseBody")
            }
            else -> {
                value = responseBody  // ✅ THIS is required
            }
        }
    }

    fun getValue(): String? = value
}

