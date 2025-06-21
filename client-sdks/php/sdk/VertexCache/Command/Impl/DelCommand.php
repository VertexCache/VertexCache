<?php
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

namespace VertexCache\Command\Impl;

use VertexCache\Command\CommandBase;
use VertexCache\Command\CommandType;
use VertexCache\Model\VertexCacheSdkException;

/**
 * Handles the DEL command in VertexCache.
 *
 * Deletes a key and its associated value from the cache.
 * If the system allows idempotent deletes, then deleting a non-existent
 * key will still return a success response (e.g., "OK DEL (noop)").
 *
 * Requires WRITE or ADMIN privileges.
 */
class DelCommand extends CommandBase
{
    private string $key;

    public function __construct(string $key)
    {
        if (trim($key) === '') {
            throw new VertexCacheSdkException(CommandType::DEL->value . ' command requires a non-empty key');
        }

        $this->key = $key;
    }

    protected function buildCommand(): string
    {
        return CommandType::DEL->keyword() . self::COMMAND_SPACER . $this->key;
    }

    protected function parseResponse(string $responseBody): void
    {
        if (strcasecmp($responseBody, 'OK') !== 0) {
            $this->setFailure("DEL failed: " . $responseBody);
        }
    }
}
