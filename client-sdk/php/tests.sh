#!/bin/bash

echo "Running SDK tests..."
cd sdk
vendor/bin/phpunit --configuration ../phpunit.xml
cd ..
