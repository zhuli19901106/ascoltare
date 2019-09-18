#!/bin/bash -xe

ENTRANCE="com._4paradigm.kg.manager.KgManagerApplication"
SPID=`ps -aux | grep ${ENTRANCE} | grep -v grep | grep -v kill | awk '{print $2}'`

if [ -n "$SPID" ]; then
    kill ${SPID}
    while kill -0 ${SPID} 2>/dev/null;
        do echo "$ENTRANCE is shutting down...";
        sleep 1s;
    done
fi
