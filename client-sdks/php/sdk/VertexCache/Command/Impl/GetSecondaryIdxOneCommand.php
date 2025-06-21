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
use VertexCache\Model\VertexCacheSdkException;

/**
 * Handles the GET Secondary Idx (idx1) command in VertexCache.
 *
 * Retrieves the value for a given secondary index key (idx1).
 * Returns an error if the key is missing or expired.
 *
 * Requires READ, READ_WRITE, or ADMIN access.
 */
class GetSecondaryIdxOneCommand extends CommandBase
{
    private string $key;
    private ?string $value = null;

    public function __construct(string $key)
    {
        if (trim($key) === '') {
            throw new VertexCacheSdkException('GET By Secondary Index (idx1) command requires a non-empty key');
        }

        $this->key = $key;
    }

    protected function buildCommand(): string
    {
        return 'GETIDX1' . self::COMMAND_SPACER . $this->key;
    }

    protected function parseResponse(string $responseBody): void
    {
        if (strcasecmp($responseBody, '(nil)') === 0) {
            $this->setSuccess('No matching key found, +(nil)');
            return;
        }

        if (str_starts_with($responseBody, 'ERR')) {
            $this->setFailure('GETIDX1 failed: ' . $responseBody);
        } else {
            $this->value = $responseBody;
        }
    }

    public function getValue(): ?string
    {
        return $this->value;
    }
}
