defmodule VertexCacheSDK.Transport.TcpClient do
  @moduledoc """
  Secure TCP/TLS client for VertexCache.
  Reads config from standardized `.env` file under `config/`.
  """

  require Logger

  defstruct [:host, :port, :socket]

  @type t :: %__MODULE__{
               host: String.t(),
               port: pos_integer(),
               socket: :ssl.sslsocket() | :gen_tcp.socket() | nil
             }

  def connect(opts \\ []) do
    DotenvParser.load_file("config/.env")

    host = System.get_env("server_host") || Keyword.fetch!(opts, :host)

    port =
      case System.get_env("server_port") do
        nil -> Keyword.fetch!(opts, :port)
        str when is_binary(str) -> String.to_integer(str)
      end

    use_ssl = String.trim(System.get_env("enable_encrypt_transport") || "") == "true"
    verify_cert = String.trim(System.get_env("enable_verify_certificate") || "") == "true"

    Logger.debug("Connecting to #{host}:#{port} via TLS: #{use_ssl}, verify: #{verify_cert}")

    if use_ssl do
      certfile = resolve_embedded_or_path(System.get_env("tls_certificate"), "vertexcache_tls_cert.pem")

      ssl_opts = [
        cacertfile: String.to_charlist(certfile),
        verify: if(verify_cert, do: :verify_peer, else: :verify_none),
        depth: 3,
        ciphers: [{:rsa, :aes_256_cbc, :sha256}],
        versions: [:"tlsv1.2"],
        server_name_indication: String.to_charlist(host),
        active: false,
        reuse_sessions: false,
        packet: :line
      ]

      case :ssl.connect(String.to_charlist(host), port, ssl_opts, 5000) do
        {:ok, socket} ->
          Logger.info("Connected via SSL")
          {:ok, %__MODULE__{host: host, port: port, socket: socket}}

        {:error, reason} ->
          Logger.error("SSL connect failed: #{inspect(reason)}")
          {:error, reason}
      end
    else
      Logger.warning("TLS is disabled. Falling back to plain :gen_tcp.")

      case :gen_tcp.connect(String.to_charlist(host), port, [:binary, packet: :line, active: false]) do
        {:ok, socket} ->
          Logger.info("Connected via TCP")
          {:ok, %__MODULE__{host: host, port: port, socket: socket}}

        {:error, reason} ->
          Logger.error("TCP connect failed: #{inspect(reason)}")
          {:error, reason}
      end
    end
  end

  def send_command(%__MODULE__{socket: socket}, command) do
    raw = command <> "\n"
    payload = VertexCacheSDK.Crypto.Encrypt.maybe_encrypt(raw)

    with :ok <- send_data(socket, payload),
         {:ok, response} <- recv(socket) do
      {:ok, String.trim(to_string(response))}
    else
      error -> {:error, error}
    end
  end

  def close(%__MODULE__{socket: socket}) do
    case socket do
      {:sslsocket, _, _} -> :ssl.close(socket)
      port when is_port(port) -> :gen_tcp.close(port)
    end

    :ok
  end

  defp send_data(socket, data) when is_port(socket), do: :gen_tcp.send(socket, data)
  defp send_data(socket, data), do: :ssl.send(socket, data)

  defp recv(socket) when is_port(socket), do: :gen_tcp.recv(socket, 0)
  defp recv(socket), do: :ssl.recv(socket, 0)

  defp resolve_embedded_or_path(nil, _filename), do: raise "Missing tls_certificate in .env"

  defp resolve_embedded_or_path(raw, filename) when is_binary(raw) do
    decoded = String.replace(raw, "\\n", "\n")

    if String.starts_with?(decoded, "-----BEGIN CERTIFICATE-----") do
      tmpfile = Path.join(System.tmp_dir!(), filename)
      File.write!(tmpfile, decoded)
      Logger.debug("Embedded TLS cert written to #{tmpfile}")
      tmpfile
    else
      Logger.debug("Using TLS cert path: #{raw}")
      raw
    end
  end
end
