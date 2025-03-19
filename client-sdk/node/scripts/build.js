const { execSync } = require("child_process");
const fs = require("fs");

// Function to check if a directory exists
const dirExists = (dir) => fs.existsSync(dir) && fs.lstatSync(dir).isDirectory();

console.log("Building SDK...");
if (dirExists("sdk")) {
    execSync("cd sdk && npm install", { stdio: "inherit" });
} else {
    console.error("SDK directory not found. Skipping SDK build.");
}

console.log("Building Client...");
if (dirExists("client")) {
    execSync("cd client && npm install", { stdio: "inherit" });
} else {
    console.error("Client directory not found. Skipping Client build.");
}

console.log("Build complete!");
