#!/bin/bash

# Note Kotlin as March 16, 2025, only supports Java 17 as the latest, you may need to need create
# alias or switch or use a tool like SDKMan to switch Java active versions

./gradlew clean build

./gradlew :sdk:test

./gradlew :client:run