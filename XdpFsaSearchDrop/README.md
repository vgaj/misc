# Description
This example contains an XDP program which will search all incoming packets for a pattern (byte sequence) and drop them if they match.
The byte sequence is set from user space.  The user space program sets a FSA in a map which the XDP program uses

## Compile
gcc user_space_set_pattern.c -lbpf -lelf -o user_space_set_pattern
clang -I/usr/src/linux-headers-$(uname -r)/tools/bpf/resolve_btfids/libbpf/include -O2 -g -target bpf -c xdp_search_drop.c -o xdp_search_drop.o

## Load XDP Program
sudo ip link set dev wlo1 xdp obj xdp_search_drop.o sec drop_packets_matching_pattern

## Set the pattern in user space
sudo ./user_space_set_pattern

## Unload XDP Program
sudo ip link set dev wlo1 xdp off
