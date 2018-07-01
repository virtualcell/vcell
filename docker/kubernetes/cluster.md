disable swap on all Kubernetes nodes (per https://kubernetes.io/docs/setup/independent/install-kubeadm/)

```bash
sudo free -h
sudo blkid
sudo lsblk
sudo swapoff /dev/mapper/cl-swap
sudo free -h
```

# comment out swap line in fstab
sudo vi /etc/fstab

# reboot
sudo init 6
```

verify network ports available:

Check required ports
Master node(s)
Protocol	Direction	Port Range		Purpose
TCP			Inbound		6443*			Kubernetes API server
TCP			Inbound		2379-2380		etcd server client API
TCP			Inbound		10250			Kubelet API
TCP			Inbound		10251			kube-scheduler
TCP			Inbound		10252			kube-controller-manager
TCP			Inbound		10255			Read-only Kubelet API
Worker node(s)
Protocol	Direction	Port Range		Purpose
TCP			Inbound		10250			Kubelet API
TCP			Inbound		10255			Read-only Kubelet API
TCP			Inbound		30000-32767		NodePort Services**

## install docker [on centos](https://docs.docker.com/install/linux/docker-ce/centos/#install-docker-ce-1)

set up yum docker stable repository (first time only)

```bash
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-complete-transaction --cleanup-only
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

install appropriate version of docker and start docker (should be same across the swarm)

```bash
sudo yum install -y docker-ce-18.03.0.ce
sudo systemctl enable docker
sudo systemctl start docker
```

```bash
sudo -i
```


```bash
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
EOF
```

```bash
setenforce 0
yum install -y kubelet kubeadm kubectl
systemctl enable kubelet && systemctl start kubelet
```


```bash
cat <<EOF >  /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system
```


```bash
docker info | grep -i cgroup
cat /etc/systemd/system/kubelet.service.d/10-kubeadm.conf
sed -i "s/cgroup-driver=systemd/cgroup-driver=cgroupfs/g" /etc/systemd/system/kubelet.service.d/10-kubeadm.conf
systemctl daemon-reload
systemctl restart kubelet
```

kubeadm by design does not install a networking solution for you, which means you have to install a third-party CNI-compliant networking solution yourself using kubectl apply.

vcell-node5		master
vcell-node6		worker
vcell-node7		worker
vcell-node8		worker

1) To initialize the master, first choose the pod network plugin you want and check if it requires any parameters to be passed to kubeadm while initializing the cluster. Pick one of the machines you previously installed kubeadm on, and run:

```bash
kubeadm init
```
