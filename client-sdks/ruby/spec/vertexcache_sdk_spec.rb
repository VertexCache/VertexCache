# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache
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
# ------------------------------------------------------------------------------

require_relative "spec_helper"
require "vertexcache_sdk"

RSpec.describe VertexCacheSDK do
  it "returns version" do
    expect(VertexCacheSDK::VERSION).not_to be_nil
  end

  it "responds to ping with mock data" do
    result = VertexCacheSDK.ping
    expect(result[:success]).to eq(true)
    expect(result[:message]).to match(/PONG/)
  end
end
