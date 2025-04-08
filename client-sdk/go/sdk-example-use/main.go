package main

import (
	"context"
	"fmt"
	"log"

	"github.com/joho/godotenv"
	"vertexcache/sdk" // Correctly referencing the sdk package
)

func main() {
	// Load .env from ../config
	err := godotenv.Load("../config/.env")
	if err != nil {
		log.Fatalf("❌ Failed to load .env: %v", err)
	}

	// Create SDK options from environment
	opts := sdk.NewVertexCacheSdkOptionsFromEnv()
	client := sdk.NewVertexCacheSdk(opts)
	ctx := context.Background()

	fmt.Println("🔗 Connecting to:", opts.ServerHost, opts.ServerPort)

	// PING
	ping := client.Ping(ctx)
	fmt.Printf("PING ➜ %s: %s\n", ping.Status, ping.Message)
}
