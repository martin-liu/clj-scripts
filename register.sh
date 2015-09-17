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
source $DIR/percol/percol.rc

## The fuck
alias fuck='eval $(thefuck $(fc -ln -1 | tail -n 1)); fc -R'

function exists { which $1 &> /dev/null }

## Boot2docker
if exists boot2docker; then
    if [ $(boot2docker status) != "running" ]; then
        eval "$(boot2docker up)"
    fi

    eval "$(boot2docker shellinit &> /dev/null)"
fi

### Martin's script

e(){
    emacsclient $1 &
}

et(){
    emacsclient -t $1
}
