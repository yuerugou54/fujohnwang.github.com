% Git对非ASCII文件名的Octal Escape处理说明
% 王福强


在Mac上如果将文件命名为中文，当将这个文件`git add .`之后， 在`git st`中显示的文件名将类似于如下的形式：

<pre>
Wangs-MacBook-Pro:docworks fujohnwang$ git add .
Wangs-MacBook-Pro:docworks fujohnwang$ git st
# On branch master
# Changes to be committed:
#   (use "git reset HEAD <file>..." to unstage)
#
#	modified:   worksheets/daily_todo.md
#	new file:   "worksheets/\345\205\263\351\224\256\350\257\215\345\272\223\345\273\272\350\256\276\350\257\264\346\230\216.md"
</pre>

之前对这种现象一直熟视无睹，今天“良心发现”，就抠了一下， 反正Google了一堆文档，简单总结下来是这样的：  git会对Non-ASCII的文件名进行编码(如果本地编码是utf8, 那就用utf8进行编码)， 编码之后，将编码转变为octal escape形式显示，从而，就变成了上面所看到的那个样子。

引用一下别人的说法，或许信息量更大一些:
<pre>
You can git-add files with non-ASCII characters, but you can't sensibly work with
them as Git stores them as sequences of raw bytes in whatever codepage GetACP()
returns (cp1251 in your case). This is documented in git-show(1) for instance.

In general, this means you can only add non-ASCII filenames if you're sure no one
will ever clone your repo to a machine with different codepage/charset (on a Unix
box, for instance), as these filenames are recorded in a non-portable way.

Additional inconvenience is that some command-line Git tools (namely, git-log,
git-add and git-commit) show bytes above 0x7F in filenames using "octal escapes",
like this: "\346\356\357\340" (in other words, on displaying, filenames are
"normalized" to strict ASCII).

So, to sum this up:
1) Non-ASCII filenames are handled mostly OK, except for the output (`git-log
--name-status` shows octal-escaped names for instance).
2) The repository is non-portable.
</pre>

关于Octal Escape形式的Literal, Java里的定义是这样的：
<pre>
OctalEscape:
    \ OctalDigit
    \ OctalDigit OctalDigit
    \ ZeroToThree OctalDigit OctalDigit

OctalDigit: one of
    0 1 2 3 4 5 6 7

ZeroToThree: one of
    0 1 2 3
</pre>
简单来讲，就是范围是`\0-\377`, 换算到十进制是`0-255`。

不过Octal Escape算是C时代的遗老了，现在都加都倾向于utf8/unicode，比如<http://www.marshut.com/munzm/any-interest-in-keeping-octal-escape-literals.html>这里的倡议。

如果想在Octal Escape字面量与字符之间互转， 在Java里做可能还会有点儿小坑爹的， 除了要做2层转换(字面量到encoding的值，encoding的值到字符)， 还要注意char在java里的存储，怎么搞？ 感兴趣的话，看官自己去折腾折腾吧～

至于如何修正：

`git config --global core.quotepath false`

或者直接更改~/.gitconfig:

```
[core]
    quotepath = false
```


**参考链接**

1. [msysgit: Issue 230:	Question: How I can add files in national character](https://code.google.com/p/msysgit/issues/detail?id=230)
2. <http://stackoverflow.com/questions/9543026/why-do-java-octal-escapes-only-go-up-to-255>
3. <http://stackoverflow.com/questions/5581857/git-and-the-umlaut-problem-on-mac-os-x>
4. <http://stackoverflow.com/questions/4144417/how-to-handle-asian-characters-in-file-names-in-git-on-os-x>







