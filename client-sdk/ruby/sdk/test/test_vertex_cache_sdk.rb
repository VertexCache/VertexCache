require 'minitest/autorun'
require_relative '../lib/vertex_cache_sdk'

class TestVertexCacheSdk < Minitest::Test
  def test_print_message
    assert_equal "VertexCache SDK!", VertexCacheSdk.print_message
  end
end
