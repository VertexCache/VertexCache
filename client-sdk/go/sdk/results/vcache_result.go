package results

import (
	"fmt"
)

type VCacheResult struct {
	IsSuccess bool
	Message   string
	Code      VCacheErrorCode
	Raw       *string
}

func NewSuccessWithRaw(raw string) *VCacheResult {
	return &VCacheResult{
		IsSuccess: true,
		Message:   raw,
		Code:      None,
		Raw:       &raw,
	}
}

func NewErrorWithRaw(code VCacheErrorCode, message string, raw string) *VCacheResult {
	return &VCacheResult{
		IsSuccess: false,
		Message:   message,
		Code:      code,
		Raw:       &raw,
	}
}

func NewNil() *VCacheResult {
	return &VCacheResult{
		IsSuccess: true,
		Message:   "(nil)",
		Code:      None,
		Raw:       nil,
	}
}

func Success(message string) *VCacheResult {
	return &VCacheResult{
		IsSuccess: true,
		Message:   message,
		Code:      None,
	}
}

func Failure(code VCacheErrorCode, message string) *VCacheResult {
	return &VCacheResult{
		IsSuccess: false,
		Message:   message,
		Code:      code,
	}
}

func (r *VCacheResult) String() string {
	if r.IsSuccess {
		return fmt.Sprintf("+%s", r.Message)
	}
	return fmt.Sprintf("-%s", r.Message)
}
