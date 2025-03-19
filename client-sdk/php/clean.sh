#!/bin/bash

echo "🔹 Cleaning all compiled and temporary files..."

# Remove Composer dependencies
echo "🧹 Removing vendor/ directories..."
rm -rf vendor/ sdk/vendor/ client/vendor/

# Remove Composer lock file
echo "🧹 Removing composer.lock..."
rm -f composer.lock sdk/composer.lock client/composer.lock

# Remove PHPUnit cache
echo "🧹 Removing PHPUnit cache..."
rm -rf .phpunit.result.cache sdk/.phpunit.result.cache client/.phpunit.result.cache

# Remove IDE and system files
echo "🧹 Removing IDE and system files..."
find . -name ".DS_Store" -delete
find . -name "Thumbs.db" -delete
find . -name "*.log" -delete
find . -name "*.cache" -delete
find . -name "*.tmp" -delete

# Remove temporary cache directories
echo "🧹 Removing cache directories..."
rm -rf sdk/.cache client/.cache

echo "Clean-up complete!"
