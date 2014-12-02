% 我的Git参考手册
% FuqiangWang



# 舍弃所有本地变更， 回滚到最后提交状态

~~~~~~~ {.bash}
git commit -a -m "Saving my work, just in case"
git branch my-saved-work

git fetch origin
git reset --hard origin/master
~~~~~~~

