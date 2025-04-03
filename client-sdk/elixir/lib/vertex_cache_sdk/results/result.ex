defmodule VertexCacheSDK.Results.Result do
  @moduledoc """
  Represents a parsed response from the VertexCache server.
  Supports parsing simple OK/ERR patterns and payloads.
  """

  defstruct [:status, :message, :raw]

  @type status_t :: :ok | :error | :unknown

  @type t :: %__MODULE__{
               status: status_t,
               message: String.t(),
               raw: String.t()
             }

  @doc """
  Parses a raw response line into a Result struct.
  """
  def parse(response) when is_binary(response) do
    cond do
      String.starts_with?(response, "OK") ->
        %__MODULE__{status: :ok, message: String.trim_leading(response, "OK"), raw: response}

      String.starts_with?(response, "ERR") ->
        %__MODULE__{status: :error, message: String.trim_leading(response, "ERR"), raw: response}

      true ->
        %__MODULE__{status: :unknown, message: response, raw: response}
    end
  end
end
