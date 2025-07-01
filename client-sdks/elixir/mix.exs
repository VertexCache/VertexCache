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

defmodule VertexcacheSdk.MixProject do
  use Mix.Project

  def project do
    [
      app: :vertexcache_sdk,
      version: "1.0.0",
      elixir: "~> 1.15",
      start_permanent: Mix.env() == :prod,
      elixirc_paths: elixirc_paths(Mix.env()),
      test_paths: ["tests"],
      deps: deps(),
      description: "Elixir SDK for VertexCache — Secure, Fast, Multi-Index Caching Client",
      package: package(),
      licenses: ["Apache-2.0"],
      source_url: "https://github.com/vertexcache/vertexcache"
    ]
  end

  def application do
    [
      extra_applications: [:logger, :ssl, :public_key]
    ]
  end

  defp elixirc_paths(:test), do: ["sdk", "tests"]
  defp elixirc_paths(_), do: ["sdk"]

  defp deps do
    [
      {:meck, "~> 0.9", only: :test}
    ]
  end

  defp package do
    [
      name: "vertexcache_sdk",
      maintainers: ["Jason Lam"],
      licenses: ["Apache-2.0"],
      links: %{
        "GitHub" => "https://github.com/vertexcache/vertexcache"
      },
      files: ~w(sdk mix.exs README.md LICENSE)
    ]
  end
end
