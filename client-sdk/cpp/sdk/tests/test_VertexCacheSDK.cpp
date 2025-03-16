#include <gtest/gtest.h>
#include "VertexCacheSDK.h"

TEST(VertexCacheSDKTest, GetMessageReturnsHelloWorld) {
    std::string message = VertexCacheSDK::SDK::getMessage();
    EXPECT_EQ(message, "Hello, World! from VertexCacheSDK");
}