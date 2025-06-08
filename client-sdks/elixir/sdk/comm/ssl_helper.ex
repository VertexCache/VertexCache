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

defmodule VertexCacheSdk.Comm.SSLHelper do
  @moduledoc false

  @doc """
  Returns TLS options for verified mode using a PEM certificate.
  """
  @spec create_verified_socket_opts(String.t()) :: {:ok, keyword()} | {:error, String.t()}
  def create_verified_socket_opts(pem) do
    try do
      cleaned_pem =
        pem
        |> String.trim()
        |> String.replace("\r", "")
        |> :erlang.binary_to_list()

      decoded = :public_key.pem_decode(cleaned_pem)

      certs =
        decoded
        |> Enum.filter(fn entry ->
          case entry do
            {:Certificate, _, _} -> true
            {:OTPCertificate, _, _} -> true
            _ -> false
          end
        end)
        |> Enum.map(&:public_key.pem_entry_decode/1)

      if certs == [] do
        {:error, "No valid certificates found"}
      else
        {:ok,
          [
            verify: :verify_peer,
            cacerts: certs,
            server_name_indication: 'localhost',
            versions: [:"tlsv1.2"]
          ]}
      end
    rescue
      _ ->
        {:error, "Failed to create secure socket connection"}
    end
  end



  @doc """
  Returns TLS options that skip verification (insecure).
  """
  @spec create_insecure_socket_opts() :: keyword()
  def create_insecure_socket_opts do
    [
      verify: :verify_none,
      server_name_indication: 'localhost',
      versions: [:"tlsv1.2"]
    ]
  end
end
