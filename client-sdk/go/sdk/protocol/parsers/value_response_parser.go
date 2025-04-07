package parsers

import "vertexcache/sdk/results"
import "strings"

type ValueResponseParser struct{}

func (p *ValueResponseParser) CanParse(response string) bool {
	return strings.HasPrefix(response, "+") &&
		response != "+OK" &&
		response != "+PONG" &&
		response != "+(nil)" &&
		response != "+Deleted"
}

func (p *ValueResponseParser) Parse(response string) *results.VCacheResult {
	return results.NewSuccessWithRaw(response)
}
