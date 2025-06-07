# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

defmodule VertexCacheSdk.Comm.MessageCodecTest do
  use ExUnit.Case
  alias VertexCacheSdk.Comm.MessageCodec

  test "write then read framed message" do
    original = "Hello VertexCache"
    frame = MessageCodec.write_framed_message(original)
    assert {:ok, decoded, ""} = MessageCodec.read_framed_message(frame)
    assert decoded == original
  end

  test "invalid version byte" do
    # length = 3, version = 0x02, payload = "abc"
    frame = <<0, 0, 0, 3, 0x02, "abc">>
    assert {:error, :unsupported_version} = MessageCodec.read_framed_message(frame)
  end

  test "too short header returns error" do
    assert :error = MessageCodec.read_framed_message(<<0x01, 0x02>>)
  end

  test "too large payload rejected on write" do
    too_big = :binary.copy(<<0>>, 10 * 1024 * 1024 + 1)
    assert_raise ArgumentError, fn -> MessageCodec.write_framed_message(too_big) end
  end

  test "write empty payload then read should fail" do
    frame = MessageCodec.write_framed_message(<<>>)
    assert {:error, :invalid_length} = MessageCodec.read_framed_message(frame)
  end

  test "utf8 multibyte payload" do
    original = "你好, VertexCache 🚀"
    frame = MessageCodec.write_framed_message(original)
    assert {:ok, decoded, ""} = MessageCodec.read_framed_message(frame)
    assert decoded == original
  end

  test "hex dump for inter-sdk comparison" do
    frame = MessageCodec.write_framed_message("ping")
    IO.puts("Framed hex: " <> Base.encode16(frame))
  end
end
