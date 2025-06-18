// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// ------------------------------------------------------------------------------

package command

import (
	"strings"

	"github.com/vertexcache/client-sdks/go/sdk/comm"
)

// ExecuteCommand is a reusable shared executor for all commands.
func ExecuteCommand(cmd CommandInterface, client *comm.ClientConnector) CommandInterface {
	raw, sendErr := client.Send(cmd.BuildCommand())
	if sendErr != nil {
		cmd.SetFailure(sendErr.Error())
		return cmd
	}

	raw = strings.TrimSpace(raw)

	if strings.HasPrefix(raw, "+") {
		body := raw[1:]
		cmd.ParseResponse(body)
		if cmd.Error() == "" {
			cmd.SetSuccessWithResponse(body)
		}
	} else if strings.HasPrefix(raw, "-") {
		cmd.SetFailure(raw[1:])
	} else {
		cmd.SetFailure("Unexpected response: " + raw)
	}
	return cmd
}
