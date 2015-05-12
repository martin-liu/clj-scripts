### Get current dir
DIR=$( cd "$( dirname "$0" )" && pwd )
export PATH=$PATH:$DIR/bin

## Add percol functions
source $DIR/percol/percol.rc

## The fuck
alias fuck='eval $(thefuck $(fc -ln -1 | tail -n 1)); fc -R'

### Martin's script

e(){
    emacsclient $1 &
}

et(){
    emacsclient -t $1
}
