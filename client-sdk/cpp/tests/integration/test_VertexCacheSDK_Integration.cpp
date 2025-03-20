#include "gtest/gtest.h"
#include "VertexCacheSDK.h"
#include "VertexCacheSDKClient.h"

TEST(VertexCacheSDKIntegrationTest, ClientUsesSDKCorrectly) {
    VertexCacheSDKClient client;

    client.Set("integration_key", "integration_value");
    EXPECT_EQ(VertexCacheSDK::Get("integration_key"), "integration_value");

    client.Del("integration_key");
    EXPECT_EQ(VertexCacheSDK::Get("integration_key"), "");
}

int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
