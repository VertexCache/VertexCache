package protocol

import (
	"strings"

	"vertexcache/sdk/protocol/parsers"
	"vertexcache/sdk/results"
)

type ResponseParser interface {
	CanParse(line string) bool
	Parse(line string) *results.VCacheResult
}

type ResponseParserRegistry struct {
	parsers []ResponseParser
}

func NewResponseParserRegistry() *ResponseParserRegistry {
	return &ResponseParserRegistry{
		parsers: []ResponseParser{
			&parsers.PongResponseParser{},
			&parsers.OkResponseParser{},
			&parsers.ErrorResponseParser{},
			&parsers.NilResponseParser{},
			&parsers.DeletedResponseParser{},
			&parsers.ValueResponseParser{},
		},
	}
}

func (r *ResponseParserRegistry) Parse(line string) *results.VCacheResult {
	for _, parser := range r.parsers {
		if parser.CanParse(line) {
			return parser.Parse(line)
		}
	}

	if strings.HasPrefix(line, "+") {
		return results.Success(line)
	}

	return results.Failure(results.Unknown, "Unknown response: "+line)
}
