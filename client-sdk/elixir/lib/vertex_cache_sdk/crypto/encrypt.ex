defmodule VertexCacheSDK.Crypto.Encrypt do
  @moduledoc "Encrypts a message using the RSA public key."

  alias VertexCacheSDK.Crypto.KeyHelper

  def maybe_encrypt(payload) when is_binary(payload) do
    DotenvParser.load_file("config/.env")

    if System.get_env("enable_encrypt_message") == "true" do
      public_key = KeyHelper.get_public_key()
      encrypted = :public_key.encrypt_public(payload, public_key)
      Base.encode64(encrypted)
    else
      payload
    end
  end
end
