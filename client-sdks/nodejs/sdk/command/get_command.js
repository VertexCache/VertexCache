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

const { CommandBase } = require('./command_base');

class GetCommand extends CommandBase {
    constructor(key) {
        super();
        if (!key || key.trim() === '') {
            throw new Error('GET command requires a non-empty key');
        }
        this.key = key;
        this.value = null;
    }

    buildCommand() {
        return `GET ${this.key}`;
    }

    parseResponse(responseBody) {
        if (responseBody.trim().toLowerCase() === '(nil)') {
            this.setSuccess('No matching key found, +(nil)');
            return;
        }

        if (responseBody.startsWith('ERR')) {
            this.setFailure(`GET failed: ${responseBody}`);
        } else {
            this.value = responseBody;
        }
    }

    getValue() {
        return this.value;
    }
}

module.exports = { GetCommand };
