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
  module Command
    module CommandType
      PING = 'PING'
      SET  = 'SET'
      DEL  = 'DEL'
      IDX1 = 'IDX1'
      IDX2 = 'IDX2'

      ALL = [PING, SET, DEL, IDX1, IDX2].freeze
    end
  end
end
