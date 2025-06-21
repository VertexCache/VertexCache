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

namespace VertexCache;

use VertexCache\Command\Impl\DelCommand;
use VertexCache\Command\Impl\GetCommand;
use VertexCache\Command\Impl\PingCommand;
use VertexCache\Command\Impl\SetCommand;
use VertexCache\Command\Impl\GetSecondaryIdxOneCommand;
use VertexCache\Command\Impl\GetSecondaryIdxTwoCommand;

use VertexCache\Model\ClientOption;
use VertexCache\Model\CommandResult;
use VertexCache\Model\GetResult;
use VertexCache\Comm\ClientConnector;

/**
 * VertexCacheSDK is the main entry point for interacting with the VertexCache server.
 *
 * Provides methods to perform cache operations like GET, SET, DEL, and index lookups.
 * Handles TLS, encryption (RSA/AES-GCM), and framing under the hood.
 */
class VertexCacheSDK
{
    private ClientConnector $client;

    public function __construct(ClientOption $clientOption)
    {
        $this->client = new ClientConnector($clientOption);
    }

    public function openConnection(): void
    {
        $this->client->connect();
    }

    public function close(): void
    {
        $this->client->close();
    }

    public function isConnected(): bool
    {
        return $this->client->isConnected();
    }

    public function ping(): CommandResult
    {
        $cmd = (new PingCommand())->execute($this->client);
        return new CommandResult($cmd->isSuccess(), $cmd->getStatusMessage());
    }

    public function set(string $key, string $value, ?string $idx1 = null, ?string $idx2 = null): CommandResult
    {
        $cmd = (new SetCommand($key, $value, $idx1, $idx2))->execute($this->client);
        return new CommandResult($cmd->isSuccess(), $cmd->getStatusMessage());
    }

    public function get(string $key): GetResult
    {
        $cmd = (new GetCommand($key))->execute($this->client);
        return new GetResult($cmd->isSuccess(), $cmd->getStatusMessage(), $cmd->getValue());
    }

    public function del(string $key): CommandResult
    {
        $cmd = (new DelCommand($key))->execute($this->client);
        return new CommandResult($cmd->isSuccess(), $cmd->getStatusMessage());
    }

    public function getBySecondaryIndex(string $key): GetResult
    {
        $cmd = (new GetSecondaryIdxOneCommand($key))->execute($this->client);
        return new GetResult($cmd->isSuccess(), $cmd->getStatusMessage(), $cmd->getValue());
    }

    public function getByTertiaryIndex(string $key): GetResult
    {
        $cmd = (new GetSecondaryIdxTwoCommand($key))->execute($this->client);
        return new GetResult($cmd->isSuccess(), $cmd->getStatusMessage(), $cmd->getValue());
    }
}
