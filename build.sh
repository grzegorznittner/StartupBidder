#!/bin/bash
cd html5-boilerplate/build
ant clean
ant
cd ../..
git checkout -- war
ant clean compile

