package core

import "vertexcache/sdk/results"

type IVCacheClient interface {
	RunCommand(command string, args []string) *results.VCacheResult
}
