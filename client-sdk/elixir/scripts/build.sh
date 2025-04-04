#!/bin/bash
set -e

echo "[elixir] Fetching dependencies..."
mix deps.get

echo "[elixir] Building SDK and client..."
mix compile
