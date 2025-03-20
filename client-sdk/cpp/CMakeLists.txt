cmake_minimum_required(VERSION 3.10)
project(VertexCacheSDK LANGUAGES CXX)

# Set C++ Standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Enable shared library building
set(BUILD_SHARED_LIBS ON)

# Define paths
set(SDK_INCLUDE_DIR "${CMAKE_SOURCE_DIR}/sdk/include")
set(SDK_SRC_DIR "${CMAKE_SOURCE_DIR}/sdk/src")
set(CLIENT_INCLUDE_DIR "${CMAKE_SOURCE_DIR}/client/include")
set(CLIENT_SRC_DIR "${CMAKE_SOURCE_DIR}/client/src")
set(TESTS_DIR "${CMAKE_SOURCE_DIR}/tests")

# Create SDK Library
add_library(VertexCacheSDK SHARED ${SDK_SRC_DIR}/VertexCacheSDK.cpp)
target_include_directories(VertexCacheSDK PUBLIC ${SDK_INCLUDE_DIR})

# Create Client Executable
add_executable(VertexCacheSDKClient ${CLIENT_SRC_DIR}/VertexCacheSDKClient.cpp)
target_include_directories(VertexCacheSDKClient PUBLIC ${CLIENT_INCLUDE_DIR} ${SDK_INCLUDE_DIR})
target_link_libraries(VertexCacheSDKClient PRIVATE VertexCacheSDK)

# Define Output Directories
if(WIN32)
    set(LIBRARY_DIR "${CMAKE_BINARY_DIR}/sdk/win")
elseif(APPLE)
    if (CMAKE_OSX_ARCHITECTURES STREQUAL "arm64")
        set(LIBRARY_DIR "${CMAKE_BINARY_DIR}/sdk/mac/silicon")
    else()
        set(LIBRARY_DIR "${CMAKE_BINARY_DIR}/sdk/mac/intel")
    endif()
elseif(UNIX)
    set(LIBRARY_DIR "${CMAKE_BINARY_DIR}/sdk/linux")
endif()

file(MAKE_DIRECTORY "${LIBRARY_DIR}")
set_target_properties(VertexCacheSDK PROPERTIES LIBRARY_OUTPUT_DIRECTORY "${LIBRARY_DIR}")

message(STATUS "🔍 SDK library will be output to: ${LIBRARY_DIR}")

# Enable Testing
enable_testing()
find_package(GTest REQUIRED)
include_directories(${GTEST_INCLUDE_DIRS})

# Add Unit Tests
add_executable(VertexCacheSDKTests ${TESTS_DIR}/unit/test_VertexCacheSDK.cpp)
target_include_directories(VertexCacheSDKTests PUBLIC ${SDK_INCLUDE_DIR})
target_link_libraries(VertexCacheSDKTests PRIVATE VertexCacheSDK GTest::GTest GTest::Main)

# Register the test
add_test(NAME VertexCacheSDKTests COMMAND VertexCacheSDKTests)
