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

pub struct SetCommand {
    pub primary_key: String,
    pub value: String,
    pub secondary_key: Option<String>,
    pub tertiary_key: Option<String>,
    pub success: bool,
    pub status_message: String,
}

impl SetCommand {
    pub fn new(
        primary_key: String,
        value: String,
        secondary_key: Option<String>,
        tertiary_key: Option<String>,
    ) -> Result<Self, VertexCacheSdkException> {
        if primary_key.trim().is_empty() {
            return Err(VertexCacheSdkException::new("Missing Primary Key"));
        }

        if value.trim().is_empty() {
            return Err(VertexCacheSdkException::new("Missing Value"));
        }

        if let Some(sk) = &secondary_key {
            if sk.trim().is_empty() {
                return Err(VertexCacheSdkException::new("Secondary key can't be empty when used"));
            }
        }

        if let (Some(sk), Some(tk)) = (&secondary_key, &tertiary_key) {
            if !sk.trim().is_empty() && tk.trim().is_empty() {
                return Err(VertexCacheSdkException::new("Tertiary key can't be empty when used"));
            }
        }

        Ok(Self {
            primary_key,
            value,
            secondary_key,
            tertiary_key,
            success: false,
            status_message: String::new(),
        })
    }

    fn build_command(&self) -> String {
        let mut cmd = format!("SET {} {}", self.primary_key, self.value);

        if let Some(sk) = &self.secondary_key {
            if !sk.trim().is_empty() {
                cmd.push_str(&format!(" IDX1 {}", sk));
            }
        }

        if let Some(tk) = &self.tertiary_key {
            if !tk.trim().is_empty() {
                cmd.push_str(&format!(" IDX2 {}", tk));
            }
        }

        cmd
    }
}

impl Command for SetCommand {
    fn execute(&mut self, connector: &mut ClientConnector) {
        let cmd = self.build_command();

        match connector.send(&cmd) {
            Ok(response) => {
                let cleaned = response.trim().trim_start_matches('+');
                self.status_message = cleaned.to_string();
                self.success = cleaned.eq_ignore_ascii_case("OK");
            }
            Err(e) => {
                self.success = false;
                self.status_message = format!("SET command failed: {}", e);
            }
        }
    }

    fn command_type(&self) -> CommandType {
        CommandType::Set
    }

    fn is_success(&self) -> bool {
        self.success
    }

    fn status_message(&self) -> &str {
        &self.status_message
    }
}
