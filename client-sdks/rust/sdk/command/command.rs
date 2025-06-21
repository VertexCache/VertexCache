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
use crate::comm::client_connector::ClientConnector;

pub trait Command {
    fn execute(&mut self, connector: &mut ClientConnector);
    fn command_type(&self) -> CommandType;
    fn is_success(&self) -> bool;
    fn status_message(&self) -> &str;
}

