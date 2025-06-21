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

pub struct GetSecondaryIdxTwoCommand {
    key: String,
    value: Option<String>,
    pub success: bool,
    pub status_message: String,
}

impl GetSecondaryIdxTwoCommand {
    pub fn new(key: String) -> Result<Self, VertexCacheSdkException> {
        if key.trim().is_empty() {
            return Err(VertexCacheSdkException::new(
                "GET By Secondary Index (idx2) command requires a non-empty key",
            ));
        }

        Ok(Self {
            key,
            value: None,
            success: false,
            status_message: String::new(),
        })
    }

    pub fn get_value(&self) -> Option<&str> {
        self.value.as_deref()
    }
}

impl Command for GetSecondaryIdxTwoCommand {
    fn execute(&mut self, connector: &mut ClientConnector) {
        let command_str = format!("GETIDX2 {}", self.key);

        match connector.send(&command_str) {
            Ok(response) => {
                let trimmed = response.trim();
                if trimmed.eq_ignore_ascii_case("(nil)") {
                    self.success = true;
                    self.status_message = "No matching key found, +(nil)".to_string();
                    self.value = None;
                } else if trimmed.starts_with("ERR") {
                    self.success = false;
                    self.status_message = format!("GETIDX2 failed: {}", trimmed);
                    self.value = None;
                } else {
                    self.success = true;
                    self.status_message = "OK".to_string();
                    self.value = Some(trimmed.to_string());
                }
            }
            Err(e) => {
                self.success = false;
                self.status_message = format!("GETIDX2 failed: {}", e);
                self.value = None;
            }
        }
    }

    fn command_type(&self) -> CommandType {
        CommandType::GetTertiaryIndex
    }

    fn is_success(&self) -> bool {
        self.success
    }

    fn status_message(&self) -> &str {
        &self.status_message
    }
}
