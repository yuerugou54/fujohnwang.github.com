% nmap手册
% 王福强

# 获取远程主机的系统类型及开放端口

> nmap -sS -P0 -sV -O <target>

这里的 < target > 可以是单一 IP, 或主机名，或域名，或子网

1. -sS TCP SYN 扫描 (又称半开放,或隐身扫描)
2. -P0 允许你关闭 ICMP pings.
3. -sV 打开系统版本检测
4. -O 尝试识别远程操作系统

其它选项:

-A 同时打开操作系统指纹和版本检测
-v 详细输出扫描情况.

> nmap -sS -P0 -A -v < target >


#  列出开放了指定端口的主机列表

> nmap -sT -p 80 -oG – 192.168.1.* | grep open

# 在网络寻找所有在线主机

> nmap -sP 192.168.0.*

或者也可用以下命令:

> nmap -sP 192.168.0.0/24

指定 subnet

# Ping 指定范围内的 IP 地址

> nmap -sP 192.168.1.100-254

# 在某段子网上查找未占用的 IP

> nmap -T4 -sP 192.168.2.0/24 && egrep “00:00:00:00:00:00″ /proc/net/arp

# 在局域网上扫找 Conficker 蠕虫病毒

> nmap -PN -T4 -p139,445 -n -v –script=smb-check-vulns –script-args safe=1 192.168.0.1-254


# 扫描网络上的恶意接入点 （rogue APs）.

> nmap -A -p1-85,113,443,8080-8100 -T4 –min-hostgroup 50 –max-rtt-timeout 2000 –initial-rtt-timeout 300 –max-retries 3 –host-timeout 20m –max-scan-delay 1000 -oA wapscan 10.0.0.0/8


# 使用诱饵扫描方法来扫描主机端口

> sudo nmap -sS 192.168.0.10 -D 192.168.0.2

#  为一个子网列出反向 DNS 记录

> nmap -R -sL 209.85.229.99/27 | awk ‘{if($3==”not”)print”(“$2″) no PTR”;else print$3″ is “$2}’ | grep ‘(‘

# 显示网络上共有多少台 Linux 及 Win 设备?

sudo nmap -F -O 192.168.0.1-255 | grep “Running: ” > /tmp/os; echo “$(cat /tmp/os | grep Linux | wc -l) Linux device(s)”; echo “$(cat /tmp/os | grep Windows | wc -l) Window(s) device”


# 扫描主机中所有的保留TCP端口
> nmap -v scanme.nmap.org

选项-v启用细节模式。

# SYN扫描

> nmap -sS -O scanme.nmap.org/24

进行秘密SYN扫描，对象为主机Saznme所在的“C类”网段 的255台主机。同时尝试确定每台工作主机的操作系统类型。因为进行SYN扫描 和操作系统检测，这个扫描需要有根权限。

# 其它

> nmap -sV -p 22，53，110，143，4564 198.116.0-255.1-127

进行主机列举和TCP扫描，对象为B类188.116网段中255个8位子网。这 个测试用于确定系统是否运行了sshd、DNS、imapd或4564端口。如果这些端口 打开，将使用版本检测来确定哪种应用在运行。

> nmap -v -iR 100000 -P0 -p 80

随机选择100000台主机扫描是否运行Web服务器(80端口)。由起始阶段 发送探测报文来确定主机是否工作非常浪费时间，而且只需探测主机的一个端口，因 此使用-P0禁止对主机列表。

> nmap -P0 -p80 -oX logs/pb-port80scan.xml -oG logs/pb-port80scan.gnmap 216.163.128.20/20

扫描4096个IP地址，查找Web服务器(不ping)，将结果以Grep和XML格式保存。

> host -l company.com | cut -d -f 4 | nmap -v -iL -

进行DNS区域传输，以发现company.com中的主机，然后将IP地址提供给 Nmap。上述命令用于GNU/Linux -- 其它系统进行区域传输时有不同的命令。






# 参考资料

1. [十条nmap常用的扫描命令](http://www.91ri.org/891.html)
2. <http://nmap.org/man/zh/man-examples.html>