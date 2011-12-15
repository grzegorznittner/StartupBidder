#!/bin/sh
PAGES="main-page top-page latest-page nearby-page closing-page industry-internet-page"
for i in $PAGES; do m4 ${i}.m4 > ${i}.html; done

