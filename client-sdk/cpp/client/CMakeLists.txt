cmake_minimum_required(VERSION 3.10)
project(HelloWorldClient LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable(HelloWorldClient src/main.cpp)

target_link_libraries(HelloWorldClient PRIVATE VertexCacheSDK)