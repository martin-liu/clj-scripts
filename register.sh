### Get current dir
DIR=$( cd "$( dirname "$0" )" && pwd )
export PATH=$PATH:$DIR/bin
export TERM=screen-256color

## Add percol functions
source $DIR/percol/percol.rc

## The fuck
alias fuck='eval $(thefuck $(fc -ln -1 | tail -n 1)); fc -R'

function exists { which $1 &> /dev/null }

## Boot2docker
if exists boot2docker; then
    eval "$(boot2docker shellinit)"
fi

### Martin's script

e(){
    emacsclient $1 &
}

et(){
    emacsclient -t $1
}
