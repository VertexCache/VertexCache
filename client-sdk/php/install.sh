#!/bin/bash

echo "Installing dependencies..."
composer install
cd sdk && composer install
cd ../client && composer install
cd ..
echo "Installation complete."