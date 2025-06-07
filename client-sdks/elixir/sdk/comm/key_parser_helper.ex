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

defmodule VertexCacheSdk.Comm.KeyParserHelper do
  alias VertexCacheSdk.Model.VertexCacheSdkException

  @doc """
  Parses a PEM-encoded RSA public key and returns a DER binary.

  Raises VertexCacheSdkException on failure.
  """
  def config_public_key_if_enabled(pem) do
    try do
      cleaned =
        pem
        |> String.replace("-----BEGIN PUBLIC KEY-----", "")
        |> String.replace("-----END PUBLIC KEY-----", "")
        |> String.replace(~r/\s/, "")

      Base.decode64!(cleaned)
    rescue
      _ -> raise VertexCacheSdkException, message: "Invalid public key"
    end
  end

  @doc """
  Decodes a Base64-encoded symmetric key into raw bytes.

  Raises VertexCacheSdkException on failure.
  """
  def config_shared_key_if_enabled(base64) do
    case Base.decode64(base64) do
      {:ok, decoded} -> decoded
      :error -> raise VertexCacheSdkException, message: "Invalid shared key"
    end
  end
end
