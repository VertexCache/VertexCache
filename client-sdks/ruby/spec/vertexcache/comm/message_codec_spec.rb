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

require 'spec_helper'
require 'stringio'
require 'vertexcache/comm/message_codec'

RSpec.describe VertexCache::Comm::MessageCodec do
  let(:codec) { VertexCache::Comm::MessageCodec }
  let(:version) { codec::DEFAULT_PROTOCOL_VERSION }

  it 'writes then reads a framed message' do
    original = "Hello VertexCache"
    out = StringIO.new
    codec.write_framed_message(out, original, version)
    out.rewind

    result = codec.read_framed_message(out)
    expect(result).to eq([version, original])
  end

  it 'returns nil if header too short' do
    io = StringIO.new("\x00\x01")
    result = codec.read_framed_message(io)
    expect(result).to be_nil
  end

  it 'raises if payload is too large' do
    too_big = "A" * (codec::MAX_MESSAGE_SIZE + 1)
    out = StringIO.new
    expect {
      codec.write_framed_message(out, too_big, version)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Payload too large/)
  end

  it 'raises if payload is empty' do
    out = StringIO.new
    expect {
      codec.write_framed_message(out, "", version)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Payload must be non-empty/)
  end

  it 'raises on invalid message length during read' do
    length = codec::MAX_PAYLOAD_SIZE + 100
    header = [length].pack('N') + [version].pack('N')
    io = StringIO.new(header + "X" * 10)
    expect {
      codec.read_framed_message(io)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Invalid message length/)
  end

  it 'handles UTF-8 multibyte payloads correctly' do
    original = "你好, VertexCache 🚀"
    out = StringIO.new
    codec.write_framed_message(out, original, version)
    out.rewind

    result = codec.read_framed_message(out)
    expect(result[1].force_encoding("UTF-8")).to eq(original)
  end

  it 'prints hex dump for inter-SDK comparison' do
    payload = "ping"
    hex = codec.hex_dump(payload, version)
    expect(hex).to match(/[A-F0-9]+/)
    puts "Framed hex: #{hex}"
  end
end
