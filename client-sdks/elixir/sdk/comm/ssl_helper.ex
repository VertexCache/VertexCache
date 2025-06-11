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
        |> Enum.filter(fn
          {:Certificate, _, _} -> true
          {:OTPCertificate, _, _} -> true
          _ -> false
        end)
        |> Enum.map(&:public_key.pem_entry_decode/1)

      if certs == [] do
        {:error, "No valid certificates found"}
      else
        {:ok,
          [
            verify: :verify_peer,
            cacerts: certs,
            server_name_indication: 'localhost', # still default for verify mode
            versions: [:"tlsv1.2"]
          ]}
      end
    rescue
      _ -> {:error, "Failed to create secure socket connection"}
    end
  end

  @doc """
  Returns TLS options that skip verification (insecure), using provided host for SNI.
  """
  @spec create_insecure_socket_opts(charlist()) :: keyword()
  def create_insecure_socket_opts(char_host) do
    [
      verify: :verify_none,
      server_name_indication: char_host,
      versions: [:"tlsv1.2"],
      ciphers: :ssl.cipher_suites(:all, :"tlsv1.2")
    ]
  end
end
