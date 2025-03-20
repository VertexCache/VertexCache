#ifndef VERTEXCACHESDK_H
#define VERTEXCACHESDK_H

#include <string>

namespace VertexCacheSDK {
    /**
     * @brief Ping the SDK to check if it's responsive.
     * @return "PONG" if the SDK is working correctly.
     */
    std::string Ping();

    /**
     * @brief Store a value with a specific key in the cache.
     * @param key The key to associate the value with.
     * @param value The value to store.
     */
    void Set(const std::string& key, const std::string& value);

    /**
     * @brief Retrieve a value from the cache using a key.
     * @param key The key to look up.
     * @return The value associated with the key, or an empty string if not found.
     */
    std::string Get(const std::string& key);

    /**
     * @brief Delete a value from the cache by key.
     * @param key The key to remove from the cache.
     */
    void Del(const std::string& key);
}

#endif // VERTEXCACHESDK_H
