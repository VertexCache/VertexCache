#include "VertexCacheSDK.h"
#include <unordered_map>
#include <mutex>

namespace VertexCacheSDK {

    // Internal cache storage
    static std::unordered_map<std::string, std::string> cache;
    static std::mutex cache_mutex;

    std::string Ping() {
        return "PONG";
    }

    void Set(const std::string& key, const std::string& value) {
        std::lock_guard<std::mutex> lock(cache_mutex);
        cache[key] = value;
    }

    std::string Get(const std::string& key) {
        std::lock_guard<std::mutex> lock(cache_mutex);
        auto it = cache.find(key);
        return (it != cache.end()) ? it->second : "";
    }

    void Del(const std::string& key) {
        std::lock_guard<std::mutex> lock(cache_mutex);
        cache.erase(key);
    }

}
