#!/bin/bash
set -e

echo "[elixir] Cleaning build artifacts..."

rm -rf config/
rm -rf _build/
rm -rf deps/
rm -rf dist/
rm -rf config/.env

echo "[elixir] Clean complete."
