const { execSync } = require("child_process");

console.log("Cleaning SDK and Client...");
["sdk", "client"].forEach((dir) => {
    try {
        execSync(`rm -rf ${dir}/node_modules ${dir}/dist ${dir}/package-lock.json`, { stdio: "inherit" });
    } catch (err) {
        console.error(`Failed to clean ${dir}:`, err);
    }
});
console.log("Clean complete!");
