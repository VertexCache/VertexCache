package parsers

import "vertexcache/sdk/results"

type ResponseParser interface {
	CanParse(response string) bool
	Parse(response string) *results.VCacheResult
}
