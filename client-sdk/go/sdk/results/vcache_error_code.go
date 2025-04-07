package results

type VCacheErrorCode int

const (
	None            VCacheErrorCode = iota // 0
	Unknown                                // 1
	InvalidCommand                         // 2
	NetworkFailure                         // 3
	EncryptionError                        // 4
	ProtocolError                          // 5
	ServerError                            // 6
)
