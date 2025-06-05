Gem::Specification.new do |spec|
  spec.name          = "vertexcache"
  spec.version       = VertexCacheSDK::VERSION
  spec.authors       = ["Jason Lam"]


  spec.summary       = "VertexCache Ruby SDK"
  spec.description   = "Ruby SDK for interacting with VertexCache server."
  spec.license       = "Apache-2.0"

  spec.files         = Dir["lib/**/*.rb"]
  spec.require_paths = ["lib"]

  spec.add_runtime_dependency "rake"
  spec.add_development_dependency "rspec"
end
