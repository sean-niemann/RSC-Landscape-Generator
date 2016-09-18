#!/bin/sh
cd "$(dirname "$0")"
find . -type f -name '*.java' -exec perl -p -i -e $'s/\t/    /g' {} +
echo "DONE"