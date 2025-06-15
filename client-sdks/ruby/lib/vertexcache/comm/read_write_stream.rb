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
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

module VertexCache
  module Comm
    class ReadWriteStream
      def initialize(io)
        @io = io
      end

      def read(n)
        @io.read(n)
      end

      def write(data)
        @io.write(data)
      end

      def flush
        @io.flush
      end

      def close
        @io.close
      end

      def closed?
        @io.closed?
      end

      def io
        @io
      end
    end
  end
end
