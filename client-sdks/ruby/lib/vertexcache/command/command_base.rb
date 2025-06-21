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

require_relative '../comm/client_connector'
require_relative '../model/vertex_cache_sdk_exception'

module VertexCache
  module Command
    class CommandBase
      RESPONSE_OK = 'OK'
      COMMAND_SPACER = ' '

      attr_reader :success, :response, :error

      def execute(client)
        begin
          raw = client.send(build_command).strip

          if raw.start_with?('+')
            @response = raw[1..]
            parse_response(@response)
            @success = @error.nil?
          elsif raw.start_with?('-')
            @success = false
            @error = raw[1..]
          else
            @success = false
            @error = "Unexpected response: #{raw}"
          end
        rescue VertexCache::Model::VertexCacheSdkException => e
          @success = false
          @error = e.message
        end
        self
      end

      def success?
        @success
      end

      def get_response
        @response
      end

      def get_error
        @error
      end

      def get_status_message
        success? ? get_response : get_error
      end

      def set_success(response = RESPONSE_OK)
        @success = true
        @response = response
        @error = nil
      end

      def set_failure(message)
        @success = false
        @error = message
      end

      # override in subclasses
      def parse_response(_body)
        # default no-op
      end

      # must override
      def build_command
        raise NotImplementedError, 'Subclasses must implement build_command'
      end
    end
  end
end
