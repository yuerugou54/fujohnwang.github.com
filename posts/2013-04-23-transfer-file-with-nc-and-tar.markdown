% 使用nc或者结合tar进行文件传输
% 王福强

相对于scp来说， 使用nc进行文件传输牵扯步骤要繁琐一些，但可以换取可观的传输速度，对于大文件或者大量文件的传输来说，尤其可取！

# 单纯使用nc进行文件传输

1. 在目标机器上执行监听并将接受到的数据重定向到文件`nc -l 6969 > targetfile`
2. 在文件源端机器上使用nc连接目标机器并执行文件传输: `nc host 6969 < sourcefile`


# 结合tar使用

依然首先在目标机器上执行:

```bash
nc -l -p 6969 | tar xf – -C /tmp/
```

之后，在源端机器上执行：

```bash
tar cf – dir | nc -w1 hostB 6969
```

<blockquote>
If the name of the tarfile is '-', tar writes to the standard output or reads from the standard input, whichever is appropriate.
</blockquote>

<blockquote>
-C directory file	

Performs a chdir (see cd(1)) operation on directory and performs the c (create) or r (replace) operation on file . Use short relative path names for file . If file is `.', archive all files in directory. This option enables archiving files from multiple directories not related by a close common parent.
</blockquote>

# 参考资料
1. [Transferring large amount of data over the network: scp, tar | ssh, tar | nc compared](http://www.spikelab.org/transfer-largedata-scp-tarssh-tarnc-compared/)
2. [netcat file transfers](http://oreilly.com/pub/h/1058)
3. man NC(1)


