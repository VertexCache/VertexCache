#include "gtest/gtest.h"
#include "VertexCacheSDKClient.h"

TEST(VertexCacheSDKClientTest, PingReturnsPong) {
    VertexCacheSDKClient client;
    EXPECT_EQ(client.Ping(), "PONG");
}

TEST(VertexCacheSDKClientTest, SetAndGet) {
    VertexCacheSDKClient client;
    client.Set("client_key", "client_value");
    EXPECT_EQ(client.Get("client_key"), "client_value");
}

TEST(VertexCacheSDKClientTest, DeleteKey) {
    VertexCacheSDKClient client;
    client.Set("client_key", "client_value");
    client.Del("client_key");
    EXPECT_EQ(client.Get("client_key"), "");
}

int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
