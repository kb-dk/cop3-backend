#!/usr/bin/env bash
set -e

echo 'Building war file'
mvn -DskipTests clean package
echo 'Deploying war to cop-devel-01.kb.dk'
scp target/cop3-backend*.war cop@cop-devel-01:services/webapps/cop.war
echo 'Done'
