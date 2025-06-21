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

module VertexCache
  module Model
    class CommandResult
      attr_reader :success, :message

      def initialize(success, message)
        @success = success
        @message = message
      end

      def success?
        @success
      end

      def error?
        !@success
      end
    end
  end
end
