#include "VertexCacheSDKClient.h"
#include "VertexCacheSDK.h"  // Include the core SDK

#include <iostream>

VertexCacheSDKClient::VertexCacheSDKClient() {
    std::cout << "VertexCacheSDKClient initialized." << std::endl;
}

VertexCacheSDKClient::~VertexCacheSDKClient() {
    std::cout << "VertexCacheSDKClient destroyed." << std::endl;
}

std::string VertexCacheSDKClient::Ping() {
    return VertexCacheSDK::Ping();
}

void VertexCacheSDKClient::Set(const std::string& key, const std::string& value) {
    VertexCacheSDK::Set(key, value);
}

std::string VertexCacheSDKClient::Get(const std::string& key) {
    return VertexCacheSDK::Get(key);
}

void VertexCacheSDKClient::Del(const std::string& key) {
    VertexCacheSDK::Del(key);
}

/**
 * @brief Main function to demonstrate the Client usage.
 */
int main() {
    VertexCacheSDKClient client;

    std::cout << "Ping Response: " << client.Ping() << std::endl;

    client.Set("my_key", "my_value");
    std::cout << "Get 'my_key': " << client.Get("my_key") << std::endl;

    client.Del("my_key");
    std::cout << "Get 'my_key' after delete: " << client.Get("my_key") << std::endl;

    return 0;
}
