# homecontroller
An android app extension for secure home automation without the need for external internet connected automation hubs

## About
This repo contains the source code necessary to create a companion app (Android) to complete home automation tasks using Openhab. The source code can be compiled through Android Studio.

## App structure
By default, the app contains generic sliders and light buttons that can be configured for use with custom backend solutions

## Dependencies
This project assumes a custom backend solution for receiving home automation commands. A network implementation is provided in the java source to identify and send specific activation commands to a resident service within the home network. A raspberry pi or similar low power microcontroller should be used to act as the in home controller backend. 

## Network connectivity
This project came about with the explicit goal of creating offline solutions to enable home automation. As a result, all communications are expected to work only when the controlling devices are on the same network. It is highly recommended to configure each part of the automation chain to only work within an isolated home network.