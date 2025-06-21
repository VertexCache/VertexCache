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

use crate::command::command_type::CommandType;

#[derive(Debug, Clone)]
pub struct CommandBase {
    pub success: bool,
    pub status_message: String,
    pub command_type: CommandType,
}

impl CommandBase {
    pub fn new(command_type: CommandType) -> Self {
        Self {
            success: false,
            status_message: String::new(),
            command_type,
        }
    }

    pub fn is_success(&self) -> bool {
        self.success
    }

    pub fn is_error(&self) -> bool {
        !self.success
    }

    pub fn status_message(&self) -> &str {
        &self.status_message
    }

    pub fn command_type(&self) -> CommandType {
        self.command_type
    }
}
