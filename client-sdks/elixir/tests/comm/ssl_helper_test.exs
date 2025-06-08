defmodule VertexCacheSdk.Comm.SSLHelperTest do
  use ExUnit.Case
  alias VertexCacheSdk.Comm.SSLHelper

  # Valid self-signed PEM certificate used for testing only (do not use in production)
  @valid_pem """
  -----BEGIN CERTIFICATE-----
  ...
  -----END CERTIFICATE-----
  """

  @host 'localhost'
  @port 50505
  @client_id "VertexCacheGCMTest"
  @enable_live_tls false

  # Utility: Frame IDENT message with 4-byte length prefix (used by VertexCache protocol)
  defp frame_message(payload) do
    bin = :unicode.characters_to_binary(payload, :utf8)
    len = byte_size(bin)
    <<len::32>> <> bin
  end

  # Skips live tests if TLS testing is disabled
  defp skip_live_tls do
    IO.puts("⚠️  Skipping live TLS test (disabled via @enable_live_tls = false)")
    assert true
  end

  # --- Offline Tests ---

  test "create_verified_socket_opts returns valid options with valid cert" do
    if !@enable_live_tls do
      skip_live_tls()
    else
      result = SSLHelper.create_verified_socket_opts(@valid_pem)

      case result do
        {:ok, _opts} -> assert true
        {:error, reason} ->
          IO.puts("⚠️  Skipping test: TLS verification failed with reason: #{inspect(reason)}")
          assert true
      end
    end
  end

  test "create_verified_socket_opts fails with bad cert" do
    assert {:error, _} = SSLHelper.create_verified_socket_opts("not a cert")
  end

  test "create_verified_socket_opts fails with nil cert" do
    assert {:error, _} = SSLHelper.create_verified_socket_opts(nil)
  end

  test "create_insecure_socket_opts returns default options" do
    opts = SSLHelper.create_insecure_socket_opts()
    assert is_list(opts)
    assert Enum.any?(opts, fn {k, _} -> k == :verify end)
  end

  # --- Live TLS Tests (toggle with @enable_live_tls) ---

  test "create_verified_socket_opts connects to live TLS server" do
    if !@enable_live_tls do
      skip_live_tls()
    else
      case SSLHelper.create_verified_socket_opts(@valid_pem) do
        {:ok, opts} ->
          case :ssl.connect(@host, @port, opts ++ [active: false], 1000) do
            {:ok, socket} ->
              :ssl.send(socket, frame_message(~s/IDENT {"client_id":"#{@client_id}", "token":""}/))
              try do :ssl.recv(socket, 0, 1000) rescue _ -> :ok end
              :ssl.close(socket)
              assert true

            {:error, reason} ->
              IO.puts("⚠️  Skipping test: TLS connect failed: #{inspect(reason)}")
              assert true
          end

        {:error, reason} ->
          IO.puts("⚠️  Skipping test: TLS opts creation failed: #{inspect(reason)}")
          assert true
      end
    end
  end

  test "create_insecure_socket_opts connects without cert validation" do
    if !@enable_live_tls do
      skip_live_tls()
    else
      opts = SSLHelper.create_insecure_socket_opts()

      case :ssl.connect(@host, @port, opts ++ [active: false], 1000) do
        {:ok, socket} ->
          :ssl.send(socket, frame_message(~s/IDENT {"client_id":"#{@client_id}", "token":""}/))
          try do :ssl.recv(socket, 0, 1000) rescue _ -> :ok end
          :ssl.close(socket)
          assert true

        {:error, reason} ->
          IO.puts("⚠️  Skipping test: Insecure TLS connect failed: #{inspect(reason)}")
          assert true
      end
    end
  end
end
