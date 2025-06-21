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

require_relative '../command_base'
require_relative '../command_type'
require_relative '../../model/vertex_cache_sdk_exception'

module VertexCache
  module Command
    module Impl
      class SetCommand < CommandBase
        def initialize(primary_key, value, secondary_key = nil, tertiary_key = nil)
          if primary_key.nil? || primary_key.strip.empty?
            raise VertexCache::Model::VertexCacheSdkException.new('Missing Primary Key')
          end

          if value.nil? || value.strip.empty?
            raise VertexCache::Model::VertexCacheSdkException.new('Missing Value')
          end

          if !secondary_key.nil? && secondary_key.strip.empty?
            raise VertexCache::Model::VertexCacheSdkException.new("Secondary key can't be empty when used")
          end

          if secondary_key && !secondary_key.strip.empty? && tertiary_key && tertiary_key.strip.empty?
            raise VertexCache::Model::VertexCacheSdkException.new("Tertiary key can't be empty when used")
          end

          @primary_key = primary_key
          @value = value
          @secondary_key = secondary_key
          @tertiary_key = tertiary_key
        end

        def build_command
          parts = [
            VertexCache::Command::CommandType::SET,
            @primary_key,
            @value
          ]

          if @secondary_key && !@secondary_key.strip.empty?
            parts << VertexCache::Command::CommandType::IDX1
            parts << @secondary_key
          end

          if @tertiary_key && !@tertiary_key.strip.empty?
            parts << VertexCache::Command::CommandType::IDX2
            parts << @tertiary_key
          end

          parts.join(CommandBase::COMMAND_SPACER)
        end

        def parse_response(body)
          if body.strip.casecmp('OK').zero?
            set_success
          else
            set_failure('OK Not received')
          end
        end
      end
    end
  end
end
