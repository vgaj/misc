#!/bin/bash

echo "*** Building..."
mvn clean install

echo "*** Stopping..."
sudo systemctl stop phm.service

echo "*** Deploying..."
sudo mkdir -p /opt/phm
#sudo cp target/phonehomemonitor*.jar /opt/phm/
cwd=$(pwd)
sudo rm -f /opt/phm/phonehomemonitor-0.0.1-SNAPSHOT.jar
sudo ln -s $cwd/target/phonehomemonitor-0.0.1-SNAPSHOT.jar /opt/phm/phonehomemonitor-0.0.1-SNAPSHOT.jar
sudo cp src/main/resources/phm.service /etc/systemd/system/

echo "*** Enabling..."
sudo systemctl daemon-reload
sudo systemctl enable --now phm.service

