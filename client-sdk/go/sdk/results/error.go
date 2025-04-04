package results

import "fmt"

// ErrorCode defines a list of standard error types.
type ErrorCode string

const (
	ErrInvalidCommand ErrorCode = "ERR_INVALID_COMMAND"
	ErrConnection     ErrorCode = "ERR_CONNECTION"
	ErrTLS            ErrorCode = "ERR_TLS"
	ErrKeyParsing     ErrorCode = "ERR_KEY_PARSING"
	ErrUnknown        ErrorCode = "ERR_UNKNOWN"
)

// SDKError represents a structured error with a code, message, and optional cause.
type SDKError struct {
	Code    ErrorCode // e.g. ERR_CONNECTION
	Message string     // user-facing or dev-facing description
	Cause   error      // underlying Go error, if any
}

// Error implements the built-in error interface.
func (e *SDKError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("[%s] %s: %v", e.Code, e.Message, e.Cause)
	}
	return fmt.Sprintf("[%s] %s", e.Code, e.Message)
}

// New creates a new SDKError instance.
func New(code ErrorCode, message string, cause error) *SDKError {
	return &SDKError{
		Code:    code,
		Message: message,
		Cause:   cause,
	}
}
