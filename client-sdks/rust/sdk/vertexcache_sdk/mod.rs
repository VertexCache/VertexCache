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

use crate::command::command::Command;
use crate::command::impls::{
    del_command::DelCommand,
    ping_command::PingCommand, set_command::SetCommand,
};
use crate::comm::client_connector::ClientConnector;
use crate::model::{client_option::ClientOption, vertex_cache_sdk_exception::VertexCacheSdkException};

pub struct VertexCacheSDK {
    connector: ClientConnector,
}

pub struct CommandResult {
    pub success: bool,
    pub message: String,
    pub value: Option<String>,
}

impl CommandResult {
    pub fn new(success: bool, message: String) -> Self {
        Self {
            success,
            message,
            value: None,
        }
    }

    pub fn with_value(success: bool, message: String, value: Option<String>) -> Self {
        Self {
            success,
            message,
            value,
        }
    }
}

impl VertexCacheSDK {
    pub fn new(option: ClientOption) -> Self {
        VertexCacheSDK {
            connector: ClientConnector::new(option),
        }
    }

    pub fn open_connection(&mut self) -> Result<(), VertexCacheSdkException> {
        self.connector.connect()
    }

    pub fn close(&mut self) {
        self.connector.close();
    }

    pub fn ping(&mut self) -> CommandResult {
        let mut cmd = PingCommand::new();
        cmd.execute(&mut self.connector);
        CommandResult::new(cmd.is_success(), cmd.status_message().to_string())
    }

    pub fn set(
        &mut self,
        key: &str,
        value: &str,
        idx1: Option<String>,
        idx2: Option<String>,
    ) -> CommandResult {
        match SetCommand::new(key.to_string(), value.to_string(), idx1, idx2) {
            Ok(mut cmd) => {
                cmd.execute(&mut self.connector);
                CommandResult::new(cmd.is_success(), cmd.status_message().to_string())
            }
            Err(e) => CommandResult::new(false, e.message),
        }
    }

    pub fn del(&mut self, key: &str) -> CommandResult {
        match DelCommand::new(key.to_string()) {
            Ok(mut cmd) => {
                cmd.execute(&mut self.connector);
                CommandResult::new(cmd.is_success(), cmd.status_message().to_string())
            }
            Err(e) => CommandResult::new(false, e.message),
        }
    }

    pub fn get(&mut self, key: &str) -> CommandResult {
        match self.connector.send(&format!("GET {}", key)) {
            Ok(resp) => {
                if resp.trim() == "+(nil)" {
                    CommandResult::with_value(true, "OK".to_string(), None)
                } else {
                    let val = resp.trim().strip_prefix('+').unwrap_or(&resp).to_string();
                    CommandResult::with_value(true, "OK".to_string(), Some(val))
                }
            }
            Err(e) => CommandResult::new(false, e.to_string()),
        }
    }

    pub fn get_by_secondary_index(&mut self, idx1: &str) -> CommandResult {
        match self.connector.send(&format!("GETIDX1 {}", idx1)) {
            Ok(resp) => {
                if resp.trim() == "+(nil)" {
                    CommandResult::with_value(true, "OK".to_string(), None)
                } else {
                    let val = resp.trim().strip_prefix('+').unwrap_or(&resp).to_string();
                    CommandResult::with_value(true, "OK".to_string(), Some(val))
                }
            }
            Err(e) => CommandResult::new(false, e.to_string()),
        }
    }

    pub fn get_by_tertiary_index(&mut self, idx2: &str) -> CommandResult {
        match self.connector.send(&format!("GETIDX2 {}", idx2)) {
            Ok(resp) => {
                if resp.trim() == "+(nil)" {
                    CommandResult::with_value(true, "OK".to_string(), None)
                } else {
                    let val = resp.trim().strip_prefix('+').unwrap_or(&resp).to_string();
                    CommandResult::with_value(true, "OK".to_string(), Some(val))
                }
            }
            Err(e) => CommandResult::new(false, e.to_string()),
        }
    }
}
