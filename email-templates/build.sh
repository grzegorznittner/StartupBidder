#!/bin/sh

#PAGES=`find . -name '*email.m4'|sed 's/^[^a-zA-Z0-9_-]*//;s/[.]m4$//'`
for i in `find . -name 'email*.m4' | sed 's/^[^a-zA-Z0-9_-]*//;s/[.]m4$//'`; do
	m4 ${i}.m4 > html/${i}.html;
done

