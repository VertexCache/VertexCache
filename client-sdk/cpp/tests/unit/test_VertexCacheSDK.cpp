#include "gtest/gtest.h"
#include "VertexCacheSDK.h"

TEST(VertexCacheSDKTest, PingReturnsPong) {
    EXPECT_EQ(VertexCacheSDK::Ping(), "PONG");
}

TEST(VertexCacheSDKTest, SetAndGet) {
    VertexCacheSDK::Set("test_key", "test_value");
    EXPECT_EQ(VertexCacheSDK::Get("test_key"), "test_value");
}

TEST(VertexCacheSDKTest, DeleteKey) {
    VertexCacheSDK::Set("test_key", "test_value");
    VertexCacheSDK::Del("test_key");
    EXPECT_EQ(VertexCacheSDK::Get("test_key"), "");
}

int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
