# Linux 命令

## 操作

- `eval` 命令用于重新运算求出参数的内容,eval可读取一连串的参数，然后再依参数本身的特性来执行。

## 网络

**查看端口**

-  `ss -lntpd | grep :22` 一般用于转储套接字统计信息。它还可以显示所有类型的套接字统计信息，包括 PACKET、TCP、UDP、DCCP、RAW、Unix 域等
-  `lsof -i tcp:22` 是一个列出系统上被进程打开的文件的相关信息。
- `fuser 22/tcp`  可以显示出当前哪个程序在使用磁盘上的某个文件、挂载点、甚至网络端口，并给出程序进程的详细信息。
- `ps -ef` 查看端口

## 文件

- `wget (url)` 下载文件

- `tar zxvf (程序.tgz)` 解压tgz文件

- `tar -zcvf` (程序.tar.gz) 压缩tgz文件

-  `cp (开始目录) (目的地)` 复制文件

  

## 系统

**查看系统内核**

- `uname -a`  查看linux系统内核信息
- `lsb_release -a` 查看Linux发行版本信息
- `df -lh` 查看文件系统磁盘使用情况
- `date` 查看系统时间 例如查看时区 `date +%z`

- `ps aux` 查看进程

**系统权限**

- `chmod` 修改文件权限 例如 `chmod +x (目录)` 