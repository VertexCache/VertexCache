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
      class DelCommand < CommandBase
        def initialize(key)
          if key.nil? || key.strip.empty?
            raise VertexCache::Model::VertexCacheSdkException.new("#{VertexCache::Command::CommandType::DEL} command requires a non-empty key")
          end

          @key = key
        end

        def build_command
          "#{VertexCache::Command::CommandType::DEL} #{@key}"
        end

        def parse_response(body)
          unless body.strip.casecmp('OK').zero?
            set_failure("DEL failed: #{body}")
          end
        end
      end
    end
  end
end
