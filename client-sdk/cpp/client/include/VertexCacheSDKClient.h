#ifndef VERTEXCACHESDKCLIENT_H
#define VERTEXCACHESDKCLIENT_H

#include <string>

class VertexCacheSDKClient {
public:
    /**
     * @brief Constructor
     */
    VertexCacheSDKClient();

    /**
     * @brief Destructor
     */
    ~VertexCacheSDKClient();

    /**
     * @brief Pings the SDK to check connectivity.
     * @return The SDK's response.
     */
    std::string Ping();

    /**
     * @brief Stores a value with a key.
     * @param key The key identifier.
     * @param value The value to store.
     */
    void Set(const std::string& key, const std::string& value);

    /**
     * @brief Retrieves a value by key.
     * @param key The key identifier.
     * @return The associated value, or an empty string if not found.
     */
    std::string Get(const std::string& key);

    /**
     * @brief Deletes a key-value pair.
     * @param key The key identifier.
     */
    void Del(const std::string& key);
};

#endif // VERTEXCACHESDKCLIENT_H
