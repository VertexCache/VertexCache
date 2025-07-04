package com.vertexcache.sdk.model

class VertexCacheSdkException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}