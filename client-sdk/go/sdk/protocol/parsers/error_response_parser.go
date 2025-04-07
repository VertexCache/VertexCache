package parsers

import (
	"strings"

	"vertexcache/sdk/results"
)

type ErrorResponseParser struct{}

func (p *ErrorResponseParser) CanParse(line string) bool {
	return strings.HasPrefix(line, "-")
}

func (p *ErrorResponseParser) Parse(line string) *results.VCacheResult {
	return results.NewErrorWithRaw(results.ServerError, line, line)
}
