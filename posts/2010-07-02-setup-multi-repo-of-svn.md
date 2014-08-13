% Setup multiple subversion repositories on Mac

> 2014年从msn space存档中重新恢复出来！

It's easy, easy, easy until after u know how to do it, :0)

In fact, Mac has installed subversion for us， so when I know this after I had tried to install subversion by myself, I felt how stupid I am, hehe, Ok, let's go straight to our topic.

Firstly, we have to setup multiple repository directories, for example :


```bash
$ mkdir repositories
$ cd repositories
$ svnadmin create red_bear
$ svnadmin create blue_goat
```

After these commands' run, we should get a directory structure similar to:

<pre>
+---repositories
|   +---blue_goat
|   |   +---conf
|   |   +---dav
|   |   +---db
|   |   +---hooks
|   |   \---locks
|   \---red_bear
|       +---conf
|       +---dav
|       +---db
|       +---hooks
|       \---locks
</pre>

OK, as u know, we can start a svnserve deamon for any repository, for example:
`$ svnserve -d -r /home/svn/repositories/blue_goat`

or 

`$ svnserve -d -r /home/svn/repositories/red_bear`

BUT, u can't start both of them simultaneously,  except that we give svnserve another option parameter:


```bash
$ svnserve -d -r /home/svn/repositories/blue_goat (--listen-port 9110)
$ svnserve -d -r /home/svn/repositories/red_bear --listen-port 91101
```


With `--listen-port` option, we can start multiple repository deamon services simultaneously. 

So, It's easy, right? (Yeah, I Know u know, I just make a note here)
