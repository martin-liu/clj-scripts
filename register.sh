### Get current dir
DIR=$( cd "$( dirname "$0" )" && pwd )
export PATH=$PATH:$DIR/bin
export TERM=screen-256color

if [ ! $EMACS ]; then
    # not in emacs
    ZSH_THEME=agnoster
else
    # in emacs
    ZSH_THEME=robbyrussell
fi

## Add percol functions
source $DIR/shell/percol/percol.rc

## The fuck
alias fuck='eval $(thefuck $(fc -ln -1 | tail -n 1)); fc -R'

function exists { which $1 &> /dev/null }

## docker for mac
if exists docker-machine; then
    command="docker-machine env vm 2> /dev/null"

    if [ $(docker-machine status vm) != "Running" ]; then
        echo "Starting docker machine 'vm' in background..."
        docker-machine start vm > /dev/null 2>&1 &
    fi

    eval $(eval $command)

fi

### Martin's script

e(){
    emacsclient $1 &
}

et(){
    emacsclient -t $1
}
