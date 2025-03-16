cmake_minimum_required(VERSION 3.10)
project(VertexCacheSDK VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(VertexCacheSDK STATIC
    src/VertexCacheSDK.cpp
)

target_include_directories(VertexCacheSDK PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# Enable testing
enable_testing()

# Add Google Test
find_package(GTest REQUIRED)
include_directories(${GTEST_INCLUDE_DIRS})

# Create test executable
add_executable(VertexCacheSDKTests
    tests/test_main.cpp
    tests/test_VertexCacheSDK.cpp
)

target_link_libraries(VertexCacheSDKTests PRIVATE VertexCacheSDK GTest::GTest GTest::Main)

# Register the tests
add_test(NAME VertexCacheSDKTests COMMAND VertexCacheSDKTests)
