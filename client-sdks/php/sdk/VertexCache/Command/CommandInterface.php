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

namespace VertexCache\Command;

use VertexCache\Comm\ClientConnector;

/**
 * CommandInterface represents a generic interface for all command types
 * that can be executed by the VertexCache SDK.
 *
 * Implementations must define how a command is executed and how
 * responses, errors, and status messages are accessed.
 */
interface CommandInterface
{
    /**
     * Executes the command using the provided client connector.
     *
     * @param ClientConnector $client
     * @return CommandInterface
     */
    public function execute(ClientConnector $client): CommandInterface;

    /**
     * Indicates if the command was successful.
     */
    public function isSuccess(): bool;

    /**
     * Returns the raw response string from the server.
     */
    public function getResponse(): string;

    /**
     * Returns the error message, if any.
     */
    public function getError(): string;

    /**
     * Returns a human-readable status message.
     */
    public function getStatusMessage(): string;
}
