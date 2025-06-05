<?php
// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// You may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

use PHPUnit\Framework\TestCase;
use VertexCache\VertexCacheSDK;

class VertexCacheSDKTest extends TestCase
{
    public function testPingShouldReturnTrue()
    {
        $sdk = new VertexCacheSDK();
        $this->assertTrue($sdk->ping(), "Ping should return true");
    }
}
