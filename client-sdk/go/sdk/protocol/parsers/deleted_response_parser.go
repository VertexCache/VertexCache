package parsers

import (
	"strings"

	"vertexcache/sdk/results"
)

type DeletedResponseParser struct{}

func (p *DeletedResponseParser) CanParse(line string) bool {
	return strings.HasPrefix(line, "+Deleted")
}

func (p *DeletedResponseParser) Parse(line string) *results.VCacheResult {
	return results.NewSuccessWithRaw(line)
}
