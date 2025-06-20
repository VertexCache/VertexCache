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

class GetSecondaryIdxTwoCommand(private val indexKey: String) : CommandBase<GetSecondaryIdxTwoCommand>() {

    private var value: String? = null

    init {
        if (indexKey.isBlank()) {
            throw VertexCacheSdkException("GETIDX2 command requires a non-empty index key")
        }
    }

    override fun buildCommand(): String = "GETIDX2 $indexKey"

    override fun parseResponse(responseBody: String) {
        when {
            responseBody.equals("(nil)", ignoreCase = true) -> {
                setSuccess("No matching tertiary index found, +(nil)")
                value = null
            }
            responseBody.startsWith("ERR", ignoreCase = true) -> {
                setFailure("GETIDX2 failed: $responseBody")
            }
            else -> {
                value = responseBody
            }
        }
    }

    fun getValue(): String? = value
}
