#!/bin/bash
cd src
javac *.java
mv * ../build
cd ../build
mv *.java ../src
java Game
