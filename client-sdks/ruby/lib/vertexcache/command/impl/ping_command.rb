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

module VertexCache
  module Command
    module Impl
      class PingCommand < CommandBase
        def build_command
          'PING'
        end

        def parse_response(body)
          if body.nil? || body.strip.empty? || body.strip.downcase != 'pong'
            set_failure('PONG not received')
          end
        end
      end
    end
  end
end
