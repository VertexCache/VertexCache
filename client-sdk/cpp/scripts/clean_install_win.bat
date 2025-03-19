@echo off
setlocal
echo Cleaning old build files...
rmdir /s /q build 2>nul

echo Creating new build directory...
mkdir build
cd build

echo Configuring and building project for Windows x64...
cmake ..
cmake --build .

echo Build complete.
endlocal
