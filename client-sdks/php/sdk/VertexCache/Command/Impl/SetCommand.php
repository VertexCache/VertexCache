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
 * Handles the SET command in VertexCache.
 *
 * Stores a value in the cache under the specified key, optionally assigning
 * secondary (idx1) and tertiary (idx2) indexes for lookup.
 * Existing keys will be overwritten.
 *
 * Requires WRITE or ADMIN access.
 */
class SetCommand extends CommandBase
{
    private string $primaryKey;
    private string $value;
    private ?string $secondaryKey;
    private ?string $tertiaryKey;

    public function __construct(string $primaryKey, string $value, ?string $secondaryKey = null, ?string $tertiaryKey = null)
    {
        if (trim($primaryKey) === '') {
            throw new VertexCacheSdkException('Missing Primary Key');
        }

        if (trim($value) === '') {
            throw new VertexCacheSdkException('Missing Value');
        }

        if ($secondaryKey !== null && trim($secondaryKey) === '') {
            throw new VertexCacheSdkException("Secondary key can't be empty when used");
        }

        if (
            $secondaryKey !== null &&
            trim($secondaryKey) !== '' &&
            $tertiaryKey !== null &&
            trim($tertiaryKey) === ''
        ) {
            throw new VertexCacheSdkException("Tertiary key can't be empty when used");
        }

        $this->primaryKey = $primaryKey;
        $this->value = $value;
        $this->secondaryKey = $secondaryKey;
        $this->tertiaryKey = $tertiaryKey;
    }

    protected function buildCommand(): string
    {
        $parts = [
            CommandType::SET->keyword(),
            $this->primaryKey,
            $this->value,
        ];

        if ($this->secondaryKey !== null && trim($this->secondaryKey) !== '') {
            $parts[] = CommandType::IDX1->keyword();
            $parts[] = $this->secondaryKey;
        }

        if ($this->tertiaryKey !== null && trim($this->tertiaryKey) !== '') {
            $parts[] = CommandType::IDX2->keyword();
            $parts[] = $this->tertiaryKey;
        }

        return implode(self::COMMAND_SPACER, $parts);
    }

    protected function parseResponse(string $responseBody): void
    {
        if (strcasecmp($responseBody, 'OK') !== 0) {
            $this->setFailure('OK Not received');
        } else {
            $this->setSuccess();
        }
    }
}
