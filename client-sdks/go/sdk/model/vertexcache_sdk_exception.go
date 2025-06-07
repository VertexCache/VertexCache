// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
// ------------------------------------------------------------------------------

package model

type VertexCacheSdkException struct {
	msg string
}

func (e *VertexCacheSdkException) Error() string {
	return e.msg
}

func NewVertexCacheSdkException(msg string) error {
	return &VertexCacheSdkException{msg: msg}
}
