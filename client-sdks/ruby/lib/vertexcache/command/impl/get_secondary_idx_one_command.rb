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
require_relative '../../model/vertex_cache_sdk_exception'

module VertexCache
  module Command
    module Impl
      class GetSecondaryIdxOneCommand < CommandBase
        attr_reader :value

        def initialize(key)
          if key.nil? || key.strip.empty?
            raise VertexCache::Model::VertexCacheSdkException.new('GET By Secondary Index (idx1) command requires a non-empty key')
          end
          @key = key
          @value = nil
        end

        def build_command
          "GETIDX1 #{@key}"
        end

        def parse_response(body)
          if body.strip.casecmp('(nil)').zero?
            set_success('No matching key found, +(nil)')
          elsif body.start_with?('ERR')
            set_failure("GETIDX1 failed: #{body}")
          else
            @value = body
          end
        end
      end
    end
  end
end
