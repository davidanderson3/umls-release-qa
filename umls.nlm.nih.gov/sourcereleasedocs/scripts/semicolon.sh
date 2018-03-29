#!/bin/sh

find . -path "*/jbake/content/*" -name "metadata.html" -print | xargs sed -i '' -e 's/;;*/;/g'
find . -path "*/jbake/content/*" -name "metadata.html" -print | xargs sed -i '' -e 's/;;*/;/g'
find . -path "*/jbake/content/*" -name "metadata.html" -print | xargs sed -i '' -e 's/>;/>/g'
find . -path "*/jbake/content/*" -name "metadata.html" -print | xargs sed -i '' -e 's/;/\<br \/>/g'
