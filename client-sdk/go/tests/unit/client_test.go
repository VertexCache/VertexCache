package core_test

import (
	"testing"

	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/core"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/protocol"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/results"
)

type mockConnection struct {
	SendFunc        func(string) error
	ReceiveLineFunc func() (string, error)
	CloseFunc       func() error
}

func (m *mockConnection) Send(data string) error               { return m.SendFunc(data) }
func (m *mockConnection) ReceiveLine() (string, error)         { return m.ReceiveLineFunc() }
func (m *mockConnection) Close() error                         { return m.CloseFunc() }

func TestEmptyCommand_ShouldReturnFailure(t *testing.T) {
	conn := &mockConnection{
		SendFunc:        func(data string) error { return nil },
		ReceiveLineFunc: func() (string, error) { return "", nil },
		CloseFunc:       func() error { return nil },
	}

	client := core.NewClient(conn)

	cmd := &protocol.Command{}
	result := client.Set("", "")

	if result.Success {
		t.Fatal("expected failure on empty key")
	}
	if result.Error == nil || result.Error.Code != results.ErrConnection {
		t.Errorf("expected connection error, got %v", result.Error)
	}
}
