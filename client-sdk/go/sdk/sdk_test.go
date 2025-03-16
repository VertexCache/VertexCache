package sdk

import "testing"

func TestHelloWorld(t *testing.T) {
    expected := "VertexCache SDK!"
    if result := HelloWorld(); result != expected {
        t.Errorf("expected %s but got %s", expected, result)
    }
}