package config

import (
	"bufio"
	"os"
	"strconv"
	"strings"
)

package config

import (
"bufio"
"os"
"strconv"
"strings"
)

func init() {
	LoadEnvFile("config/.env")
}

func LoadEnvFile(path string) {
	file, err := os.Open(path)
	if err != nil {
		return
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())

		if line == "" || strings.HasPrefix(line, "#") {
			continue
		}

		parts := strings.SplitN(line, "=", 2)
		if len(parts) != 2 {
			continue
		}

		key := strings.TrimSpace(parts[0])
		val := strings.TrimSpace(parts[1])

		if strings.HasPrefix(val, "\"") && strings.HasSuffix(val, "\"") {
			val = val[1 : len(val)-1]
			val = strings.ReplaceAll(val, `\n`, "\n")
		}

		os.Setenv(key, val)
	}
}

func GetString(key string, defaultVal string) string {
	val := os.Getenv(key)
	if strings.TrimSpace(val) == "" {
		return defaultVal
	}
	return val
}

func GetInt(key string, defaultVal int) int {
	val := GetString(key, "")
	i, err := strconv.Atoi(val)
	if err != nil {
		return defaultVal
	}
	return i
}

func GetBool(key string, defaultVal bool) bool {
	val := GetString(key, "")
	b, err := strconv.ParseBool(val)
	if err != nil {
		return defaultVal
	}
	return b
}
