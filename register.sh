#!env zsh

### use gnu utils
export PATH=/usr/local/opt/gnu-sed/libexec/gnubin:/usr/local/opt/gnu-tar/libexec/gnubin:/usr/local/opt/coreutils/libexec/gnubin:$PATH

### Get current dir
DIR=$( cd "$( dirname "$0" )" && pwd )
export PATH=$PATH:$DIR/bin
export TERM=screen-256color

## Languages
### GO
export GOPATH=~/martin/code/go
export PATH=$PATH:$GOPATH/bin

## The fuck
alias fuck='eval $(thefuck $(fc -ln -1 | tail -n 1)); fc -R'

function exists { which $1 &> /dev/null }

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

source $DIR/shell/util.sh
source $DIR/shell/alias.sh
