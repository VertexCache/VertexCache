package protocol

import (
	"vertexcache/sdk/results"
)

func Parse(line string) *results.VCacheResult {
	registry := NewResponseParserRegistry()
	return registry.Parse(line)
}
