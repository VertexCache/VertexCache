@echo off
java -Dlog4j.configurationFile=vertex-cache-config\console\log4j2-vertexcache-console.xml ^
     -jar vertex-cache-console.jar ^
     --config=vertex-cache-config\console\.env
