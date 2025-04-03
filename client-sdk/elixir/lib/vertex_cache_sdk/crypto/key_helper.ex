defmodule VertexCacheSDK.Crypto.KeyHelper do
  @moduledoc """
  Loads and parses the RSA public key from `.env` (embedded or file-based).
  """

  require Logger

  def get_public_key do
    DotenvParser.load_file("config/.env")

    raw = System.get_env("public_key")

    cond do
      raw == nil ->
        raise "Missing `public_key` in .env"

      String.starts_with?(raw, "-----BEGIN") or String.contains?(raw, "\\n") ->
        Logger.debug("Decoding embedded public key...")
        raw
        |> String.replace("\\n", "\n")
        |> decode_pem()

      File.exists?(raw) ->
        Logger.debug("Reading public key from file: #{raw}")
        raw
        |> File.read!()
        |> decode_pem()

      true ->
        raise "Invalid public_key format or path"
    end
  end

  defp decode_pem(pem_string) do
    case :public_key.pem_decode(pem_string) do
      [entry] ->
        :public_key.pem_entry_decode(entry)

      [] ->
        raise "No PEM entries found in public_key"

      entries ->
        Logger.warn("Multiple PEM entries found. Using first.")
        :public_key.pem_entry_decode(hd(entries))
    end
  end
end
