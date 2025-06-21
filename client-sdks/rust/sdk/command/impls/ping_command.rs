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

use crate::comm::client_connector::ClientConnector;
use crate::command::command::Command;
use crate::command::command_type::CommandType;

pub struct PingCommand {
    pub success: bool,
    pub status_message: String,
}

impl PingCommand {
    pub fn new() -> Self {
        Self {
            success: false,
            status_message: String::new(),
        }
    }
}

impl Command for PingCommand {
    fn execute(&mut self, connector: &mut ClientConnector) {
        let command_str = "PING";
        match connector.send(command_str) {
            Ok(response) => {
                if response.trim().eq_ignore_ascii_case("+PONG") {
                    self.success = true;
                    self.status_message = "PONG received".to_string();
                } else {
                    self.success = false;
                    self.status_message = "Unexpected response to PING".to_string();
                }
            }
            Err(e) => {
                self.success = false;
                self.status_message = format!("PING failed: {}", e);
            }
        }
    }

    fn command_type(&self) -> CommandType {
        CommandType::Ping
    }

    fn is_success(&self) -> bool {
        self.success
    }

    fn status_message(&self) -> &str {
        &self.status_message
    }
}
