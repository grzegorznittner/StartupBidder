#!/bin/sh
echo "Building HTML pages from templates using M4..."
PAGES=`find . -name '*page.m4'|sed 's/^[^a-zA-Z0-9_-]*//;s/[.]m4$//'`
for i in $PAGES; do m4 ${i}.m4 > ../${i}.html; done
echo "... done!"

