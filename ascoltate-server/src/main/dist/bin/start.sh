#!/bin/bash -xe

max_memory() {
    local memory_limit=$1
    local ratio=${JAVA_MAX_MEM_RATIO:-50}
    echo "${memory_limit} ${ratio} 1048576" | awk '{printf "%d\n" , ($1*$2)/(100*$3) + 0.5}'
}

set_jvm_heap_size() {
    # Check for container memory limits/request and use it to set JVM Heap size.
    # Defaults to 50% of the limit/request value.
    if [ -n "$MY_MEM_LIMIT" ]; then
        export JVM_HEAP_SIZE="$( max_memory $MY_MEM_LIMIT )"
    elif [ -n "$MY_MEM_REQUEST" ]; then
        export JVM_HEAP_SIZE="$( max_memory $MY_MEM_REQUEST )"
    fi

    if [ -z "$JVM_HEAP_SIZE" ]; then
        echo "Unable to detect reasonable JVM heap size, not configuring JVM heap size"
    else
        echo "Setting JVM_HEAP_SIZE to ${JVM_HEAP_SIZE}M"
    fi
}

set_set_java_opts() {
    if [ -z "$JAVA_OPTS" ]; then
        echo "\$JAVA_OPTS not set, setting JVM max heap size"
        set_jvm_heap_size
        export JAVA_OPTS="$JAVA_OPTS -Xmx${JVM_HEAP_SIZE}m"
    elif [[ ! "$JAVA_OPTS" =~ "-Xmx" ]]; then
        echo "\$JAVA_OPTS set, but no JVM max heap size flag set, setting JVM max heap size"
        set_jvm_heap_size
        export JAVA_OPTS="$JAVA_OPTS -Xmx${JVM_HEAP_SIZE}m"
    elif [[ "$JAVA_OPTS" =~ "-Xmx" ]] && [ "$OVERWRITE_JVM_HEAPSIZE" == "true" ]; then
        echo "\$JAVA_OPTS set and JVM max heapsize already configured, but \$OVERWRITE_JVM_HEAPSIZE=true, setting JVM max heap size"
        set_jvm_heap_size
        export JAVA_OPTS="${JAVA_OPTS//-Xmx+([[:digit:]])[a-z]/-Xmx${JVM_HEAP_SIZE}m}"
    else
        echo "Not setting JVM max heap size, already set"
    fi
}


ENTRANCE="com.zhuli.ascoltate.server.AscoltateApplication"
# 获取模块工作目录的绝对路径
CWD=$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )

pushd ${CWD}
    echo "start the $ENTRANCE server"
    # 执行启动前先cd到start.sh所在目录，执行stop.sh
    bash ./bin/stop.sh 2>/dev/null || true
    # 根据环境变量设置jvm内存参数
    set_set_java_opts
    java $JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="${CWD}/tmp" \
        -cp ./conf:./lib/hive-storage-api-2.2.1.jar:./lib/* \
        $ENTRANCE \
        --spring.profiles.active=${prof:=prod} 2>&1
popd
