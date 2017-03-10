function m_add_ssh_key() {
    cat ~/.ssh/id_rsa.pub | ssh $1 'cat >> /tmp/1.txt && mkdir -p .ssh && cat /tmp/1.txt >> .ssh/authorized_keys'
}
