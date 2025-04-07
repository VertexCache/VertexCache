package transport

type VCacheConn interface {
	Send(line string) (int, error)
	SendRaw(data []byte) (int, error)
	ReadLine() (string, error)
	Dispose()
}
