### Get current dir
DIR=$( cd "$( dirname "$0" )" && pwd )
export PATH=$PATH:$DIR/bin
export TERM=screen-256color

## Languages
### GO
export GOPATH=~/.go
export PATH=$PATH:$GOPATH/bin

## Add percol functions
source $DIR/shell/percol/percol.rc

## The fuck
alias fuck='eval $(thefuck $(fc -ln -1 | tail -n 1)); fc -R'

function exists { which $1 &> /dev/null }

## docker for mac
if exists docker-machine; then
    command="docker-machine env default 2> /dev/null"

    if [ $(docker-machine status default) != "Running" ]; then
        echo "Starting docker machine 'default' in background..."
        docker-machine start default > /dev/null 2>&1 &
    fi

    eval $(eval $command)

fi

## hub
if exists hub; then
    alias git=hub
fi

## emacs

e(){
    visible_frames() {
        emacsclient -a "" -e '(length (visible-frame-list))'
    }

    change_focus() {
        emacsclient -n -e "(select-frame-set-input-focus (selected-frame))" > /dev/null
    }

    # try switching to the frame incase it is just minimized
    # will start a server if not running
    test "$(visible_frames)" -eq "1" && change_focus

    if [ "$(visible_frames)" -lt  "2" ]; then # need to create a frame
        # -c $@ with no args just opens the scratch buffer
        emacsclient -n -c "$@" && change_focus
    else # there is already a visible frame besides the daemon, so
        change_focus
        # -n $@ errors if there are no args
        test  "$#" -ne "0" && emacsclient -n "$@"
    fi
}

et(){
    exec emacsclient -a "" -t "$@"
}
