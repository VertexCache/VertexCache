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

  it 'writes then reads a framed message' do
    original = "Hello VertexCache"
    out = StringIO.new
    codec.write_framed_message(out, original)
    out.rewind

    result = codec.read_framed_message(out)
    expect(result).to eq(original)
  end

  it 'raises on invalid version byte' do
    bad = [3].pack('N') + [0x02].pack('C') + "abc"
    io = StringIO.new(bad)
    expect { codec.read_framed_message(io) }.to raise_error(IOError, /Invalid version/)
  end

  it 'returns nil if header too short' do
    io = StringIO.new("\x01\x02")
    result = codec.read_framed_message(io)
    expect(result).to be_nil
  end

  it 'raises if payload is too large' do
    too_big = "A" * (codec::MAX_MESSAGE_SIZE + 1)
    out = StringIO.new
    expect { codec.write_framed_message(out, too_big) }.to raise_error(IOError, /Payload too large/)
  end

  it 'writes empty payload but fails to read' do
    out = StringIO.new
    codec.write_framed_message(out, "")
    out.rewind

    expect { codec.read_framed_message(out) }.to raise_error(IOError, /Invalid message length/)
  end

  it 'handles UTF-8 multibyte payloads correctly' do
    original = "你好, VertexCache 🚀"
    out = StringIO.new
    codec.write_framed_message(out, original)
    out.rewind

    result = codec.read_framed_message(out)
    expect(result.force_encoding("UTF-8")).to eq(original)
  end

  it 'prints hex dump for inter-SDK comparison' do
    payload = "ping"
    out = StringIO.new
    codec.write_framed_message(out, payload)
    hex = out.string.unpack1("H*").upcase
    puts "Framed hex: #{hex}"
  end
end
