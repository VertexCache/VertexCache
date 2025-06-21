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
use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;

pub struct GetCommand {
    key: String,
    pub success: bool,
    pub status_message: String,
    value: Option<String>,
}

impl GetCommand {
    pub fn new(key: String) -> Result<Self, VertexCacheSdkException> {
        if key.trim().is_empty() {
            return Err(VertexCacheSdkException::new("GET command requires a non-empty key"));
        }

        Ok(Self {
            key,
            success: false,
            status_message: String::new(),
            value: None,
        })
    }

    pub fn get_value(&self) -> Option<String> {
        self.value.clone()
    }
}

impl Command for GetCommand {
    fn execute(&mut self, connector: &mut ClientConnector) {
        let command_str = format!("GET {}", self.key);

        match connector.send(&command_str) {
            Ok(response) => {
                let trimmed = response.trim();

                if trimmed.eq_ignore_ascii_case("(nil)") {
                    self.success = true;
                    self.status_message = "No matching key found, +(nil)".to_string();
                    self.value = None;
                } else if trimmed.starts_with("ERR") {
                    self.success = false;
                    self.status_message = format!("GET failed: {}", trimmed);
                    self.value = None;
                } else {
                    self.success = true;
                    self.status_message = String::new();
                    self.value = Some(trimmed.to_string());
                }
            }
            Err(e) => {
                self.success = false;
                self.status_message = format!("GET failed: {}", e);
                self.value = None;
            }
        }
    }

    fn command_type(&self) -> CommandType {
        CommandType::Get
    }

    fn is_success(&self) -> bool {
        self.success
    }

    fn status_message(&self) -> &str {
        &self.status_message
    }
}
