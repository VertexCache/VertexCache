defmodule VertexCacheClientTest do
  use ExUnit.Case
  import ExUnit.CaptureIO

  test "client calls SDK function" do
    assert capture_io(fn -> VertexCacheClient.run() end) == "VertexCacheSDK\n"
  end
end
