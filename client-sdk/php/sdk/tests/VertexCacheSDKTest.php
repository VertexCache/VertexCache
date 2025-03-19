<?php

use PHPUnit\Framework\TestCase;
use vertexcache\sdk\VertexCacheSDK;

class VertexCacheSDKTest extends TestCase {
    public function testHelloWorld() {
        $sdk = new VertexCacheSDK();
        $this->assertEquals("Hello, world from VertexCache SDK!", $sdk->helloWorld());
    }
}
