package parsers

import "vertexcache/sdk/results"

type PongResponseParser struct{}

func (p *PongResponseParser) CanParse(response string) bool {
	return response == "+PONG"
}

func (p *PongResponseParser) Parse(response string) *results.VCacheResult {
	return results.NewSuccessWithRaw(response)
}
