defmodule VertexCacheClient do
  @moduledoc """
  Documentation for `VertexCacheClient`.
  """

  @doc """
  Hello world.

  ## Examples

      iex> VertexCacheClient.hello()
      :world

  """
  def run do
    IO.puts VertexCacheSDK.hello()
  end
end
