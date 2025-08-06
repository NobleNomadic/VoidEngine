#!/bin/bash
set -e

cd src
javac GameEngine.java Game.java
mv *.class ../build
cd ../build
java Game
cd ..
