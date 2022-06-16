#!/usr/bin/env bash

MAX_CONCURRENCY=$1
SLEEP_TIME=$2

if [ -z "$MAX_CONCURRENCY" ]; then
	MAX_CONCURRENCY=10
fi

if [ -z "$SLEEP_TIME" ]; then
	SLEEP_TIME=10
fi

while true
do
	END=$((RANDOM % MAX_CONCURRENCY + 1 ))
	echo "Creating $END clients"
	for i in $(seq $END)
	do
		echo "Starting client $i"
		telnet 127.0.0.1 8000 <&- >&- 2>&- & disown
	done
	sleep $SLEEP_TIME
done
