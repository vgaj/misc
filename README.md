# Miscellaneous Programing Exercises
This repo contains a number of my programing exercises that someone might find useful to reuse.

## Arduino Christmas Lights (ChristmasLights)
This project contains simple Arduino programs that drive my 7 Chistmas lights.  I will upload details of the hardware setup at some point.

## JNA UDP
A UDP client implemented in JNA

## OpenCL/JOCL implementation of Finite State Automata (JoclFsaSearch)
This project contains an implementation of Finite State Automata for pattern searching in OpenCL.

The purpose of the exercise was to compare the performance of running on the CPU with running on the GPU to try to understand/quantify the performance overhead of tasking the GPU.

## Plain Language Encoder App
App using https://github.com/vgaj/ple

## XDP Pattern Search
This example contains an XDP program which will search all incoming packets for a pattern (byte sequence) and drop them there is a match. The byte sequence is set from user space. The user space program sets a FSA in a map which the XDP program uses.
