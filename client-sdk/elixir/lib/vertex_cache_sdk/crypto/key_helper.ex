defmodule VertexCacheSDK.Crypto.KeyHelper do
  @moduledoc "Loads and parses RSA public key from `.env`."

  require Logger

  def get_public_key do
    DotenvParser.load_file("config/.env")
    raw = System.get_env("public_key")

    cond do
      raw == nil ->
        raise "Missing `public_key` in .env"

      String.starts_with?(raw, "-----BEGIN") or String.contains?(raw, "\\n") ->
        Logger.debug("Using embedded public key")
        raw |> String.replace("\\n", "\n") |> decode_pem()

      File.exists?(raw) ->
        Logger.debug("Using public key from file: #{raw}")
        File.read!(raw) |> decode_pem()

      true ->
        raise "Invalid public_key format or path"
    end
  end

  defp decode_pem(pem) do
    case :public_key.pem_decode(pem) do
      [entry] ->
        :public_key.pem_entry_decode(entry)

      [] ->
        raise "No PEM entries found in public_key"

      entries ->
        Logger.warning("Multiple PEM entries found. Using first.")
        :public_key.pem_entry_decode(hd(entries))
    end
  end
end
