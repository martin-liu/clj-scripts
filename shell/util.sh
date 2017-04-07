function m_add_ssh_key() {
    cat ~/.ssh/id_rsa.pub | ssh $1 'cat >> /tmp/1.txt && mkdir -p .ssh && cat /tmp/1.txt >> .ssh/authorized_keys'
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

function m_ansible_install_docker() {
    group=$1
    ansible $group -a "wget 'https://apt.dockerproject.org/repo/pool/main/d/docker-engine/docker-engine_1.12.5-0~ubuntu-trusty_amd64.deb'" --sudo
    ansible $group -a "apt-get install -y libltdl7 libsystemd-journal0" --sudo
    ansible $group -a "dpkg -i docker-engine_1.12.5-0~ubuntu-trusty_amd64.deb" --sudo
}
