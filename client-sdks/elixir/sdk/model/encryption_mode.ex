# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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

defmodule VertexCacheSdk.Model.EncryptionMode do
  @moduledoc """
  Defines supported encryption modes for VertexCache SDK communication.

  Modes:
  - :none - no encryption
  - :symmetric - shared secret encryption (e.g., AES-GCM)
  - :asymmetric - public/private key encryption (e.g., RSA)
  """

  @type t :: :none | :symmetric | :asymmetric

  @spec all() :: [t()]
  def all, do: [:none, :symmetric, :asymmetric]

  @spec valid?(atom()) :: boolean()
  def valid?(mode), do: mode in all()
end
