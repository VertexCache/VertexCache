defmodule VertexCacheSDK.Unit.SdkTest do
  use ExUnit.Case, async: true

  alias VertexCacheSDK.Transport.TcpClient

  test "Empty command should return failure" do
    {:ok, client} = TcpClient.connect(host: "localhost", port: 50505)
    result = TcpClient.send_command(client, "")

    assert {:error, _} = result
  end
end
