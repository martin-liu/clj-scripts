function m_add_ssh_key() {
    cat ~/.ssh/id_rsa.pub | ssh $(m ssh g $1) 'cat >> /tmp/1.txt && mkdir -p .ssh && cat /tmp/1.txt >> .ssh/authorized_keys'
}

# Grant user access && sudo to a remote server
# Usage: `m_grant_user_sudo_access SERVER USER`
function m_grant_user_sudo_access() {
    server=$1
    usr=$2
    if [ -z "$server" ] || [ -z "$usr" ]
    then
        echo "no Server or User specific" && return
    fi
    command="
sudo sed -i 's|-:ALL:ALL|+:${usr}:ALL\n-:ALL:ALL|g' /etc/security/access.conf && \
echo '$usr ALL=(ALL) NOPASSWD:ALL' | sudo tee --append /etc/sudoers.d/ldapuser
"
    m ssh s $server $command
}

# ansible tora -a "key='SSH_PUB_KEY' user=root" -m authorized_key -u hualiu -s -k -K
function m_ansible_add_ssh_key() {
    group=$1
    usr=$2
    shift
    shift
    option=$@

    if [ -z "$group" ] || [ -z "$usr" ]
    then
        echo "no Ansible Group or User specific" && return
    fi

    command="key={{ lookup('file', '~/.ssh/id_rsa.pub') }} user=$usr"
    ansible $group -a "su $usr" -s $option
    ansible $group -a "$command" -m authorized_key -s $option
}

function m_ansible_grant_user_sudo_access() {
    group=$1
    usr=$2
    shift
    shift
    option=$@

    if [ -z "$group" ] || [ -z "$usr" ]
    then
        echo "no Ansible Group or User specific" && return
    fi
    command="
sudo sed -i 's|-:ALL:ALL|+:${usr}:ALL\n-:ALL:ALL|g' /etc/security/access.conf && \
echo '$usr ALL=(ALL) NOPASSWD:ALL' | sudo tee --append /etc/sudoers.d/ldapuser
"
    ansible $group -a "$command" -m shell -s $option
}

# ansible tora -m copy -a "src=docker-engine_1.12.5-0~ubuntu-trusty_amd64.deb dest=~"
# for Ubuntu 16
function m_ansible_install_docker() {
    group=$1
    shift
    option=$@
    ansible $group -a "wget 'https://apt.dockerproject.org/repo/pool/main/d/docker-engine/docker-engine_1.13.1-0~ubuntu-xenial_amd64.deb'" --sudo $option
    ansible $group -a "apt-get update && apt-get install -y libltdl7 && dpkg -i docker-engine_1.13.1-0~ubuntu-xenial_amd64.deb" --sudo $option
}


function m_ansible_setup_ssh() {

}

### MAC OS
## trigger sidecar to ipad
function m_sidecar() {
  cat <<EOF > /tmp/m_sidecar.scpt
set iPadName to "iPad"

tell application "System Events"
   tell application process "ControlCenter"
       repeat with tElement in menu bar items of menu bar 1
           if (exists attribute "AXTitle" of tElement) then
               if value of attribute "AXTitle" of tElement contains "Display" then
                   set theBar to tElement
                   tell theBar
                       click
                   end tell
                   set tElements to entire contents of window 1
                   repeat with i from 1 to count of tElements
                       set tElement to item i of tElements
                       if (title of tElement contains iPadName) then
                           tell tElement
                               click
                           end tell
                           exit repeat
                       end if
                   end repeat
                   tell theBar
                       click
                   end tell
                   exit repeat
               end if
           end if
       end repeat
   end tell
end tell
EOF
  
  osascript /tmp/m_sidecar.scpt
}

