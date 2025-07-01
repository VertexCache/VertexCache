require_relative "lib/vertexcache/version"

Gem::Specification.new do |spec|
  spec.name          = "vertexcache"
  spec.version       = Vertexcache::VERSION
  spec.authors       = ["Jason Lam"]
  spec.email         = ["contact@vertexcache.com"]  # Adjust if needed

  spec.summary       = "VertexCache Ruby SDK"
  spec.description   = "Official Ruby SDK for interacting with the VertexCache server."
  spec.homepage      = "https://www.vertexcache.com"
  spec.license       = "Apache-2.0"

  spec.files         = Dir["lib/**/*.rb"]
  spec.require_paths = ["lib"]

  # Development dependencies
  spec.add_development_dependency "rake", "~> 13.0"
  spec.add_development_dependency "rspec", "~> 3.0"

  # Optional metadata for RubyGems page
  spec.metadata = {
    "source_code_uri" => "https://github.com/VertexCache/VertexCache/tree/main/client-sdks/ruby",
    "changelog_uri"   => "https://github.com/VertexCache/VertexCache/releases",
    "documentation_uri" => "https://github.com/VertexCache/VertexCache/wiki"
  }
end
