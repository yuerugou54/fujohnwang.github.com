% 了解tmux
% FuqiangWang

tmux = terminal multiplexer

tmux就是一个允许我们创建多个命令行窗口的程序， 采用C/S结构， tmux server运行在后台， tmux client连接到tmux server创建多个session和window， pane等， 即使关掉tmux client的窗口， 创建的session相关进程将持续在后台的tmux server上执行，除非所有的session都关闭。

对于“键盘人”来说，尤其是vim或者emacs重度用户， tmux会让他们更爽； 即使对于其它客户，当管理服务器的时候，tmux也可以让命令行窗口更具“扩展性”。

# 基本命令
``` {.bash}
$ tmux  // 新建匿名session
$ tmux new -s [session name] // 新建命名session
$ tmux list-sessions
$ tmux detach // 脱离当前session
$ tmux attach -t [session name]  // 重新attach到某个session
$ tmux kill-session -t [session]  // 杀掉某个指定的session
$ tmux kill-server //杀掉后台所有session
```


Prefix快捷键默认是Control + b， 在Mac下，我们通过更改~/.tmux.conf，将其更改为Control + a:

<pre>
# improve colors
set -g default-terminal "screen-256color"

# act like GNU screen
unbind C-b
set -g prefix C-a
bind-key C-a send-prefix
</pre>

Prefix快捷键的使用是， 首先按Prefix快捷键组合，然后松开，再紧跟着按相应命令的快捷键， 比如， 新建窗口的话，我们先按Control + a, 然后再按c，则完成在当前session下创建窗口的工作， 其它窗口(Window)操作包括：

``` {.bash}
$ Ctrl + a, c   // 创建窗口
$ Ctrl + a, [0 - 9] // 输入0到9任何一个数字选择相应的窗口， 相当于命令tmux select-window 
$ Ctrl + a, %       // 分割split窗口为多个pane
$ Ctrl + a, ← | →   // 从窗口的pane跳到左边或者右边的pane
```



# 拷贝模式

`Ctrl + a, [`进入拷贝模式， 敲空格开始拷贝，回车结束拷贝， 敲`Ctrl + a, ]`退出拷贝模式。

# Say Goodbye to nohup &

在服务器上安装tmux之后， 本地ssh登陆上去执行tmux，然后随便创建Window或者Pane， 甚至detach或者直接关掉命令行， 运行的进程和session状态将持续在后台的tmux server上运行。


# Cheatsheet

<pre>
tmux -- terminal multiplexer

Managing tmux sessions:
$ tmux      # start tmux server
$ tmux at   # attach running sessions to a terminal
$ tmux ls   # list running tmux sessions
$ exit      # close tmux session

Sharing sessions between terminals:
$ tmux new -s session_name # make new named session
$ tmux at -t session_name  # attach to exist session (allowing shared sessions)
$ tmux kill-session -t session_name  # kill named session

Commands (used within a running tmux session):

NOTE: All commands need to be prefixed with the action key.
      By default, this is CTRL-b

 c  - create new window
n/p - move to next/previous window
0-9 - move to window number 0-9
 f  - find window by name
 w  - menu with all windows
 &  - kill current window
 ,  - rename window

 %  - split window, adding a vertical pane to the right
 "  - split window, adding an horizontal pane below
←/→ - move focus to left/right pane
↑/↓ - move focus to upper/lower pane

 !  - Break current pane into new window
 x  - Kill the current pane.
 d  - detach the current client

 [  - enter copy mode (then use emacs select/yank keys)
      * press CTRL-SPACE or CTRL-@ to start selecting text
      * move cursor to end of desired text
      * press ALT-w to copy selected text

 ]  - paste copied text

 ?  - show tmux key bindings
</pre>





