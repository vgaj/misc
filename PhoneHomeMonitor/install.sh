#!/bin/bash

echo "*** Building..."
mvn clean install

echo "*** Stopping..."
sudo systemctl stop phm.service

echo "*** Deploying..."
sudo mkdir -p /opt/phm
sudo cp target/phonehomemonitor*.jar /opt/phm/
sudo cp src/main/resources/phm.service /etc/systemd/system/

echo "*** Enabling..."
sudo systemctl daemon-reload
sudo systemctl enable --now phm.service

