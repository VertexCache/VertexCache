defmodule VertexCacheSDK.MixProject do
  use Mix.Project

  def project do
    [
      app: :vertex_cache_sdk,
      version: "1.0.0",
      elixir: "~> 1.15",
      start_permanent: Mix.env() == :prod,
      deps: deps(),
      elixirc_paths: elixirc_paths(Mix.env()),
      description: "VertexCache Elixir Client SDK",
      package: [
        name: "vertex_cache_sdk",
        maintainers: ["Jason Lam"],
        licenses: ["MIT"],
        links: %{"GitHub" => "https://github.com/jasonlam604/VertexCache"}
      ]
    ]
  end

  def application do
    [
      extra_applications: [:logger, :ssl]
    ]
  end

  defp elixirc_paths(:test), do: ["lib", "test/support"]
  defp elixirc_paths(_),     do: ["lib"]

  defp deps do
    [
      {:dotenv_parser, "~> 2.0", only: [:dev, :test]}
    ]
  end

end
