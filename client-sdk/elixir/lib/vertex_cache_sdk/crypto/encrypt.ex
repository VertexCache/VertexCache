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
      base64 = Base.encode64(encrypted)

      Logger.debug("Encrypted payload (Base64): #{String.slice(base64, 0..60)}...")
      base64
    else
      Logger.debug("Sending plaintext payload: #{inspect(payload)}")
      payload
    end
  end
end
