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

class PingCommand extends CommandBase {
    buildCommand() {
        return 'PING';
    }

    parseResponse(responseBody) {
        if (!responseBody || responseBody.trim().toUpperCase() !== 'PONG') {
            this.setFailure('PONG not received');
        }
    }
}

module.exports = { PingCommand };
