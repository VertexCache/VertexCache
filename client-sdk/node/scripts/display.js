const { execSync } = require("child_process");
const fs = require("fs");

// Function to check if the client directory exists
const dirExists = (dir) => fs.existsSync(dir) && fs.lstatSync(dir).isDirectory();

console.log("Displaying Client Output...");
if (dirExists("client")) {
    try {
        execSync("node client/index.js", { stdio: "inherit" });
    } catch (err) {
        console.error("Error displaying client output:", err);
    }
} else {
    console.error("Client directory not found. Skipping client execution.");
}
