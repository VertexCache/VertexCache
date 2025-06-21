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

namespace VertexCache\Model;

/**
 * Represents the result of executing a cache command in the VertexCache SDK.
 *
 * This class encapsulates the response status and message returned
 * from the server after executing a command such as GET, SET, or DEL.
 */
class CommandResult
{
    private bool $success;
    private string $message;

    public function __construct(bool $success, string $message)
    {
        $this->success = $success;
        $this->message = $message;
    }

    /**
     * Returns whether the command succeeded.
     */
    public function isSuccess(): bool
    {
        return $this->success;
    }

    /**
     * Returns the associated server message.
     */
    public function getMessage(): string
    {
        return $this->message;
    }
}
