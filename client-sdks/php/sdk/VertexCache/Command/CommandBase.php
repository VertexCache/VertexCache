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
use VertexCache\Model\VertexCacheSdkException;

/**
 * BaseCommand defines the foundational structure for all client-issued commands in the VertexCache SDK.
 *
 * It encapsulates common metadata and behaviors shared by all command types, including:
 * - Command type identification (e.g., GET, SET, DEL)
 * - Internal tracking for retries and timestamps
 * - Role-based authorization levels
 *
 * Subclasses should extend this class to implement specific command logic and payload formatting.
 */
abstract class CommandBase implements CommandInterface
{
    protected const COMMAND_SPACER = ' ';
    private const RESPONSE_OK = 'OK';

    private bool $success = false;
    private ?string $response = null;
    private ?string $error = null;

    public function execute(ClientConnector $client): CommandInterface
    {
        try {
            $raw = trim($client->send($this->buildCommand()));

            if (str_starts_with($raw, '+')) {
                $this->response = substr($raw, 1);
                $this->parseResponse($this->response);
                if ($this->error === null) {
                    $this->success = true;
                }
            } elseif (str_starts_with($raw, '-')) {
                $this->success = false;
                $this->error = substr($raw, 1);
            } else {
                $this->success = false;
                $this->error = "Unexpected response: " . $raw;
            }

        } catch (VertexCacheSdkException $e) {
            $this->success = false;
            $this->error = $e->getMessage();
        }

        return $this;
    }

    /**
     * Constructs the raw command string to send.
     */
    abstract protected function buildCommand(): string;

    /**
     * Optional: Parses the response body. Can be overridden by subclasses.
     */
    protected function parseResponse(string $responseBody): void
    {
        // Default: no-op
    }

    public function setFailure(string $response): void
    {
        $this->success = false;
        $this->error = $response;
    }

    public function setSuccess(string $response = self::RESPONSE_OK): void
    {
        $this->success = true;
        $this->response = $response;
        $this->error = null;
    }

    public function getStatusMessage(): string
    {
        return $this->isSuccess() ? $this->getResponse() : $this->getError();
    }

    public function isSuccess(): bool
    {
        return $this->success;
    }

    public function getResponse(): string
    {
        return $this->response ?? '';
    }

    public function getError(): string
    {
        return $this->error ?? '';
    }
}
