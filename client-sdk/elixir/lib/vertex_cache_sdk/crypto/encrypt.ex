defmodule VertexCacheSDK.Crypto.Encrypt do
  @moduledoc "Encrypts a message using the RSA public key."

  alias VertexCacheSDK.Crypto.KeyHelper
  require Logger

  def maybe_encrypt(payload) when is_binary(payload) do
    DotenvParser.load_file("config/.env")

    encrypt_flag =
      System.get_env("enable_encrypt_message")
      |> to_string()
      |> String.trim()

    Logger.debug("Message encryption enabled? #{encrypt_flag}")

    if encrypt_flag == "true" do
      public_key = KeyHelper.get_public_key()
      encrypted = :public_key.encrypt_public(payload, public_key)

      Logger.debug("Encrypted payload (raw bytes, #{byte_size(encrypted)} bytes)")
      encrypted
    else
      Logger.debug("Sending plaintext payload: #{inspect(payload)}")
      payload
    end
  end
end
