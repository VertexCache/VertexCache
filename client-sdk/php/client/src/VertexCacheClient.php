<?php

namespace vertexcache\client;

require __DIR__ . '/../../vendor/autoload.php';

use vertexcache\sdk\VertexCacheSDK;

class VertexCacheClient {
    private VertexCacheSDK $sdk;

    public function __construct() {
        $this->sdk = new VertexCacheSDK();
    }

    public function run(): void {
        echo $this->sdk->helloWorld() . "\n";
    }
}

// Execute the client
$client = new VertexCacheClient();
$client->run();
