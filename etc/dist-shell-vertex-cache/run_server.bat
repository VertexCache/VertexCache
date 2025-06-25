@echo off
java -Dlog4j.configurationFile=vertex-cache-config\server\log4j2-vertexcache-server.xml ^
     -jar vertex-cache-server.jar ^
     --config=vertex-cache-config\server\.env
