package console_app

import (
	"bufio"
	"fmt"
	"os"
	"sdk/core"
	"strings"
)

type CliRunner struct {
	client  core.VCacheClient
	options *core.VertexCacheSdkOptions
}

func NewCliRunner(client core.VCacheClient, options *core.VertexCacheSdkOptions) *CliRunner {
	return &CliRunner{
		client:  client,
		options: options,
	}
}

func (r *CliRunner) RunInteractiveAsync() {
	PrintWelcomeBanner(r.options)

	scanner := bufio.NewScanner(os.Stdin)
	for {
		PrintPrompt(r.options)

		if !scanner.Scan() {
			break
		}

		input := scanner.Text()
		if strings.TrimSpace(input) == "" {
			continue
		}

		if strings.EqualFold(strings.TrimSpace(input), "exit") {
			break
		}

		cmd, args := ParseCommand(input)
		result := r.client.RunCommand(cmd, args)

		if result.IsSuccess() {
			fmt.Println(result.Message())
		} else {
			fmt.Printf("[ERROR] %s\n", result.Message())
		}
	}
}
