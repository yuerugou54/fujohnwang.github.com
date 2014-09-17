% 了解docker

Docker = versioned lightweight provisioning

# Docker Architecture

server : docker daemon

client : docker cli

lxc

virtualization vs. containerization

# 安装

## ubuntu13.10下安装说明

ubuntu : 13.10

docker : 0.9.0

官方安装说明文档见<http://docs.docker.io/en/latest/installation/ubuntulinux/>

### local dns resovle的问题
参照官方安装说明文档中"Docker and local DNS server warnings"部分的说明， 将`/etc/default/docker`配置文件中`DOCKER_OPTS="-dns 8.8.8.8"`配置注释打开，并重启docker即可(如果没有，则添加此行)。


### [error] client.go:2315 Error getting size: bad file descriptor问题
ubuntu13.10, 按照官方文档进行(<http://docs.docker.io/en/latest/installation/ubuntulinux/>)0.9安装之后，如果运行`sudo docker run -i -t ubuntu /bin/bash`, 会出现"[error] client.go:2315 Error getting size: bad file descriptor"这样的信息，只要使用<https://raw.github.com/dotcloud/docker/master/contrib/init/upstart/docker.conf>的配置文件替换`/etc/init/docker.conf`, 重新启动docker即可(`sudo service docker restart`).

### WARNING: No swap limit support
安装完成后，通过`docker info`查看信息的时候，会报类似"WARNING: No swap limit support"的警告，参考官方文档中memory swap accounting部分：

1. 编辑/etc/default/grub， 添加`GRUB_CMDLINE_LINUX_DEFAULT="cgroup_enable=memory swapaccount=1"`
2. 然后`sudo grub-update && sudo reboot`

## 使用非root用户使用docker(不用每次sudo)
<pre>
$ sudo groupadd docker
$ sudo gpasswd -a ${USER} docker  # 当前用户添加到docker group
$ sudo reboot  # 切记，是reboot，而不是像官方文档那样restart， 否则，用户即使在第二步添加成功，但使用`groups`命令查看用户会显示还是没有被添加
</pre>

## 升级
<pre>
$ sudo apt-get update
$ sudo apt-get install lxc-docker
</pre>

---



# 使用
## docker daemon
默认情况下， docker daemon是绑定到unix socket的，这也就意味着只能通过本机root访问； 但是如果我们通过-H命令行参数启动docker daemon的话，就可以在任何安装了docker client的机器上控制docker daemon，所以， 一般情况下，不建议以public IP的形式对外暴露docker daemon！

> 注意与`docker run`命令的-p参数的功能相区分， 后者是启动的container所属的网络属性， 而`docker -H`则是docker daemon的对外服务的网络设置！


## RUN

### 交互形式运行
使用-i参数

### 指定端口运行
HOST跟container的网络设置之间的关系跟router和局域网中的机器类似(非确切说法，实际上是通过docker0这个linux networking bridge进行网络功能的管理)， 如果要让外部的机器能够访问hosted container， 需要做[port redirection](http://docs.docker.io/en/latest/use/port_redirection/)。

使用-p参数:

`$> docker run –p 49200:8080 gcm http://localhost:49200`

http://localhost:49200， 8080是private port， 49200是public port；

或者只指定private port，由docker决定最终的public port是哪个:
<pre>
JOB=$(sudo docker run -d -p 4444 ubuntu:12.10 /bin/nc -l 4444)         //Bind port 4444 of this container, and tell netcat to listen on it

PORT=$(sudo docker port $JOB 4444 | awk -F: '{ print $2 }')            // Which public port is NATed to my container?

echo hello world | nc 127.0.0.1 $PORT                                  // Connect to the public port

echo "Daemon received: $(sudo docker logs $JOB)"                       // Verify that the network connection worked
</pre>

又或者:
<pre>
// Bind to a dynamically allocated port
$ docker run -p 127.0.0.1::8080 --name dyn-bound <image> <cmd>

// Lookup the actual port
$ docker port dyn-bound 8080
// 127.0.0.1:49160
</pre>


### 以deamon形式运行
使用-d参数：

`$> docker run –d –p 127.0.0.1::8080 <image>`

## 查看运行状态
使用`docker ps`命令。

`docker ps -a`查看所有已经停止的containers;

`docker ps -l`查看上一个停止的container;

## attach到运行的container
<pre>
CONTAINER_ID=$(sudo docker run -d ubuntu /bin/sh -c "while true; do echo hello world; sleep 1; done")
sudo docker logs $CONTAINER_ID                           // “docker logs” This will return the logs for a container
sudo docker attach -sig-proxy=false $CONTAINER_ID        // “-sig-proxy=false” Do not forward signals to the container; allows us to exit the attachment using Control-C without stopping the container.
</pre>


# Private Registry
fast way to install/setup a private registry:

~~~~~~~ {.bash}
$ docker run –p 5000 samalba/docker-registry
~~~~~~~

then use it: 

~~~~~~~ {.bash}
$ docker push <namespace>/<name>
~~~~~~~

Docker uses the namespace to know where to push, if the namespace is an url, it will push on this url:

~~~~~~~ {.bash}
#push the <name> to your a private registry <url>
$ docker push <url>/<name>
~~~~~~~



# 系统细节
## docker元数据存放位置

<pre>
The images are stored in /var/lib/docker/graph/<id>/layer.

Note that images are just diffs from the parent image. The parent ID is stored with the image's metadata /var/lib/docker/graph/<id>/json.

When you docker run an image. AUFS will 'merge' all layers into one usable file system.
</pre>

默认路径是`/var/lib/docker`， 如果要定制， 则修改"/etc/default/docker"配置文件， 增加如下配置项内容: `DOCKER_OPTS="-g /path/to/docker/data"`, 即"use the -g option when starting the docker daemon to specify where you want /var/lib/docker to live"

## 使用Data Volumes

不想将应用数据存放和docker的各种元数据/系统数据混在一起？ “快使用Data Volume， 哼哼哈嘿”

可以为container制定多个data volumes: `$ docker run -v /var/volume1 -v /var/volume2 busybox true`， 或者使用Dockerfile指定：
<pre>
// BUILD-USING:        docker build -t data .
// RUN-USING:          docker run -name DATA data
FROM          busybox
VOLUME        ["/var/volume1", "/var/volume2"]
CMD           ["/bin/true"]
</pre>

我们可以使用“--volumes-from”来指定新的container可以使用之前container指定的data volumes: `$ docker run -t -i -rm -volumes-from DATA -name client1 ubuntu bash` （-rm - remove the container when it exits）

只要有container还在引用data volumes，即使rm了相应的container， data volumes将持续保存直到没有任何一个container再引用它。




# 参考
1. [Advanced Docker Volumes](http://crosbymichael.com/advanced-docker-volumes.html)
2. [Where are Docker images stored?](http://blog.thoward37.me/articles/where-are-docker-images-stored/)  *****
3. [How it works: Docker](http://traveling-railsman.com/blog/how-it-works-docker)
4. [BUILDING A DOCKER-BASED MYSQL SERVER](http://ijonas.com/devops-2/building-a-docker-based-mysql-server/)
5. [What is Containerization?](http://mutteringsontech.com/post/what-is-containerization)
6. [Panamax: Docker Management for Humans](http://panamax.io/)









