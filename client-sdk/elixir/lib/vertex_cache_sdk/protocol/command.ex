defmodule VertexCacheSDK.Protocol.Command do
  @moduledoc """
  Represents a VertexCache command and builds its payload for sending.
  """

  defstruct [:name, :args]

  @type t :: %__MODULE__{
               name: String.t(),
               args: [String.t()] | nil
             }

  @doc """
  Returns a new command struct.
  """
  def new(name, args \\ []) when is_binary(name) and is_list(args) do
    %__MODULE__{name: name, args: args}
  end

  @doc """
  Converts the command to a string suitable for sending over the wire.
  """
  def to_wire(%__MODULE__{name: name, args: args}) do
    payload = [name | args] |> Enum.join(" ")
    payload
  end
end
