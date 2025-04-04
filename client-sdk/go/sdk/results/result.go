package results

// Result represents the outcome of a VertexCache SDK operation.
type Result struct {
	Success bool       // true if the operation succeeded
	Data    string     // optional payload for successful commands
	Error   *SDKError  // structured error info if Success == false
}

// NewSuccess creates a success result with optional data.
func NewSuccess(data string) *Result {
	return &Result{
		Success: true,
		Data:    data,
		Error:   nil,
	}
}

// NewFailure creates a failed result with an SDKError.
func NewFailure(err *SDKError) *Result {
	return &Result{
		Success: false,
		Data:    "",
		Error:   err,
	}
}
