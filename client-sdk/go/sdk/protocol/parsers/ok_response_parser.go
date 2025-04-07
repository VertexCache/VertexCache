package parsers

import "vertexcache/sdk/results"

type OkResponseParser struct{}

func (p *OkResponseParser) CanParse(response string) bool {
	return response == "+OK"
}

func (p *OkResponseParser) Parse(response string) *results.VCacheResult {
	return results.NewSuccessWithRaw(response)
}
