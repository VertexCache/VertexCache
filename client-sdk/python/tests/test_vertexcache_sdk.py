import unittest
from sdk.vertexcache_sdk import VertexCacheSDK

class TestVertexCacheSDK(unittest.TestCase):
    def test_greet(self):
        sdk = VertexCacheSDK()
        self.assertEqual(sdk.greet(), "VertexCache SDK!")

if __name__ == "__main__":
    unittest.main()
