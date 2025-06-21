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

const { ClientConnector } = require('./comm/client_connector');
const { CommandResult } = require('./model/command_result');
const { GetResult } = require('./model/get_result');

const { PingCommand } = require('./command/ping_command');
const { SetCommand } = require('./command/set_command');
const { DelCommand } = require('./command/del_command');
const { GetCommand } = require('./command/get_command');
const { GetSecondaryIdxOneCommand } = require('./command/get_secondary_idx_one_command');
const { GetSecondaryIdxTwoCommand } = require('./command/get_secondary_idx_two_command');

class VertexCacheSDK {
    constructor(clientOption) {
        this.connector = new ClientConnector(clientOption);
    }

    async openConnection() {
        await this.connector.connect();
    }

    async ping() {
        const cmd = new PingCommand();
        const result = await cmd.execute(this.connector);
        return new CommandResult(result.isSuccess(), result.getStatusMessage());
    }

    async set(key, value, idx1 = null, idx2 = null) {
        const cmd = new SetCommand(key, value, idx1, idx2);
        const result = await cmd.execute(this.connector);
        return new CommandResult(result.isSuccess(), result.getStatusMessage());
    }

    async del(key) {
        const cmd = new DelCommand(key);
        const result = await cmd.execute(this.connector);
        return new CommandResult(result.isSuccess(), result.getStatusMessage());
    }

    async get(key) {
        const cmd = new GetCommand(key);
        const result = await cmd.execute(this.connector);
        return new GetResult(result.isSuccess(), result.getStatusMessage(), result.getValue());
    }

    async getBySecondaryIndex(idx1) {
        const cmd = new GetSecondaryIdxOneCommand(idx1);
        const result = await cmd.execute(this.connector);
        return new GetResult(result.isSuccess(), result.getStatusMessage(), result.getValue());
    }

    async getByTertiaryIndex(idx2) {
        const cmd = new GetSecondaryIdxTwoCommand(idx2);
        const result = await cmd.execute(this.connector);
        return new GetResult(result.isSuccess(), result.getStatusMessage(), result.getValue());
    }

    isConnected() {
        return this.connector.isConnected();
    }

    async close() {
        await this.connector.close();
    }
}

module.exports = { VertexCacheSDK };
