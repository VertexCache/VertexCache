package parsers

import "vertexcache/sdk/results"

type NilResponseParser struct{}

func (p *NilResponseParser) CanParse(response string) bool {
	return response == "+(nil)"
}

func (p *NilResponseParser) Parse(response string) *results.VCacheResult {
	return results.NewNil()
}
