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
const { CommandType } = require('./command_type');

class SetCommand extends CommandBase {
    constructor(primaryKey, value, secondaryKey = null, tertiaryKey = null) {
        super();

        if (!primaryKey || primaryKey.trim() === '') {
            throw new Error('Missing Primary Key');
        }

        if (!value || value.trim() === '') {
            throw new Error('Missing Value');
        }

        if (secondaryKey !== null && secondaryKey.trim() === '') {
            throw new Error("Secondary key can't be empty when used");
        }

        if (
            secondaryKey !== null &&
            secondaryKey.trim() !== '' &&
            tertiaryKey !== null &&
            tertiaryKey.trim() === ''
        ) {
            throw new Error("Tertiary key can't be empty when used");
        }

        this.primaryKey = primaryKey;
        this.value = value;
        this.secondaryKey = secondaryKey;
        this.tertiaryKey = tertiaryKey;
    }

    buildCommand() {
        let command = `${CommandType.SET} ${this.primaryKey} ${this.value}`;

        if (this.secondaryKey && this.secondaryKey.trim() !== '') {
            command += ` ${CommandType.IDX1} ${this.secondaryKey}`;
        }

        if (this.tertiaryKey && this.tertiaryKey.trim() !== '') {
            command += ` ${CommandType.IDX2} ${this.tertiaryKey}`;
        }

        return command;
    }

    parseResponse(responseBody) {
        if (responseBody.trim().toUpperCase() !== 'OK') {
            this.setFailure('OK Not received');
        } else {
            this.setSuccess();
        }
    }
}

module.exports = { SetCommand };
