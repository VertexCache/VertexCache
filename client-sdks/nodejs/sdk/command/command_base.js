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

const RESPONSE_OK = 'OK';

class CommandBase {
    static COMMAND_SPACER = ' ';

    constructor() {
        this.success = false;
        this.response = null;
        this.error = null;
    }

    async execute(clientConnector) {
        try {
            const raw = (await clientConnector.send(this.buildCommand())).trim();

            if (raw.startsWith('+')) {
                this.response = raw.substring(1);
                this.parseResponse(this.response);
                if (!this.error) {
                    this.success = true;
                }
            } else if (raw.startsWith('-')) {
                this.success = false;
                this.error = raw.substring(1);
            } else {
                this.success = false;
                this.error = `Unexpected response: ${raw}`;
            }
        } catch (err) {
            this.success = false;
            this.error = err.message;
        }

        return this;
    }

    buildCommand() {
        throw new Error('buildCommand() must be implemented by subclass');
    }

    parseResponse(_responseBody) {
        // Optional override in subclass
    }

    setFailure(message) {
        this.success = false;
        this.error = message;
    }

    setSuccess(response = RESPONSE_OK) {
        this.success = true;
        this.response = response;
        this.error = null;
    }

    isSuccess() {
        return this.success;
    }

    getResponse() {
        return this.response;
    }

    getError() {
        return this.error;
    }

    getStatusMessage() {
        return this.isSuccess() ? this.getResponse() : this.getError();
    }
}

module.exports = { CommandBase };
