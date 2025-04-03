defmodule VertexCacheSDKClient.MixProject do
  use Mix.Project

  def project do
    [
      app: :vertex_cache_sdk_client,
      version: "1.0.0",
      elixir: "~> 1.15",
      start_permanent: Mix.env() == :prod,
      deps: deps()
    ]
  end

  def application do
    [
      extra_applications: [:logger]
    ]
  end

  defp deps do
    [
      {:vertex_cache_sdk, path: "../elixir"}
    ]
  end
end
