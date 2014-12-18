% 我的Git参考手册
% FuqiangWang



# 舍弃所有本地变更， 回滚到最后提交状态

~~~~~~~ {.bash}
git commit -a -m "Saving my work, just in case"
git branch my-saved-work

git fetch origin
git reset --hard origin/master
~~~~~~~



# 打包指定版本的文件

~~~~~~~ {.bash}
git archive -o docs.zip v2.2.0 'Documentation/*.html'
~~~~~~~

git版本2.2.0


# Remote Branch

<http://git-scm.com/book/en/v2/Git-Branching-Remote-Branches>




