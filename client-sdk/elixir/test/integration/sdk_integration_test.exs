defmodule VertexCacheSDK.Integration.SdkIntegrationTest do
  use ExUnit.Case, async: true

  alias VertexCacheSDK.Transport.TcpClient
  alias VertexCacheSDK.Protocol.Command

  defp run(cmd, args \\ []) do
    full = Enum.join([cmd | args], " ") |> Command.new() |> Command.to_wire()
    {:ok, client} = TcpClient.connect(host: "localhost", port: 50505)
    TcpClient.send_command(client, full)
  end

  test "run ping, set, and get command sequence" do
    assert {:ok, "+PONG"} = run("ping")
    assert {:ok, "+OK"} = run("set", ["testkey", "hello"])
    assert {:ok, "+hello"} = run("get", ["testkey"])
  end
end
