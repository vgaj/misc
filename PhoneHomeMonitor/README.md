# Purpose
Monitor NIC is promiscuous mode to identify services making http and https connections.
This is a WIP.

# Installation
This requires pcap to run so:
On Ubuntu: apt-get install libpcap-dev
On Centos: yum install libpcap-devel
On Mac: brew install libpcap
On Windows: choco install winpcap

# Running
sudo java -jar phonehomemonitor-0.0.1-SNAPSHOT.jar 
http://localhost:8080/