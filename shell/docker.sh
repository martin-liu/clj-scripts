## docker for mac
if exists docker-machine; then
    command="docker-machine env vm 2> /dev/null"

    if [ $(docker-machine status vm) != "Running" ]; then
        docker-machine start vm > /dev/null 2>&1 && eval $(eval $command)
        echo "Starting docker machine 'vm' in background..."
    else
        eval $(eval $command)
    fi

fi
