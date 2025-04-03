defmodule VertexCacheSDK.Results.Result do
  @moduledoc "Parses response results from VertexCache server."

  defstruct [:status, :message]

  def parse(response) when is_binary(response) do
    case String.trim(response) do
      <<"+"::utf8, rest::binary>> ->
        %__MODULE__{status: "ok", message: rest}

      <<"-"::utf8, rest::binary>> ->
        %__MODULE__{status: "error", message: rest}

      other ->
        %__MODULE__{status: "unknown", message: other}
    end
  end
end
