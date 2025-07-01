# frozen_string_literal: true

# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# ------------------------------------------------------------------------------

require_relative 'vertexcache/version'
require_relative 'vertexcache/comm/client_connector'
require_relative 'vertexcache/command/impl/ping_command'
require_relative 'vertexcache/command/impl/set_command'
require_relative 'vertexcache/command/impl/del_command'
require_relative 'vertexcache/command/impl/get_command'
require_relative 'vertexcache/command/impl/get_secondary_idx_one_command'
require_relative 'vertexcache/command/impl/get_secondary_idx_two_command'
require_relative 'vertexcache/model/command_result'
require_relative 'vertexcache/model/get_result'

module VertexCache
  class VertexCacheSDK
    def initialize(client_option)
      @client_connector = VertexCache::Comm::ClientConnector.new(client_option)
    end

    def open_connection
      @client_connector.connect
    end

    def close
      @client_connector.close
    end

    def is_connected?
      @client_connector.connected?
    end

    def ping
      cmd = VertexCache::Command::Impl::PingCommand.new.execute(@client_connector)
      VertexCache::Model::CommandResult.new(cmd.success?, cmd.get_status_message)
    end

    def set(key, value, secondary_key = nil, tertiary_key = nil)
      cmd = VertexCache::Command::Impl::SetCommand.new(key, value, secondary_key, tertiary_key).execute(@client_connector)
      VertexCache::Model::CommandResult.new(cmd.success?, cmd.get_status_message)
    end

    def del(key)
      cmd = VertexCache::Command::Impl::DelCommand.new(key).execute(@client_connector)
      VertexCache::Model::CommandResult.new(cmd.success?, cmd.get_status_message)
    end

    def get(key)
      cmd = VertexCache::Command::Impl::GetCommand.new(key).execute(@client_connector)
      VertexCache::Model::GetResult.new(cmd.success?, cmd.get_status_message, cmd.value)
    end

    def get_by_secondary_index(key)
      cmd = VertexCache::Command::Impl::GetSecondaryIdxOneCommand.new(key).execute(@client_connector)
      VertexCache::Model::GetResult.new(cmd.success?, cmd.get_status_message, cmd.value)
    end

    def get_by_tertiary_index(key)
      cmd = VertexCache::Command::Impl::GetSecondaryIdxTwoCommand.new(key).execute(@client_connector)
      VertexCache::Model::GetResult.new(cmd.success?, cmd.get_status_message, cmd.value)
    end
  end
end
