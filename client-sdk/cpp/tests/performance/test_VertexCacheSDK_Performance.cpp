#include "gtest/gtest.h"
#include "VertexCacheSDK.h"
#include <chrono>

TEST(VertexCacheSDKPerformanceTest, SetPerformance) {
    auto start = std::chrono::high_resolution_clock::now();

    for (int i = 0; i < 100000; ++i) {
        VertexCacheSDK::Set("key_" + std::to_string(i), "value_" + std::to_string(i));
    }

    auto end = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> elapsed = end - start;
    std::cout << "Set Performance: " << elapsed.count() << " seconds\n";

    EXPECT_TRUE(elapsed.count() < 1.0);  // Expect to complete under 1 second
}

TEST(VertexCacheSDKPerformanceTest, GetPerformance) {
    for (int i = 0; i < 100000; ++i) {
        VertexCacheSDK::Set("key_" + std::to_string(i), "value_" + std::to_string(i));
    }

    auto start = std::chrono::high_resolution_clock::now();

    for (int i = 0; i < 100000; ++i) {
        VertexCacheSDK::Get("key_" + std::to_string(i));
    }

    auto end = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> elapsed = end - start;
    std::cout << "Get Performance: " << elapsed.count() << " seconds\n";

    EXPECT_TRUE(elapsed.count() < 1.0);
}

int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
