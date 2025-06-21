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

/**
 * Handles the PING command in VertexCache.
 *
 * This command checks server availability and latency.
 * It returns a "PONG" response and verifies basic liveness.
 *
 * PING is always allowed regardless of authentication or client role.
 */
class PingCommand extends CommandBase
{
    protected function buildCommand(): string
    {
        return 'PING';
    }

    protected function parseResponse(string $responseBody): void
    {
        if (trim($responseBody) === '' || strcasecmp($responseBody, 'PONG') !== 0) {
            $this->setFailure('PONG not received');
        }
    }
}
