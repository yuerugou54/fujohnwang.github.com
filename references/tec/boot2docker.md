% Docker with boot2Docker on Mac
% 陨石 - yunshi AT wacai DOTA com
% 2015-04-23

# 准备与运行

0. 先装[virtualbox](https://www.virtualbox.org/wiki/Downloads), boot2docker需要它！
1. [download](https://github.com/boot2docker/osx-installer/releases) and install
	- 更简单的做法：`brew update` & `brew install boot2docker`
2. `boot2docker init` to download vm iso and create it in virtual box;

	~~~
	➜  ~  boot2docker init
Latest release for boot2docker/boot2docker is v1.6.0
Downloading boot2docker ISO image...
	~~~

3.  `boot2docker start`
	- 返回信息中会提示你设置环境变量，不用手动设置， 直接执行第4步的命令即可。
4.  `boot2docker shellinit` - 设置相关环境变量
5.  





# 常用命令说明

## 升级boot2docker
下载最新的boot2docker安装包并安装即可。

## 升级docker
运行`boot2docker download`

## 下载最新的boot2docker iso
`boot2docker download`

## 删除虚拟机景象
`boot2docker delete`


# Gotchas

哥黑苹果， BIOS有的项目禁止了， 所以up不起来，fuck~


# References
1.  <http://docs.docker.com/installation/mac/>
2. [Docker —— 从入门到实践](https://www.gitbook.com/book/yeasy/docker_practice/details)
	- <http://yeasy.gitbooks.io/docker_practice/content/dockerfile/basic_structure.html> - Dockerfile基础结构
3. [A production ready Docker workflow](http://www.luiselizondo.net/a-production-ready-docker-workflow/)










