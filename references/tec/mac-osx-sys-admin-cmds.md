% MAC OSX 系统管理命令汇总
% 王福强

# hardlink 

苹果的ln跟Linux/Unix下的ln分道扬镳了，不支持hard link， 要使用hard link，可以：

~~~~~~~ {.bash}
$ brew install hardlink-osx
$ hln .task ~/FuqiangWorks/task-warrior-storage-mirror  // hln source destination, 这里我们把task warrior的数据目录映射到备份目录
$ hln -u destination   // unlink
~~~~~~~

Ref: <https://github.com/selkhateeb/hardlink>



