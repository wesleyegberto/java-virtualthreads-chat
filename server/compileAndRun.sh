#!/usr/bin/env bash
set -e errexit

if [[ -d build/ ]]
then
	rm build/*
fi

echo 'Usage: `sh compileAndRun.sh [mode]`
Mode argument values:
- `M` for monothread (default);
- `P` for multithread using platform threads;
- `V` for multithread using virtual threads.
'

javac -d build --enable-preview -source 19 source/*.java
java -cp .:build --enable-preview Main "$1"
