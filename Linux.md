# Linux 笔记

## Linux 基础篇

### 网络连接的三种方法

- 桥接模式，虚拟系统可以和外部系统进行通讯，但是容易造成IP冲突
- NAT模式，网络地址转换模式，虚拟系统可以和外部系统进行通讯，不会造成IP冲突
- 主机模式，独立的系统，不与外部发生联系

### 虚拟机克隆

- 方式一 
  - 直接拷贝一份安装好的虚拟机文件
- 方式二
  - 使用VMware的克隆操作
  - 克隆时需要先关闭Linux系统

### 虚拟机快照

- 利用VMware快照管理功能，可以快速将系统恢复到之前正常运行的状态

### 虚拟机的迁移和删除

- 在VMware安装的虚拟机的本质是文件，想要对虚拟机进行迁移或者删除操作，只需要对相对应的虚拟机文件进行操作

### 安装VMtools

- 安装VMtools可以在Windows下更好的管理VM虚拟机
- 可以设置Windows和虚拟机的共享文件夹
- 安装 VMtools 的步骤
  -  进入centos
  - 弹出镜像光驱
  - 设置虚拟机CD/DVD(SATA) 使用iso镜像文件
    - 文件指向vmware安装目录中的 **linux.iso**
  - 点击 vm 菜单的 install vmware tools
  - centos 会出现一个 vm 的安装包，xx.tar.gz
  - 拷贝到 /opt 
  - 使用解压命令 tar ，得到一个安装文件
    - tar -zxvf xx.tar.gz
  - 进入该 vm 解压的目录，/opt 目录下
  - 安装 ./vmware-install.pl
  - 全部使用默认设置即可安装成功
    - 出现Would you like to recover the wasted disk space by uninstalling VMware Tools at
      this time? (yes/no) [yes]  这条日志时需要输入 **no**
  - 注意
    - 安装 vmtools 需要有 gcc
      - gcc -v 查看 gcc 

### 使用 vmtools 设置共享文件夹

- 步骤

  - 打开虚拟机设置中的选项，设置共享文件的路径和总是启用即可

  - centos 下共享文件夹路径为 /mnt/hgfs

- 注意事项和细节说明
  - 在实际开发中，windows 和 linux 不在同一台机器上，需要通过远程方式实现文件的上传下载

### Linux 目录结构

- linux 的文件系统采用层级式树状目录结构，在此结构的最上层是根目录 **/** ，然后在此目录下创建其他目录
- 在linux中，一切皆文件
  - 在linux中，硬件会被映射为一个个的文件进行管理
- 具体的目录结构
  - /bin ( /usr/bin，/usr/local/bin )
    - 是 Binary 的缩写，这个目录存放着最经常常用的命令
  - /sbin ( /usr/sbin，/usr/local/sbin )
    - s 表示 super user 的意思，这里存放的是系统管理员使用的系统管理程序
  - /home
    - 存放普通用户的主目录，在 Linux 中，每个用户都会有一个自己的主目录，一般该目录名是以用户的账号命名
  - /root
    - 该目录为系统管理员，也称作超级权限者的用户主目录
  - /lib
    - 系统开机所需要的最基本的动态链接共享库，其作用类似 Windows 中的 DLL 文件，几乎所有的应用程序都需要用到这些共享库
  - /lost+found 
    - 这个目录一般是空的，当系统非法关机后，这个目录中就出现了一些文件
  - /etc
    - 所有的系统管理所需要的配置文件和子目录，my.conf
  - /usr
    - 一个非常重要的目录，用户的很多应用程序和文件都放在这个目录下，类似于 windows 下的 program files 目录
  - /boot
    - 存放的是使用 linux 时，使用的一些核心文件，包括一些连接文件和镜像文件
  - /proc **不能动**
    - 这个目录是一个虚拟的目录，它是系统内存的映射，访问这个目录来获取系统信息
  - /srv **不能动**
    - service 的缩写，该目录存放一些服务启动之后需要提取的数据
  - /sys **不能动**
    - 这是 linux 2.6 内核的一个很大的变化，该目录下安装了 2.6 内核中新出现的一个文件系统 sysfs 
  - /tmp
    - 临时文件目录
  - /dev
    - 类似于 windows 的设备管理器，把所有的硬件用文件的形式存储
  - /media
    - linux 系统会自动识别一些设备，例如 U盘 光驱等等，当识别后，linux 会把识别的设备挂载到这个目录下
  - /mnt
    - 系统提供该目录是为了让用户临时挂载别的文件系统的，我们可以将外部存储挂载到 /mnt 上，然后进入该目录就可以查看里面的内容了
    - 例如上面的共享文件夹 **/mnt/hgfs**
  - /opt
    - 这是给主机额外安装软件存放的目录，如安装 oracle 数据库就可以存放到该目录下，默认为空
  - /usr/local
    - 这是另一个给主机额外安装软件所存放的目录，一般是通过源码方式安装的程序
  - /var
    - 这个目录存放着不断扩充着的东西，习惯将经常被修改的目录放在这个目录下，包括各种日志文件
  - /selinux [ security-enhanced linux ]
    - SELinux 是一种安全子系统，它能控制程序只能访问特定文件，有三种工作模式，可以自行设置
    - 默认关闭，使用需手动打开

## Linux 实操篇

### 远程登录 Linux 服务器

- Xshell 用于远程登录 Linux 服务器
- 操作步骤
  - Xshell 连接 Linux 服务器
    - 需要知道 Linux 服务器的 IP 地址，使用 ifconfig 命令查看
    - 然后在 Windows 上使用 ping <ip> 的方式测试是否能够正常连接 Linux 服务器
    - 根据 Xshell 软件提示新建连接
      - 名称随便设置
      - 主机为 Linux 的 IP 地址
      - 端口号为 22

### 远程文件传输

- Xftp 用于远程的文件上传与下载
- 操作步骤
  - 与 Xshell 的使用相似
  - 在新建连接时，选择协议为 SFTP，端口号为 22
  - 如何处理中文乱码
    - 修改连接 属性-编码 为 **utf-8**

### Vim 编辑器

#### Vim 三种模式及切换

- 正常模式
  - 进入 vim 的默认模式
  - 可以移动光标，可以删除字符或者删除整行，可以使用复制、粘贴来修改文档内容
- 插入模式
  - 在正常模式下，按 i、o、a、r 进入插入模式，也就是编辑模式
  - 按 Esc 键退出插入模式
- 命令行模式
  - 在正常模式下，输入 **:** 进入命令行模式
  - 可以输入指令来完成读取、存盘、替换、离开 vim、显示行号等操作

#### Vim 快捷键

- 复制当前行，在一般模式下输入  **yy**
- 复制当前向下五行，在一般模式下输入 **5yy**
- 粘贴，在一般模式下输入  **p**
- 删除当前行，在一般模式下输入  **dd**
- 文件中查找某个单词 ，在一般模式下输入 **/** 进入命令行模式，然后在 **/** 后面输入要查找的单词，例如 **/hello**
- 设置行号  **:set nu**
- 取消行号  **:set nonu**
- 定位文件最末行，在一般模式下输入  **G**
- 定位文件首行，在一般模式下输入  **gg**
- 撤销，在一般模式下输入  **u**
- 光标跳至指定行号 ，在一般模式下输入，输入 **20** ，再输入 **shift+g** 

### 开机、重启、用户的登录注销

#### 关机、重启命令

- 立刻关机  **shutdown -h now**  等同于  **halt**
- 一分钟后关机  **shutdown -h 1**  输入 **shutdown** 不加任何参数，默认即为前面的命令，即一分钟后关机
- 立即重启计算机  **shutdown -r now**  等同于 **reboot**
- 把内存的数据同步到硬盘  **sync**
- tips: 建议在关机或者重启前，先运行 **sync**，把内存中的数据写入磁盘

#### 用户登录和注销

- 切换用户
  - **su root** 
- 注销
  - **exit**
  - **logout** 
    - 使用 **su - root** 切换用户时，才可以用 **logout** 注销

### 用户管理

#### 基本介绍

- Linux 系统是一个多用户，多任务的操作系统，任何一个要使用系统资源的用户，都必须首先向系统管理员申请一个账号，然后以这个账号的身份进入系统

#### 添加用户

- 基本语法
  - **useradd** **<用户名>**
- 应用案例
  - 添加一个用户 **tom**，默认该用户的家目录在 **/home/tom**
- 细节说明
  - 当创建用户成功后，会自动创建和用户同名的家目录
  - 可以通过 **useradd -d <指定目录> <新的用户名>**，给新创建的用户指定家目录

#### 指定、修改密码

- 基本语法
  - **passwd <用户名>**
- tips：**pwd** 显示当前所在目录

#### 删除用户

- 基本语法
  - **userdel <用户名>**
    - 删除用户，保留家目录
  - **userdel -r <用户名>**
    - 删除用户以及该用户家目录

- tips：一般情况下，建议保留家目录

#### 查询用户信息

- **id <用户名>**

#### 切换用户

- **su - <用户名>**

#### 查看当前登录的用户信息

- **who am i**

#### 用户组

- 介绍
  - 类似于角色，系统可以对有共性/权限的多个用户进行统一的管理
- 新增组
  - **groupadd <组名>**
- 删除组
  - **groupdel <组名>**
- 创建用户时指定组
  - **useradd -g <用户组> <用户名>**
- 修改用户的组
  - **usermod -g <用户组> <用户名>** 

#### 用户和组相关的文件

- **/etc/passwd** 文件
  - 用户（user）的配置文件，记录用户的各种信息
  - 每行的含义
    - 用户名：口令：用户标识符：组标识符：注释性描述：主目录：登录 shell
- **/etc/shadow** 文件
  - 口令的配置文件
  - 每行的含义
    - 登录名：加密口令：最后一次修改时间：最小间隔时间：最大间隔时间：警告时间：不活动时间：失效时间：标志
- **/etc/group** 文件
  - 组（group）配置文件，记录 linux 包含组的信息
  - 每行的含义
    - 组名：口令：组标识号：组内用户列表

### 实用指令

#### 指定运行级别

- 基本介绍
  - 0  关机
  - 1  单用户（找回丢失密码）
  - 2  多用户状态没有网络服务
  - 3  多用户状态有网络服务
  - 4  系统未使用，保留给用户定义
  - 5  图形界面
  - 6  系统重启
- 使用语法
  - **init <运行级别 [ 0、1、2、3、4、5、6 ] >**
  - 例如 **init 6** 系统重启
- 指定默认运行级别（Centos7 之后）
  - **/etc/inittab** 文件中
    - **multi-user.target**：analogous to runlevel 3
    - **graphical.target**：analogous to runlevel 5
    - To view current default target，run：
      - **systemctl get-default**
    - To set a default target，run：
      - **systemctl set-default <Target>.target**

#### 找回 root 密码

centos7之后的方法

1. 首先，启动系统，进入开机界面，在选择系统的界面中按 **e**，进入编辑模式
2. 进入编辑模式后，找到以 **Linux16** 开头内容所在行，在行的末尾添加 **init=/bin/sh**
3. 输入完成后，按快捷键 **Ctrl + x** 进入单用户模式
4. 然后输入 **mount -o remount，rw /** ，按 Enter 键
5. 接着输入 **passwd**，设置密码，然后确认密码，修改成功后会显示 passwd......的样式
6. 继续输入 **touch /.autorelabel**，按 Enter 键
7. 再输入 **exec /sbin/init**，按 Enter 键
8. 等待系统自动修改密码，这个过程时间可能有点长，耐心等待，完成后，系统会自动重启

#### 帮助指令

- man 获得帮助信息
  - 基本语法
    - **man <命令或配置文件>**
    - 例如  **man ls**
    - 在linux下，隐藏文件是以 **.** 开头
- help 指令
  - 基本语法
    - **help <命令>**
  - 功能描述
    - 获得 shell 内置命令的帮助信息
    - 例如 **help cd**

#### 文件目录指令

- **pwd**
  - 显示当前工作目录的绝对路径
- **ls**
  - 显示当前目录中的文件和子目录
- **mkdir**
  - 创建目录
  - 常用选项
    - **-p**
      - 创建多级目录
- **rmdir**
  - 删除空目录
- **rm -rf**
  - 如果要删除非空目录，使用这个命令
- **touch**
  - 创建空文件，touch hello.txt
- **cp**
  - 拷贝文件到指定目录
  - 基本语法
    - **cp [ 选项 ] source dest**
  - 常用选项
    - **-r** 递归复制整个文件夹
    - **\cp -r** 强制覆盖整个文件夹，不提示
- **rm**
  - 删除文件或者目录
  - 基本语法
    - **rm [ 选项 ] 要删除的文件或者目录**
  - 常用选项
    - **-r** 递归删除整个文件夹
    - **-f** 强制删除不提示
- **mv**
  - 移动文件与目录或者重命名
  - 基本语法
    - mv oldNameFile newNameFile  // 重命名
    - mv /temp/movefile /targetFolder  // 移动文件
    - mv /home/tom /root  // 移动目录，不需要加参数
- **cat**
  - 查看文件内容
  - 基本语法
    - **cat [ 选项 ] 要查看的文件**
  - 常用选项
    - **-n** 显示行号
  - 可以与管道命令组合使用
    - **cat -n /etc/profile | more**
- **more** 
  - more 指令是一个基于 vi 编辑器的文本过滤器，它以全屏幕的方式按页显示文本文件的内容
  - more指令中内置了若干快捷键（交互的指令）
  - 基本语法
    - **more** 要查看的文件
  - 操作说明
    - **空格**  代表向下翻一页
    - **Enter**  代表向下翻一行
    - **q**  表示退出
    - **Ctrl + F**  向下滚动一屏
    - **Ctrl + B**  返回上一屏
    - **=**  输出当前行的行号
    - **:f**  输出当前文件名和当前行的行号
- **less**
  - less 指令用于分屏查看文件内容，它的功能与 more 指令类似，但是比 more 更加强大，支持各种显示终端。
  - less 指令在显示文件内容的时候，并不是一次性将整个文件都加载完才显示的，而是根据显示需要加载内容，对于显示大型文件具有较高的效率
  - 基本语法
    - **less** 要查看的文件
  - 操作说明
    - 空格键  向下翻一页
    - pagedown键  向下翻一页
    - pageup键  向上翻一页
    - **/** 字串，向下搜寻字串，**n** 向下查找，**N** 向上查找
    - **？** 字串，向上搜寻字串，**n** 向上查找，**N** 向下查找
    - **q** 离开 less
- **echo**
  - echo 输出内容到控制台
  - 基本语法
    - echo [ 选项 ] [ 输出内容 ]
  - 应用实例
    - echo $PATH
    - echo $HOSTNAME
    - echo "hello，world!"
- **head**
  - 显示文件开头的部分
  - **head 文件**   默认显示文件前十行内容
  - **head -n 5**    显示文件的前五行内容
- **tail**
  - 用于显示文件结尾部分的内容
  - 默认显示文件后十行的内容
  - 基本语法
    - **tail 文件**  查看文件后十行的内容
    - **tail -n x 文件** 查看后x行的内容
    - **tail -f 文件**  实时追踪该文档的所有更新
- 输出重定向
  - **>** 覆盖
  - **>>** 追加
- **ln**
  - 软链接，又称为符号链接，类似于 windows 中的快捷方式，主要存放了链接其他文件的路径
  - 基本语法
    - **ln -s [ 原文件或目录 ]  [ 软链接名 ]**  给原文件创建一个软链接
  - 应用实例
    - **ln -s /root/ /home/myroot**
- **history**
  - 查看已经执行过的历史命令，也可以执行历史命令
  - 基本语法
    - history
  - 显示所有历史命令  **history**
  - 显示最近使用过的10个指令  **history 10**
  - 执行历史编号为5的指令  **!5**

#### 时间日期指令

- **date** 指令，显示当前日期
  - 基本语法
    1. **date** 显示当前时间
    2. **date + %Y**  显示当前年份
    3. **date + %m**  显示当前月份
    4. **date + %d**  显示当前是哪一天
    5. **date  "+%Y-%m-%d %H:%M:%S"**   显示年月日时分秒
    6. **date -s "2011-11-11 11:11:11"**  设置系统当前时间
- **cal** 指令，查看日历
  - 基本语法
    - **cal [ 选项 ]**  不加选项，显示本月日历
    - **cal 2020**  显示2020年日历

#### 搜索查找指令

- **find** 指令
  - find 指令将从指定目录向下递归遍历其各个子目录，将满足条件的文件或者目录显示到终端
  - 基本语法
    - **find  [ 搜索范围 ]  [ 选项 ]**
  - 选项说明
    - **-name <文件名>**   按照指定的文件名查找文件
    - **-user <用户名>**  查找属于指定用户的所有文件
    - **-size <文件大小>**  按照指定的文件大小查找文件
  - 应用实例
    - **find /home -name hello.txt**
    - **find /opt -user nobody**
    - **find / -size +200M**  // 查找系统中大于200M的文件
      - tips：+n 表示大于 n，-n 表示小于 n，n 表示等于 n，n 的单位有 k，M，G
- **locate** 指令，可以快速定位文件路径
  - locate 指令利用事先建立的系统中所有文件名称及路径的locate数据库实现快速定位给定的文件。
  - locate 指令无需遍历整个文件系统，查询速度较快，为了保证查询结果的准确性，管理员必须定期更新locate时刻
  - 基本语法
    - **locate 搜索文件**
    - 由于locate指令基于数据库进行查询，所以第一次运行前，必须使用 **updatedb** 指令创建locate数据库
  - **locate hello.txt**
- **which** 指令，可以查看哪个指令在哪个目录下
  - **which ls**
- **grep** 指令，和管道符号 **|** 
  - grep 过滤查找，管道符号 **|** 表示将前一个命令的处理结果输出传递给后面的命令处理
  - 基本语法
    - **grep [ 选项 ] 查找内容 源文件**

  - 常用选项
    - -n 显示匹配行及行号
    - -i 忽略字母大小写
  - 在 hello.txt 文件中，查找 **yes** 所在行及行号
    - 写法1：**cat /home/hello.txt | grep -n "yes"** 
    - 写法2：**grep -n "yes" /home/hello.txt**


#### 压缩和解压指令

- **gzip** 和 **gunzip** 指令
  - gzip 用于压缩文件，将文件压缩为 xxx.gz 格式
    - **gzip hello.txt**
  - gunzip 用于解压文件，将 xxx.gz 文件解压缩还原
    - **gunzip hello.txt.gz**
- **zip** 和 **unzip**
  - 基本语法
    - **zip [ 选项 ] xxx.zip <将要压缩的文件或目录>**
    - **unzip [ 选项 ] xxx.zip**
  - 选项说明
    - **-r** 表示递归压缩，即压缩目录
    - **-d <目录>** 表示解压至指定目录
  - 应用实例
    - 将 /home 下的所有文件和文件夹进行压缩成 myhome.zip
      - **zip -r /home/myhome.zip /home/**
    - 将 myhome.zip 解压到 /opt/tmp 文件夹中
      - **mkdir /opt/tmp**
      - **unzip -d /opt/tmp/ myhome.zip**
- **tar** 指令
  - tar 是打包指令，打包后的文件是  xxx.tar.gz
  - 基本语法
    - **tar [ 选项 ] xxx.tar.gz <打包的内容>**
  - 选项说明
    - **-c**  产生 xxx.tar 打包文件
    - **-v**  显示详细信息
    - **-f**   指定压缩后的文件名
    - **-z**  打包同时压缩
    - **-x**  解包 xxx.tar 文件
  - 应用实例
    - 将 /home/zwj 打包成 zwj.tar.gz
      - **tar -zcvf zwj.tar.gz /home/zwj**
    - 将 zwj.tar.gz 解包，到 /home 目录下
      - **tar -zxvf zwj.tar.gz /home** 

### Linux 组

- 基本介绍
  - 在 linux 中，每个用户必须属于一个组，不能独立于组外，在 linux 中，每个文件都有所有者、所在组、其他组的概念
- 文件/目录 所有者
  - 一般为文件的创建者
  - 查看文件的所有者
    - **ls -la**
  - 修改文件的所有者
    - **chown <用户名> <文件名>**
  - 应用案例
    - 使用 root 创建一个文件，然后将文件的所有者改为其他用户
    - **root: touch apple.txt**
    - **jerry: chown jerry apple.txt**
- 组的创建
  - **groupadd** 组名
  - 应用实例
    - 创建 monster 组
      - **groupadd monster**
    - 创建一个用户 fox，并放入 monster组
      - useradd -g monster fox
- 修改文件所在组
  - **chgrp 组名 文件名**
  - **chgrp jerry hello.txt**  


### 权限

#### 基本介绍

- -rw-r--r--. 1 root  jerry    43 8月  19 20:42 hello.txt
- 0-9 位说明
  - 第0位确定文件类型
    - **l** 是链接，相当于快捷方式
    - **d** 是目录
    - **c** 是字符设备文件，鼠标键盘等等
    - **b** 是块设备，比如硬盘
    - **- **表示普通文件
  - 第1-3位确定文件所有者对文件的权限  ---User
  - 第4-6位确定所属组对文件的权限  ---Group
  - 第7-9位确定非同组用户对文件的权限  ---Other
- 权限后面的 **1** 
  - 如果是文件，则表示一个文件
  - 如果是目录，则表示 子目录数 + 文件数
- root 所属用户
- jerry 所属组
- 43 文件大小
- 8月  19 20:42 最后修改日期时间

#### **rwx** 权限详解

- r read  可读
- w write  可写
- x execute  可执行
- rwx 作用到文件
  - r 表示可以读取文件内容
  - w 表示可以修改文件内容
  - x 表示文件可以被执行
- rwx 作用到目录
  - r 表示可以查看目录中有哪些文件
  - w 表示可以在目录中创建文件、删除文件、重命名该目录
  - x 表示可以进入该目录  cd /dir

#### 修改权限

- 通过 **chmod** 命令，可以修改文件或目录的权限
- 第一种方式
  - **+ 、- 、=** 变更权限
  - u user 表示所有者
  - g group 表示所有组
  - o other 表示其他人
  - a all 表示所有人
  - **chmod u=rwx,g=rx,o=x <文件名或目录>**
  - **chmod o+x <文件名或目录>**
  - **chmod a+x <文件名或目录>**
- 第二种方式，通过数字变更权限
  - r = 4，w = 2，x = 1，可以任意组合，比如 r+w+x = 7
  - **chmod u=rwx,g=rx,o=x <文件目录名>** 等同于 **chmod 751 <文件目录名>**

#### 修改文件所有者

- **chown newowner <文件/目录>**  // 改变文件所有者
- **chown newowner:newgroup <文件/目录>**  // 改变文件所有者和所在组
- **-R** 如果是目录，则使其下所有子文件、子目录递归生效

#### 修改文件所在组

- **chgrp newgroup <文件/目录>** 
- **-R** 如果是目录，则使其下所有子文件、子目录递归生效

### 任务调度   crond

- crontab 进行定时任务的设置
- 概述
  - 任务调度：是指系统在某个时间执行的特定的命令或程序
  - 任务调度分类：
    1. 系统工作：有些重要的工作必须周而复始的执行，如病毒扫描等
    2. 个别用户工作：个别用户有可能希望执行某些程序，比如对 mysql 数据库的备份
- 基本语法
  - **crontab [ 选项 ]**
- 常用选项
  - **-e** 编辑 crontab 定时任务
  - **-l** 查询 crontab 任务
  - **-r** 删除当前用户所有的 crontab 任务
- 快速入门
  - 设置任务调度文件：/etc/crontab
  - 设置个人调度任务，执行 **crontab -e** 命令
  - 接着输入任务到调度任务，如 ***/1 * * * * date > /tmp/hello.txt** ，意思是每分钟执行一次
    - ps：每个 * 号之间都有一个空格
  - 参数说明
    - 五个占位符
      - 第一个 ***** 一小时当中的第几分钟 
        - 范围 0-59
      - 第二个 * 一天当中的第几个小时
        - 范围 0-23
      - 第三个 * 一个月当中的第几天
        - 范围 1-31
      - 第四个 * 一年当中的第几月
        - 范围 1-12
      - 第五个 * 一周当中的星期几
        - 范围 0-7   0和7都表示周日
- 特殊符号的说明
  - *****   代表任何时间，比如第一个 * 号就代表一小时中每分钟都执行一次的意思
  - **，** 代表不连续的时间，比如 " 0 8,12,16 * * * 命令"，代表在每天的 8点0分，12点0分，16点0分，都执行一次命令
  - **-**    代表连续的时间范围，比如 " 0 5 * * 1-6 命令 " ，代表在周一到周六的凌晨 5点0分 执行命令
  - ***/n** 代表每隔多久执行一次，比如 " */10 * * * * 命令 "，代表每隔十分钟执行一次命令
- 注意：星期几和几号最好不要同时出现，因为他们定义的都是天，非常容易让管理员混乱
- 应用实例
  - 案例1：每隔一分钟，将当前的日期信息追加到 /tmp/mydate 文件中
    - */1 * * * * date >> /tmp/mydate
  - 案例2：每隔一分钟，将当前日期和日历都追加到 /home/mycal 文件中
    - 步骤
      1. 编写脚本 time.sh
      2. 给脚本文件增加执行权限
      3. crontab -e  ，增加 */1 * * * * bash /home/time.sh
  - 案例3：每天凌晨2点 将 mysql 数据库 testdb，备份到文件中。
    - 备份数据库指令为：mysqldump -u root -p <密码> <数据库> > /home/db.bak
    - 步骤
      1. crontab -e
      2. 0 2 * * * mysqldump -u root -p root testdb > /home/db.bak
- **crond** 相关指令
  - crontab -r ：终止任务调度
  - crontab -l ：列出当前所有的任务调度
  - service crond restart ：重启任务调度

### at  定时任务

- 基本介绍
  - at 命令是一次性定时计划任务，at 的守护进程 atd 会以后台模式运行，检查作业队列
  - 默认情况下，atd 守护进程每60秒检查作业队列，有作业时，会检查作业的运行时间，如果时间与当前时间匹配，则运行此作业
  - at 命令是一次性定时计划任务，执行完一个任务后不再执行此任务了
  - 在使用 at 命令的时候，一定要保证 atd 进程的启动，可以使用相关指令来查看
    - **ps -ef | grep atd** 
- at 命令格式
  - **at [ 选项 ] [ 时间 ]**
  - Ctrl + d 组合键按两次结束 at 命令的输入 
  - 按 ctrl + 删除键 可以删除输入
- at 时间的定义
  - at 指定时间的方法
    1. **hh:mm** 式的时间设定，假如当天的该时间已经过去，那么就放到第二天执行，例如：**04：00**
    2. 使用 **midnight，noon，teatime** 等比较模糊的词语来指定时间
    3. 采用 12 小时计时制，即在时间后面加上 **am** 或 **pm** 来说明上午还是下午，例如：**12pm**
    4. 指定命令执行的具体日期，指定格式为 **month day** 或 **mm/dd/yy** 或 **dd.mm.yy**，指定的日期必须跟在指定时间的后面，例如：**04：00 2021-03-01**
    5. 使用相对计时法，指定格式为：**now + count time-units**，now 代表当前时间，count 表示时间的数量，time-units 表示时间单位，例如：**now + 5 minutes**
       - 时间单位包括：minutes，hours，days，weeks
    6. 直接使用 **today，tomorrow** 来指定完成命令的时间
- 其他命令
  - **atq** 查询作业队列
  - **atrm <作业编号>** 删除作业

### Linux 硬盘分区

- 概述
  - linux 将一块硬盘分为几个分区，例如 sda 被分为 sda1、sda2、sda3，然后将这些分区挂载到文件系统中的某个目录，例如，sda1 被挂载到 **/boot**，sda2 被挂载到 **/swap**，sda3 被挂载到 **/** 根目录
  - 可以使用 **lsblk** 查看 linux 分区信息
    - **lsblk -f** 查看更详细的分区信息
- 硬盘说明
  1. Linux 硬盘分为 IDE 硬盘和 SCSI 硬盘，目前基本上是 SCSI 硬盘
  2. 对于 IDE 硬盘，驱动器标识符为 **"hdx~"**，其中 **"hd"** 表明分区所在设备的类型，这里是指 IDE 硬盘。
     - **"x"** 为盘号，**a** 为基本盘，**b** 为基本从属盘，**c** 为辅助主盘，**d** 为辅助从属盘。
     - **"~"** 代表分区，前四个分区用数字 1-4 表示，它们是主分区或扩展分区，从5开始就是逻辑分区。
     - 例如，**hda3** 表示为第一个 IDE 硬盘上的第三个主分区或扩展分区，**hdb2** 表示第二个 IDE 硬盘上的第二个主分区或扩展分区。
  3. 对于 SCSI 硬盘，则标识为 **"sdx~"**，SCSI 硬盘是用 **"sd"** 来表示分区所在设备的类型的，其余则和 IDE 硬盘的表示方法一样。
- 挂载的经典案例
  1. 虚拟机添加硬盘
  2. 分区
     - 指令：**fdisk /dev/sdb**
     - **m** 显示命令列表
     - **p** 显示磁盘分区
     - **n** 新增分区
     - **d** 删除分区
     - **w** 写入并退出
     - 说明：开始分区后输入 n 新增分区，然后选择 p 指定分区类型为主分区。两次回车默认剩余全部空间，最后输入w 写入分区并退出，若不保存退出输入 q。
  3. 格式化分区
     - 指令：**mkfs -t ext4 /dev/sdb1**
  4. 挂载
     - 挂载指令：**mount /dev/sdb1 /newdisk**
     - 卸载指令：**umount /dev/sdb1**  或者  **umount /newdisk**
     - **注意：这样指定挂载后，重启后会失效**
  5. 设置可以自动挂载 
     - 设置永久挂载方法
       - 将需要挂载的分区信息添加到 **/etc/fstab** 文件中
       - 添加完成后，执行 mount -a 生效，或者重启生效

### 磁盘情况查询

#### 查询系统整体磁盘使用情况

- 基本语法
  - **df -h** 

#### 查询指定目录的磁盘占用情况（目录大小）

- 基本语法
  - **du -h <目录>**
  - 默认为当前目录
- 选项说明 
  - **-s** 指定目录占用大小汇总
  - **-h** 带计量单位
  - **-a** 含文件
  - **--max-depth=1** 子目录深度
  - **-c** 列出明细的同时，增加汇总值
- 应用实例
  - 查询 /opt 目录的磁盘占用情况，深度为1

#### 工作实用指令

1. 统计 /opt 文件夹下文件的个数
   - **ll /opt | grep "^-" | wc -l**
2. 统计 /opt 文件夹下目录的个数
   - **ll /opt | grep "^d" | wc -l**
3. 统计 /opt 文件夹下文件的个数，包括子文件夹里的
   - **ll -R /opt | grep "^-" | wc -l**
4. 统计 /opt 文件夹下目录的个数，包括子文件夹里的
   - **ll -R /opt | grep "^d" | wc -l**
5. 以树状显示目录结构指令： **tree <目录>**
   - 使用 **yum install tree**

### 网络配置

#### 基本介绍

- 虚拟机的网络配置：NAT网络配置
- 测试主机之间网络连通性
  - 基本语法：**ping <目的主机>**
  - ping www.baidu.com
  - ping 192.168.26.1

#### linux 网络配置

1. 第一种方法，自动获取

   - 说明：登陆后，通过界面来设置自动获取 ip
   - 特点：linux 启动后会自动获取 IP
   - 缺点：每次自动获取的 ip 地址可能不一样
   - 优点：避免了 ip 冲突

2. 第二种方法，指定 ip

   - 要求：将 ip 地址配置为静态的 

   - 说明：直接修改配置文件来指定 ip，并可以连接外网（程序员推荐）
   - 方法步骤
     1. vim /etc/sysconfig/network-scripts/ifcfg-ens33
     2. 修改或添加以下字段
        - BOOTPROTO="static"
        - IPADDR="192.168.200.130"  // IP 地址
        - GATEWAY="192.168.200.2" // 网关
        - DNS1= "192.168.200.2" // 域名解析器
     3. 然后重启系统或者重启网络服务
        - reboot
        - service network restart

#### 设置主机名

- 查看主机名
  - **hostname**
- 修改主机名
  - vim /etc/hostname
- 修改后重启生效

- 设置 hosts 映射
  - 说明：通过主机名找到某个系统（IP）
  - 方法
    1. windows
       - 修改 C:\Windows\System32\drivers\etc\hosts 文件
       - 例如：20.205.243.166 github.com
    2. linux
       - 修改 /etc/hosts 文件
       - 例如：192.168.200.1 主机名
  - hosts
    - 说明：一个文本文件，用来记录 IP 和 HOSTNAME（主机名）的映射关系
  - DNS
    - 说明：Domain Name System 的缩写，域名系统，是互联网上作为域名和 IP 地址相互映射的一个分布式数据库

### 进程

#### 基本介绍

- 在 linux 中，每一个**执行**的程序都称为一个进程，每一个进程都分配一个 ID 号（ PID，进程号）
- 进程分为前台进程和后台进程
- 一般系统的服务都是以后台进程的方式存在，并且常驻在系统中，直到关机
- 执行的程序会被加载到内存中，称为进程

#### 显示系统执行的进程

- **ps <选项>**
- 选项说明
  - -a：显示当前终端所有进程信息
  - -u：以用户的格式显示进程信息
  - -x：显示后台进程运行的参数
- 经常组合使用，**ps -aux**

- ps 显示字段说明
  - VSZ：进程占用的虚拟内存大小，单位 KB
  - RSS：进程占用的物理内存大小，单位 KB
  - TTY：终端名称
  - STAT：进程状态，其中 S 睡眠，s 表示该进程是会话的先导进程，N 表示进程拥有比普通优先级更低的优先级，R 正在运行，D 短期等待，Z 僵死进程，T 被跟踪或者被停止等等
  - STARTER：进程的启动时间
  - TIME：CPU时间，即进程使用CPU的总时间
  - COMMAND：启动进程所用的命令和参数，如果过长会被截断显示

#### 父子进程

- 全格式显示当前所有进程，查看进程的父进程
  - 指令：**ps -ef**
    - -e：显示所有进程
    - -f：全格式显示
    - 常用：**ps -ef | grep xxx**
  - UID：用户 ID
  - PID：进程 ID
  - PPID：父进程 ID

#### 终止进程

- 说明：若是某个进程执行一半需要停止时，或是占用了很多系统资源时，此时可以考虑停止该进程
- 指令
  - 通过进程号终止进程
    - kill [ 选项 ] <进程号>
  - 通过进程名称终止进程
    - killall
- 常用选项
  - -9：表示强迫进程立即停止
- 应用案例
  - 踢掉某个非法登录的用户
    - ps -aux | grep sshd  // 查询非法用户登录的进程号
    - kill <上面查询的进程号>
  - 终止远程登录服务 sshd，在适当的时候重启 
    - /bin/systemctl start sshd.service // 重启sshd
  - 终止多个 gedit（文本编辑器）
    - killall gedit
    - 说明：与 gedit 有关的进程全部终止，包括子进程
  - 强制终止一个进程
    - 添加 -9 选项

#### 查看进程树

- pstree [ 选项 ] ，可以更加直观的来查看进程信息
- 常用选项
  - -p：显示进程的 PID
  - -u：显示进程的所属用户
- 应用案例
  - 以树状的形式显示进程的 PID
    - pstree -p
  - 以树状的形式显示用户 ID
    - pstree -u

#### 服务管理（service）

- 基本介绍
  - 服务的本质就是进程，运行在后台，通常会监听某个端口，等待其他程序的请求，比如mysql，sshd，防火墙等，因此又称为守护进程
- 管理指令
  1. service <服务名> [ 选项 ]
     - 选项说明 start，stop，restart，reload，status
  2. 在Centos7.0后，很多服务不在使用service，而是 systemctl
  3. service 指令管理的服务在 /etc/init.d 中查看
- 服务的运行级别
  - 说明：linux 系统中有七种运行级别（runlevel）：常用的是级别3和5
  - 运行级别
    - 0  关机
    - 1  单用户（找回丢失密码）
    - 2  多用户状态没有网络服务
    - 3  多用户状态有网络服务
    - 4  系统未使用，保留给用户定义
    - 5  图形界面
    - 6  系统重启
    - Centos7.0后，运行级别说明，查看 /etc/inittab 
  - 开机流程
    1. 开机
    2. BIOS
    3. /boot 引导
    4. 启动systemctl进程1
    5. 确定运行级别
    6. 启动运行级别对应的服务
  - chkconfig 指令
    - 基本介绍
      1. 通过该指令可以给服务在各个运行级别设置 自启动/关闭
      2. 该指令管理的服务在 /etc/init.d 查看
      3. 注意：Centos7.0后，很多服务使用 systemctl 管理
    - 基本语法
      - 查看服务
        - chkconfig --list
      - 查看某个服务
        - chkconfig <服务名> --list
      - 设置某一个服务在运行级别5自启动/关闭
        - chkconfig --level 5 服务名 on/off
    - 注意：chkconfig 重新设置服务自启动或关闭，需要重启机器生效
  - systemctl 管理指令
    - 基本语法
      - systemctl [ start | stop | restart | status ] <服务名>
    - systemctl 指令管理的服务在 /usr/lib/systemd/system 查看
    - 常用指令
      1. 查看服务开机启动状态
         - systemctl list-unit-files
      2. 设置服务开机启动
         - systemctl enable <服务名>
         - 默认运行级别为 3 和 5
      3. 关闭服务开机启动
         - systemctl disable <服务名>
         - 默认运行级别为 3 和 5
      4. 查询某个服务是否是自启动
         - systemctl is-enabled <服务名>

#### 防火墙

- 说明：防火墙监听系统中的端口，当外界通过端口访问系统时，如果是打开的端口则放行，如果是关闭的端口则拦截

- 查看系统网络状态的指令
  - netstat -anp
- 在 windows 下访问 linux 111端口
  - telnet 192.168.200.130 111
- 防火墙关闭的情况下可以访问
  - systemctl stop firewalld
  - 临时生效
  - 重启失效
- 防火墙指令
  - 打开端口：firewall-cmd --permanent --add-port = 端口号/协议
  - 关闭端口：firewall-cmd --permanent --remove-port = 端口号/协议
  - 重新载入，才能生效：firewall-cmd --reload
  - 查询端口是否开放：firewall-cmd --query-port = 端口号/协议
  - 使用 netstat -anp 查询协议
- 应用案例
  - 开放 111 端口，测试是否能够 telnet 访问
    - firewall-cmd --permanent --add-port=111/tcp
    - firewall-cmd --reload
    - firewall-cmd --query-port=111/tcp

#### 动态监控进程

- 基本介绍
  - top 和 ps 指令很相似，他们都用来显示正在执行的进程。它们的不同之处在于 top 在执行一段时间可以更新正在运行的进程。
- 基本语法
  - top [ 选项 ]
- 选项说明
  - -d：秒数，指定 top 命令每隔几秒更新，默认是 3 秒
  - -i：使 top 不显示任何闲置或者僵死进程
  - -p：通过指定监控进程 ID 来监控某个进程的状态
- zombie
  - 僵死进程，进程终止，但是内存没有释放
- 交互操作说明
  - P：以 cpu 使用率排序，默认是此选项
  - M：以内存的使用率排序
  - N：以 pid 排序
  - q：退出
- 应用案例
  1. 监视特定用户 xxx
     - 输入top查看执行的进程
     - 然后输入u，再输入用户名
  2. 终止指定的进程
     - 输入top查看执行的进程
     - 输入k，再输入要结束的进程 ID 号
  3. 指定系统状态更新时间（每隔10s自动更新）
     - top -d 10 

#### 监控网络状态

- 基本语法
  - netstat [ 选项 ]
- 选项说明
  - -an：按一定顺序排列输出
  - -p：显示哪个进程在调用
- 应用案例
  - 查看sshd的服务信息
    - netstat -anp | grep sshs
- 检测主机链接命令
  - ping <ip>

#### rpm 包的管理

- 介绍
  - rpm 用于互联网下载包的打包及安装工具，它包含在某些 Linux 分发版中。它生成具有 .rpm 扩展名的文件。RPM 是 RedHat Package Manager （RedHat 软件包管理工具）的缩写，类似 windows 的 setup.exe，这一文件格式名称虽然打上了 RedHat 的标志，但理念是通用的。
- rpm 包的简单查询指令
  - 查询已安装的 rpm 列表：rpm -qa
- rpm 包名基本格式
  - 例如：firefox-60.2.2-1.el7.centos.x86_64
    - 名称：firefox
    - 版本号：60.2.2-1
    - 适用操作系统：el7.centos.x86_64
      - 表示 centos7.x 的64位系统
      - 如果是 i686、i386 表示32位系统，noarch 表示通用
- 指令选项说明
  - rpm -q <软件包名>  查询软件包是否安装
    - rpm -q firefox
  - rpm -qi <软件包名>  查询软件包信息
    - rpm -qi firefox
  - rpm -ql <软件包名>  查询软件安装的文件及位置
    - rpm -ql firefox
  - rpm -qf <文件全路径名>  查询文件所属软件包
    - rpm -qf /etc/passwd
- 卸载 rpm 包
  - 基本语法
    - rpm -e <软件包名称> 
      - rpm -e firefox
  - 注意：如果其他软件包依赖于要卸载的软件包，卸载时可能会出现错误提示，如果确定要卸载，增加参数 --nodeps
    - rpm -e --nodeps foo
- 安装 rpm 包
  - 基本语法
    - rpm -ivh <rpm包的全路径名称>
  - 参数说明
    - -i：install 安装
    - -v：verbose 提示
    - -h：hash 进度条 

#### Yum

- 基本介绍
  - Yum 是一个 Shell 前端软件包管理器。基于 rpm 包管理，能够自动从指定的服务器自动下载 rpm 包进行安装，可以自动处理依赖性关系，并且一次安装所有依赖的软件包
- 基本指令
  - yum list | grep xxx软件列表  查询yum服务器是否有需要安装的软件
  - yum install xxx  下载安装 

## Java EE 定制篇

#### 安装 jdk

- 安装步骤
  1. 下载 jdk 安装包
     - jdk-8u341-linux-x64.tar.gz
  2. 使用 Xftp 将文件上传到 linux 服务器上
  3. 解压 安装包
     - tar -zxvf  jdk-8u341-linux-x64.tar.gz
  4. 将解压出的文件夹放到 /usr/local/java 目录下
  5. 修改环境变量配置文件 /etc/profile
     - 添加以下两行
       - export JAVA_HOME=/usr/loacl/java/jdk1.8.0_341
       - export $PATH=$JAVA_HOME/bin：$PATH
  6. 使配置文件生效
     - source /etc/profile 

#### 安装tomcat

1. 上传安装文件，并解压缩
2. 进入解压目录/bin，启动tomcat bash startup.sh
3. 开放端口 8080
   - firewall-cmd --permanent --add-port=8080/tcp

#### 安装mysql

1. 将mysql安装包上传至linux服务器 /opt/mysql，并解压
   - tar -xvf mysql-5.7.26-1.el7.x86_64.rpm-bundle.tar
2. 如果linux服务器自带的mariadb，需要卸载mariadb，会与MySQL冲突
3. 依次安装以下rpm包
   1. rpm -ivh mysql-community-common-5.7.26-1.el7.x86_64.rpm
   2. rpm -ivh mysql-community-libs-5.7.26-1.el7.x86_64.rpm
   3. rpm -ivh mysql-community-client-5.7.26-1.el7.x86_64.rpm
   4. rpm -ivh mysql-community-server-5.7.26-1.el7.x86_64.rpm
4. 运行mysql
   - systemctl start mysqld
5. 设置mysql root用户密码
   - mysql自动设置root随机密码，查看 /var/log/mysqld.log 可以找到密码
   - 登录mysql
     - mysql -u root -p
   - 设置密码策略
     - set global validate_password_policy=0;
   - 设置root密码，生产服务器要设置复杂密码
     - set password for 'root'@'localhost'=password('nidihanwango');
6. 运行 flush privileges; 使密码生效

## 大数据定制篇

#### Shell 脚本的执行方式

- 脚本格式要求
  - 脚本以 #!/bin/bash 开头
  - 脚本需要可执行权限
- 脚本执行的两种方式
  1. 输入脚本的绝对路径或者相对路径，这种方式需要执行权限
     - ./hello.sh
     - /home/jerry/scripts/hello.sh
  2. bash hello.sh 或 sh hello.sh
     - 这种方式不需要执行权限

#### Shell 的变量

- 基本介绍
  1. Linux Shell 中的变量分为系统变量和用户自定义变量
  2. 系统变量：$HOME、$PWD、$SHELL、$USER 等等
  3. 显示当前 shell 中的所有变量
- 定义语法
  1. 定义变量：变量=值
     - 等号两边不要打空格
  2. 撤销变量：unset 变量
  3. 声名静态变量：readonly 变量
     - 注意：不能使用 unset 撤销 
- 变量定义的规则
  1. 变量名称可以由字母、数字、下划线组成，但是不能以数字开头。
  2. 等号两侧不能有空格
  3. 变量名称一般习惯大写，这是一个规范
- 将命令的返回值赋给变量
  1. **A=`pwd``**，运行里面的命令，并把结果返回给变量A
  2. **A=$(pwd)** 等价于反引号

#### 设置环境变量

- 基本语法
  1. **export 变量名=变量值** 设置环境变量/全局变量
  2. **source 配置文件** 让修改后的配置信息立即生效
  3. **echo $变量名** 查询环境变量的值

####   位置参数变量

- 基本介绍
  - 当我们执行一个shell脚本时，如果希望获取到命令行的参数信息，就可以使用到位置参数变量
- 基本语法
  1. **$n** n为数字，$0代表命令本身，$1-$9代表第一到第九个参数，十以上的参数需要用大括号包括，如${10}
  2. **$*** 这个变量代表命令行中所有的参数，$* 把所有的参数看成一个整体
  3. **$@** 这个变量也代表命令行中所有参数，不过 $@ 把每个参数区别对待
  4. **$#** 这个变量代表命令行中所有参数的个数

#### 预定义变量

- 基本介绍
  - shell 设计者事先定义号的变量，可以直接在shell脚本中使用
- 基本语法
  - **$$** 当前进程的进程号
  - **$!** 后台运行的最后一个进程的进程号
  - **$?** 最后一次执行的命令的返回状态，0表示上个命令正确执行，非0表示上个命令执行不正确

#### 运算符

- 基本语法
  1. $((运算式))  $[运算式]  expr m + n 
  2. expr运算符间要有空格，使用反引号括起来，可以将expr的结果赋给某个变量
  3. expr m - n
  4. expr  \\*、/、%  乘，除，取余 

#### 条件判断

- 基本语法
  - [ condition ]
- 注意condition前后要有空格
  - 非空返回true，可以使用 $? 验证
- 判断语句
  - 常用判断条件
    1. **=** 字符串比较
    2. 两个整数的比较
       - **-lt** 小于，**-le** 小于等于，**-eq** 等于，**-gt** 大于，**-ge** 大于等于，**-ne** 不等于
    3. 按照文件权限进行判断
       - **-r** 有读的权限
       - **-w** 有写的权限
       - **-x** 有执行的权限
    4. 按照文件类型进行判断
       - **-f** 文件存在并且是一个常规的文件
       - **-e** 文件存在
       - **-d** 文件存在并且是一个目录

#### 流程控制

- if 判断 

  - ```shell
    #基本语法
    #单分支
    if [ condition ]
    then
    	...
    fi
    #多分支
    if [ condition ]
    then
    	...
    elif [ condition ]
    then
    	...
    fi
    ```

- case 语句

  - ```shell
    #基本语法
    case $var in
    "值1")
    	...
    ;;
    "值2")
    	...
    ;;
    ...省略其他分支...
    *)
    	...
    ;;
    esac
    ```

- for 循环

  - ```shell
    #基本语法1
    for i in 值1，值2，值3...
    do
    	...
    done
    
    #基本语法2
    for(( 初始值；循环控制条件；变量变化 ))
    do
    	...
    done
    #例如
    for(( i=1;i<=100;i++ ))
    do
    	SUM=$[ $SUM + $i ]
    done
    ```

  - $* 将命令行所有参数视为一个整体
  - $@ 会将命令行的所有参数分别对待

- while 循环

  - ```shell
    #基本语法
    while [ 条件判断式 ]
    do
    	...
    done
    #注意：while 和 [ 条件判断式 ] 之间有空格
    #案例1：从命令行输入一个数n，统计 1+..+n 的值是多少？
    I=1
    SUM=0
    while [ $I -le $1 ]
    do
    	SUM=$[ $SUM + $I ]
    	I=$[ $i + 1 ]
    done
    ```


#### read 读取控制台输出

```shell
#基本语法
read [选项]<参数>
#选项：
	-p：指定读取值时的提示符
	-t：指定读取值时等待的时间，单位秒，如果没有在指定的时间内输入，就不再等待了
#参数:
	变量：指定读取值的变量名
#应用实例
	1.读取控制台输入一个num值
	read -p "请输入一个数：" num
	echo $num
	2.读取控制台输入一个num值，在十秒内输入
	read -t 10 -p "请输入一个数：" num
```

#### 函数

```shell
#函数介绍
	shell编程和其他编程语言一样，有系统函数，也可以自定义函数
#系统函数
1.bashname
	功能：返回完整路径最后/的部分，常用于获取文件名
	语法：
		basename [pathname][suffix]
		basename [string][suffix]
    说明：
    	basename命令会删除所有前缀包括最后一个'/'字符，然后将字符串显示出来。
    	suffix为后缀，如果suffix被指定了，该命令会将suffix部分也删除掉
	应用实例:
	1.返回 /home/jerry/scripts/hello.sh的'hello'部分
		basename /home/jerry/scripts/hello.sh .sh
2.dirname
	功能：返回完整路径最后'/'的前面的部分，常用于返回路径部分
	语法：
		dirname <文件的绝对路径>
    说明：
    	从给定的包含绝对路径的文件名中去除文件名，返回剩下的路径（目录部分）
    应用实例：
    	返回 /home/jerry/scripts/hello.sh 的目录路径
    	dirname /home/jerry/scripts/hello.sh
#自定义函数
	语法：
		function funname(){
			Action;
			[return int;]
		}
     调用：
     	直接写函数名：funname [值]
     应用实例：
     	计算输入两个参数的和
     		#自定义函数
     		function getSum(){
     			SUM=$[$a+$b]
     			echo "SUM=$SUM"
     		}
     		#输入两个数
     		read -p "请输入一个数：" a
     		read -p "请输入另一个数：" b
     		#调用自定义函数
     		getSum $a $b
```

#### Shell 编程综合案例 --- 备份数据库

```shell
#需求分析
#1.每天凌晨2：30备份数据库test到 /data/backup/db
#2.备份开始和备份结束能够给出相应的提示信息
#3.备份后的文件要求以备份时间为文件名，并打包成.tar.gz的形式，比如：2021-03-12_230201.tar.gz
#4.在备份的同时，检查是否有10天前备份的数据库文件，如果有将其删除

#/usr/sbin/backupdb.sh
#!/bin/bash
#备份文件的目录
DIR=/data/"D:"/backup/db
#当前时间
DATETIME=`date "+%Y-%m-%d_%H%M%S"`
#数据库地址
HOST=localhost
#数据库用户名
DB_USER=root
#数据库密码
DB_PW=[password]
#要备份的数据库
DATABASE=test
#判断目录是否存在，不存在则创建
if [ ! -d ${DIR}/${DATETIME} ]
then
        mkdir -p ${DIR}/${DATETIME}
fi
#备份数据库
mysqldump -u${DB_USER} -p${DB_PW} --host=${HOST} -q -R --databases ${DATABASE} > ${DIR}/${DATETIME}/${DATETIME}.sql

if [ -f ${DIR}/${DATETIME}/${DATETIME}.sql ]
then
        cd ${DIR}
        tar -zcvf ${DATETIME}.tar.gz ${DATETIME}
        
        if [ $? -eq 0 ]
        then
                echo 数据库${DATABASE}备份完成
        fi

        rm -rf ${DIR}/${DATETIME}
        find ${DIR} -atime +10 -name "*.tar.gz" -exec rm -rf {} \;
fi
```

## Linux 高级篇

### 日志

```shell
#基本介绍
1.日志文件是重要的系统信息文件，其中记录了许多重要的系统事件，包括用户的登录信息，系统的启动信息，系统的安全信息，邮件相关信息，各种服务相关信息等
2.日志对于安全来说也很重要，它记录了系统每天发生的各种事情，通过日志来检查错误发生的原因，或者受到攻击时攻击者留下的痕迹
3.日志是用来记录重大事件的工具
```

#### 系统常用的日志

```shell
#系统常用的日志
/var/log/ 目录就是系统日志文件的保存位置

/var/log/boot.log  #系统启动日志
/var/log/cron  #记录与系统定时任务相关的日志
/var/log/cups  #记录打印信息的日志
/var/log/dmesg  #记录了系统在开机时内核自检的信息，也可以使用dmesg命令直接查看内核自检信息
/var/log/btmp  #记录错误登录的日志，这个文件是二进制文件，不能直接用vi查看，而要用lastb命令查看
/var/log/lastlog  #记录系统中所有用户最后一次登录时间的日志，这个文件也是二进制文件，需要用lastlog查看
/var/log/maillog  #记录邮件信息的日志
/var/log/message  #记录系统重要消息的日志，这个日志文件中会记录linux系统中绝大多数重要信息，如果系统出问题，首先要检查的应该就是这个日志文件
/var/log/secure  #记录验证和授权方面的信息，只要涉及账户密码的程序都会记录，比如系统的登录，ssh的登录，su切换用户，sudo授权，添加用户，修改用户密码都会记录在这个日志文件中
/var/log/wtmp  #永久记录所有用户的登录，注销信息，同时记录系统的启动，重启，关机事件，是二进制文件，需要使用last命令查看
/var/tun/ulmp  #记录当前已经登录的用户信息，这个文件会随着用户的登录和注销而不断变化，只记录当前登录用户的信息，这个文件不能用vi查看，需要使用w、who、users等命令查看
```

#### 日志管理服务

```shell
#日志管理服务 rsyslogd
	Centos7.6日志服务是 rsyslogd，Centos6.x 日志服务是 syslogs。rsyslogd 功能更强大。
	rsyslogd 的使用、日志文件的格式，和 syslogd 服务兼容

#查询 Linux 中的 rsyslogd 服务是否启动
	ps -aux | grep rsyslog | grep -v grep
#查询 rsyslogd 服务的自启动状态
	systemctl list-unit-files | grep rsyslog

#配置文件
    /etc/rsyslog.conf
#配置文件说明
    编辑文件时的格式为：*.*  存放日志文件
    第一个*号代表日志类型，第二个*号代表日志级别
1.日志类型分为：
    auth #pam产生的日志
    anthpriv #ssh、ftp等登录信息的验证信息
    corn #时间任务相关
    kern #内核
    lpr #打印
    mail #邮件
    mark(syslog)-rsyslog #服务内服的信息，时间标识
    news #新闻组
    user #用户程序产生的相关信息
    uucp #unix to nuix copy主机之间相关的通信
    local 1-7 #自定义的日志设备
2.日志级别分为：
	debug #有调试信息的，日志通信最多
	info #一般信息日志，最常用
	notice #最具有重要性的普通条件信息
	warning #警告级别
	err #错误级别，阻止某个功能或者模块不能正常工作的信息
	crit #严重级别，阻止整个系统或者整个软件不能正常工作的信息
	alert #需要立刻修改的信息
	emerg #内核崩溃等重要信息
	none #什么都不记录
从上到下，级别从低到高，记录信息越来越少

#日志文件的查看
由日志服务 rsyslogd 记录的日志文件，日志文件的格式包含以下4列：
cat /var/log/secure
Aug 26 15:59:51 你滴韩王 sshd[1708]: Accepted password for jerry from 192.168.200.1 port 50043 ssh2
1.事件产生的时间 ：Aug 26 15:59:51
2.产生事件的服务器的主机名 ：你滴韩王
3.产生事件的服务名或程序名 ：sshd[1708]
4.事件的具体信息 ： Accepted password for jerry from 192.168.200.1 port 50043 ssh2

#自定义日志服务
在 /etc/rsyslog.conf 中添加一个日志文件 /var/log/test.log
步骤：
	1.vim /etc/rsyslog.conf
	2.# 测试自定义日志文件
  	  *.*         /var/log/test.log
```

#### 日志轮替

```shell
#基本介绍
日志轮替就是把旧的日志文件移动并改名，同时建立新的空日志文件，当旧的日志文件超出保存范围后，就会被删除
#日志轮替-文件命名
	1.centos7 使用 logrotate 进行日志轮替管理，要想改变日志轮替文件名字，通过 /etc/logrotate.conf 配置文件中 dateext 参数
	2.如果配置文件中有 dateext 参数，那么日志会用日期来作为日志文件名的后缀，例如"secure-20201010"。这样日志文件名不会重叠，也就不需要日志文件的改名，只需要指定保存日志个数，删除多余的日志文件即可。
	3.如果配置文件中没有 dateext参数，日志文件就需要进行改名，当第一次进行日志轮替时，当前的"secure"日志会自动改名为"secure.1"，然后新建"secure"日志，用来保存新的日志信息。当第二次进行日志轮替时，"secure.1"会自动改名为"secure.2",当前的"secure"日志会自动改名为"secure.1"，然后也会新建"secure"日志，用来保存新的日志，依次类推。
```

##### 日志轮替-配置文件

```shell
#logrotate 配置文件
/etc/logrotate.conf 为 logroteta 的全局配置文件
# rotate log files weekly 每周对日志文件进行一次轮替
weekly
# keep 4 weeks worth of backlogs 共保存4份日志文件，当建立新的日志文件时，旧的会被删除
rotate 4
# create new (empty) log files after rotating old ones 日志轮替后，创建新的日志文件
create
# use date as a suffix of the rotated file 使用日期作为日志轮替文件的后缀
dateext
# uncomment this if you want your log files compressed 日志文件是否压缩，如果取消注释，则日志会在转储的同时进行压缩
#compress
# RPM packages drop log rotation information into this directory 包含/etc/logrotate.d 目录中所有的子配置文件，也就是说会把这个目录下的所有子配置文件读取进来
include /etc/logrotate.d
# no packages own wtmp and btmp -- we'll rotate them here
/var/log/wtmp {
    monthly #每月对日志文件进行一次轮替
    create 0664 root utmp #建立新的日志文件，权限是 0664，所有者是root，所属组是 utmp 组
    minsize 1M #日志文件的最小轮替大小是1MB，也就是日志一定要超过1MB才会轮替，否则就算时间达到一个月，也不进行日志轮替
    rotate 1 #仅保留一个日志备份
}
```

##### 日志轮替-参数说明

|          参数           |                           参数说明                           |
| :---------------------: | :----------------------------------------------------------: |
|          daily          |                     日志的轮替周期是每天                     |
|         weekly          |                     日志的轮替周期是每周                     |
|         monthly         |                     日志的轮替周期是每月                     |
|       rotate 数字       |               保存日志文件的个数。0指没有备份                |
|        compress         |                 日志轮替时，旧的日志进行压缩                 |
| create mode owner group |       建立新日志，同时指定新日志的权限与所有者和所属组       |
|      mail address       |      当日志轮替时，输出内容通过邮件发送到指定的邮件地址      |
|       missinggok        |            如果日志不存在，则忽略该日志的警告信息            |
|       notifempty        |              如果日志为空文件，则不进行日志轮替              |
|   minsize <文件大小>    | 日志轮替的最小值，也就是日志一定要超过这个最小值才会轮替，否则就算时间达到，也不进行日志轮替 |
|     size <文件大小>     |    日志只有大于指定大小才进行日志轮替，而不是按照时间轮替    |
|         dateext         |                使用日期作为日志轮替文件的后缀                |
|      sharedscripts      |                在此关键字之后的脚本只执行一次                |
|   prerotate/endscript   |                  在日志轮替之前执行脚本命令                  |
|  postrotate/endscript   |                  在日志轮替之后执行脚本命令                  |

##### 自定义日志轮替策略

```shell
#第一种方式
直接在/etc/logrotate.conf配置文件中写入该日志的轮替策略
#第二种方式
在/etc/logrotate.d/目录下创建自定义日志轮替策略文件，在该轮替文件中写入正确的轮替策略
#tips
推荐使用第二种方式，因为系统中需要轮替的日志非常多，如果全都直接写入/etc/logrotate.conf全局配置文件，那么这个文件的可管理性就会非常差，不利于文件的维护
#例如
/var/log/test.log{
        daily
        rotate 7
        minsize 1MB
        missinggok
        notifempty
}
```

##### 日志轮替机制原理

```shell
#概述
日志轮替之所以可以在指定的时间备份日志，是依赖系统定时任务，在/etc/cron.daily/目录中，有一个 logrotate文件（可执行），logrotate通过这个文件依赖定时任务执行的。
```

#### 查看内存日志

```shell
#概述
有一部分日志存放于内存，还没有写入文件（磁盘），重启清空
#指令
journalctl #可以查看全部内存日志
journalctl -n 3 #查看最新3条
journalctl --since 19:00 --until 19:10:10 #查看起始时间到结束时间的日志可加日期
journalctl -p err #报错日志
journalctl -o verbose #日志详细内容
journalctl _PID=1245 _COMM=sshd #查看包含这些参数的日志（在详细日志查看）
或者
journalctl | grep sshd
#注意
journalctl 查看的是内存日志，重启清空
```

### 定制 Linux 系统

```shell
#Linux 启动流程
1.首先 Linux 要通过自检，检查硬件设备有没有故障
2.如果有多块启动盘的话，需要在BIOS中选择启动磁盘
3.启动MBR中的 bootloader 引导程序
4.加载内核文件
5.执行所有进程的父进程 systemd
6.欢迎界面
在Linux的启动流程中，加载内核文件时关键文件
1.kernel文件：vmlinuz-3.10.0-957.el7.x86_64
2.initrd文件：initramfs-3.10.0-957.el7.x86-64.img
```

```shell
#定制linux思路分析
1.在现有的Linux系统上加一块硬盘/dev/sdb，在硬盘上分两个分区，一个是/boot，一个是/根分区，并将其格式化
2.在/dev/sdb硬盘上，制作独立的linux系统，里面的所有文件是需要拷贝进去
3.把内核文件和initramfs文件拷到/dev/sdb
4.以上步骤完成，自制的linux就完成，创建一个新的linux虚拟机，将硬盘指向创建的硬盘，启动即可
```

```shell
#定制步骤
1.添加新硬盘到现有的Linux系统
	lsblk -f #查询硬盘
2.为新硬盘进行分区、格式化
	fdisk <新硬盘> #分区
	mkfs -t ext4 <新硬盘分区> #格式化
3.将分区好的硬盘挂载到系统上
	mount <新硬盘分区> <挂载点>
4.安装grub
grub2-install --root-directory=/mnt /dev/sdb
5.确认是否安装成功
hexdump -C -n 512 /dev/sdb
6.内核文件拷贝至目标磁盘
rm -rf /mnt/boot/* #先清空该目录下的文件
cp -rf /boot/* /mnt/boot
7.修改 grub2/grub.cfg文件
将文件中指定硬盘分区的UUID改为新硬盘分区的UUID
/boot
/
然后在 "linux16" 行的末尾加上 selinux=0 init=/bin/bash
8.创建目标主机根文件系统
mkdir -pv /mnt/sys/{etc/rc.d,usr,var,proc,sys,dev,lib,lib64,bin,sbin,boot,srv,mnt,media,home,root} 
9.拷贝需要的bash(也可以拷贝你需要的指令)和库文件给新的系统使用
cp /lib64/*.* /mnt/sys/lib64/
cp /bin/bash /mnt/sys/bin/
10.现在可以创建一个新的虚拟机，然后将默认分配的硬盘移除掉，指向刚刚创建的硬盘即可
```

```shell
#指令说明
grub2-install --root-directory=/mnt /dev/sdb

	后面跟的为/dev/sdb设备，非分区，因为grub的第一阶段是装在MBR中的，即指定第一阶段安装位置，但是经过测试，后面即使指的为分区，系统会理解为装在该分区所在设备，仍然可以安装成功。
	--root-directory=/mnt 指定第二阶段安装位置，此目录下必须要有boot目录，如果没有会安装不了（待验证），如果不指定，默认为当前根。
```

### Linux 内核源码

#### 介绍

```shell
#为什么要阅读Linux源码
1.爱好，就是喜欢linux(黑客精神)
2.想深入理解linux底层运行机制，对操作系统有深入理解
3.阅读Linux内核，会对整个计算机体系有一个更深刻的认识。作为开发者，不管从事的是驱动开发，应用开发还是后台开发，都需要了解操作系统内核的运行机制，这样才能写出更好的代码
4.作为开发人员不应该只局限在自己的领域，设计的模块看起来小，但是不了解进程的调用机制，不知道进程为什么会阻塞、就绪、执行几个状态,那么很难写出优质的代码
5.找工作面试的需要
6.深入的了解一个操作系统的底层机制,(比如linux/unix) 最好是源码级别的，这样写多线程高并发程序，包括架构，优化，算法等，高度不一样的
```

```shell
#基本介绍
Linux的内核源代码可以从网上下载，解压缩后文件一般也都位于linux目录下。内核源代码有很多版本，可以从linux0.01内核入手，总共的代码1w行左右，最新版本 5.9.8 总共代码超过700w行，非常庞大
内核地址: https://www.kernel.org/
```

```shell
#linux0.01 内核源码目录&阅读
1.阅读内核源码需要懂c语言
2.阅读源码前，应知道Linux内核源码的整体分布情况。现代的操作系统一般由进程管理、内存管理、文件系统、驱动程序和网络等组成。Linux内核源码的各个目录大致与此相对应.
3.在阅读方法或顺序上，有纵向与横向之分。所谓纵向就是顺着程序的执行顺序逐步进行;所谓横向，就是按模块进行。它们经常结合在一起进行。
4.对于Linux启动的代码可顺着Linux的启动顺序一步步来阅读;对于像内存管理部分，可以单独拿出来进行阅读分析。实际上这是一个反复的过程，不可能读一遍就理解
```

##### 目录介绍

|    boot     |                 和系统引导相关的代码                 |
| :---------: | :--------------------------------------------------: |
|   **fs**    |           **存放linux支持的文件系统代码**            |
| **include** | **存放linux核心需要的头文件，比如：asm、linux、sys** |
|  **init**   |                     **main文件**                     |
| **kernel**  |               **和系统内核相关的源码**               |
|   **lib**   |                    **存放库代码**                    |
|   **mm**    |               **和内存管理相关的代码**               |
|  **tools**  |                       **工具**                       |

##### main.c 说明

|     time_init()     |         初始化运行时间         |
| :-----------------: | :----------------------------: |
|     tty_init()      |           tty初始化            |
|     trap_init()     |  陷阱门（硬件中断向量）初始化  |
|    sched_init()     |         调度程序初始化         |
|    buffer_init()    |         缓冲管理初始化         |
|      hd_init()      |           硬盘初始化           |
|        sti()        | 所有初始化工作完成后，开启中断 |
| move_to_user_mode() |         进入到用户模式         |

#### linux 内核最新版

- https://www.kernel.org/

#### 内核升级

```shell
#将Centos系统从7.6版本内核升级到7.8版本内核
#升级需要考虑兼容性问题
#步骤
uname -a //查看当前版本内核
yum info kernel -q //检测内核版本，显示可以升级的内核
yum update kernel //升级内核
yum list kernel -q //查看已经安装的内核
```

### 备份和恢复

```shell
#基本介绍
如果系统出现异常或者数据损坏，后果严重，要重做系统，还会造成数据丢失，所以可以使用备份和恢复技术
linux 的备份和恢复很简单，有两种方式：
1.把需要的文件或者分区使用tar打包就行，下次需要恢复的时候，再解压覆盖即可
2.使用dump和restore命令
#安装dump和restore
yum -y install dump
yum -y install restore
```

```shell
#使用dump完成备份
#基本介绍
dump 支持分卷和增量备份
增量备份：是指备份上次备份后修改/增加过的文件，也称为差异备份
#dump语法说明
dump [-cu] [-0123456789] [-f <备份后的文件名>] [-T <日期>] [要备份的目录或文件系统]
-c：创建新的归档文件，并将由一个或多个文件参数所指定的内容写入归档文件的开头
-0123456789：备份的层级，0为完整备份，会备份所有文件，若指定0以上的层级，则备份至上一次备份以来修改/新增的文件，到9后，可以再次轮替
-f <备份后的文件名>：指定备份后文件名
-j：使用 bzlib 库压缩备份文件，也就是将备份后的文件压缩成 bz2格式，让文件更小
-T <日期>：指定开始备份的时间和日期
-u：备份完毕后，在/etc/dumpdates/中记录备份的文件系统，层级，日期与时间等
-t：指定文件名，若该文件已存在备份文件中，则列出名称
-W：显示需要备份的文件及其最后一次备份的层级，时间，日期
-w：与-W类似，但仅显示需要备份的文件
#应用案例1
将/boot分区所有内容备份到/opt/boot.bak0.bz2文件中，备份层级为0
dump -0uj -f /opt/boot.bak0.bz2 /boot
#应用案例2
在/boot目录下新增文件，备份层级为1，比较生成的备份文件
dump -1uj -f /opt/boot.bak1.bz2 /boot
#通过dump命令在配合crontab可以实现无人值守备份
#查看备份记录
dump -W //显示需要备份的文件及其最后一次备份的层级，时间，日期
cat /etc/dumpdates //查看备份时间文件 
#dump备份文件或者目录
dump备份分区时，支持增量备份，如果备份文件或者目录，不再支持增量备份，即只能使用0级别备份
#应用案例
使用dump备份/etc目录
dump -0j -f /opt/etc.bak0.bz2 /etc
#下面这条语句会报错，提示DUMP: Only level 0 dumps are allowed on a subdirectory
dump -1j -f /opt/etc.bak1.bz2 /etc
#同时-u参数也不能使用
dump -0uj -f /opt/etc.bak0.bz2 /etc
DUMP: You can't update the dumpdates file when dumping a subdirectory
#如果是重要的备份文件，比如数据区，建议将文件上传到其他服务器保存
```

```shell
#使用restore完成恢复
#基本介绍
restore命令用来恢复已备份的文件，可以从dump生成的备份文件中恢复原文件
#基本语法
restore [模式] [选项]
四个模式：
	-C：对比模式，将备份的文件与已存在的文件相互对比
	-i：交互模式，在进行还原操作时，restore指令将依序询问用户
	-r：还原模式
	-t：查看模式，查看备份文件中有哪些文件
选项：
	-f <备份文件>：从指定的文件中读取备份数据，进行还原操作
#应用案例1
restore命令比较模式，比较备份文件和原文件的区别
mv /boot/hello /boot/hello100
restore -C -f boot.bak1.bz2 //注意和最新的文件比较
mv /boot/hello100 /boot/hello
restore -C -f boot.bak1.bz2
#应用案例2
restore命令查看模式，看备份文件有哪些数据/文件
restore -t -f boot.bak1.bz2
#应用案例3
restore命令还原模式，如果有增量备份，需要把增量备份文件也进行恢复，有几个增量备份文件，就要恢复几个，按顺序来恢复即可
mkdir /opt/boottmp
cd /opt/boottmp //文件会恢复到这里
restore -r -f /opt/boot.bak0.bz2 //恢复到第一次完全备份状态
restore -r -f /opt/boot.bak1.bz2 //恢复到第二次增量备份状态

```

### 可视化管理 

#### webmin

```shell
#基本介绍
webmin是功能强大的基于web的unix/linux系统管理工具，管理员通过浏览器访问webmin的各种管理功能并完成相应的管理操作，除了各版本的linux以外还可用于：AIX、HPUX、Solaris、Unixware、Irix、FreeBSD等系统
#安装webmin
1.下载地址：https://www.webmin.com/
2.安装：rpm -ivh webmin-2.000-1.noarch.rpm
3.重置密码 /usr/libexec/webmin/changepass.pl /etc/webmin root test
root是webmin的用户名，不是os的，把root密码改为test
4.修改webmin服务的端口号(默认是10000 出于安全目的)
vim /etc/webmin/miniserv.conf #修改端口
将port=10000修改为其他端口号，如port=6666
listen=10000修改为和port相同端口，如listen=6666
5.重启webmin
/etc/webmin/restart #重启
/etc/webmin/start #启动
/etc/webmin/stop #停止
6.防火墙放开8888端口
firewall-cmd --zone=public --add-port=8888/tcp --permanent #配置防火墙开放6666端口
firewall-cmd --reload #更新防火墙配置
firewall-cmd --zone=public --list-ports #查看已经开放的端口号
7.登录webmin
访问 http://ip:8888
使用root账户和重置的密码test登录

ps：#安装可能会出现的问题，缺少依赖
webmin-1.930-1.noarch.rpm
[root@localhost]# rpm -ivh webmin-1.930-1.noarch.rpm
警告：webmin-1.930-1.noarch.rpm: 头V4 DSA/SHA1 Signature, 密钥 ID 11f63c51: NOKEY
错误：依赖检测失败：
perl(Net::SSLeay) 被 webmin-1.930-1.noarch 需要
perl(Encode::Detect) 被 webmin-1.930-1.noarch 需要
[root@localhost]# yum -y install perl-Net-SSLeay perl-Encode-Detect
已加载插件：fastestmirror, langpacks

#webmin使用演示
比如修改 语言设置，ip访问控制，查看进程，修改密码，任务调度，mysql等..
```

#### 宝塔 bt

```shell
#基本介绍
bt宝塔linux面板是提升运维效率的服务器管理软件，支持一键LAMP/LNMP/集群/监控/网站/FTP/数据库/JAVA等多项服务器管理功能
#安装和使用
http://www.bt.cn
yum install -y wget && wget -O install.sh http://download.bt.cn/install/install_6.0.sh && sh install.sh ed8484bec
安装成功后控制台会显示登录地址，账户密码，复制浏览器打开登录
#使用介绍
比如可以登录终端，配置，快捷安装运行环境和系统工具，添加计划任务脚本
#忘记密码或者登录地址
bt default #查看初始信息
```

## Linux 面试题

```shell
#腾讯面试题
问题：
	分析日志t.log(访问量)，将各个ip地址截取，并统计出现次数，并按从大到小排序
    t.log
    http://192.168.200.10/index.html
    http://192.168.200.20/index.html
    http://192.168.200.40/index.html
    http://192.168.200.10/order.html
    http://192.168.200.20/order.html
解：
	cat t.log | cut -d "/" -f 3 | sort | uniq -c | sort -nr
	
问题：
	统计连接到服务器的各个ip情况，并按连接数从大到小排序	
解：
	netstat -an | grep ESTABLISHED | awk -F " " '{print $5}' | cut -d : -f 1 | sort | uniq -c | sort -nr
```

```shell
#滴滴面试题
问题：
	如果忘记了mysql5.7数据库root账户的密码，如何找回？
解：
	1.修改 /etc/my.cnf 文件
	在 [mysqld] 下面添加语句 skip-grant-tables
	2.重启mysqld服务
	service mysqld restart 或者
	systemctl restart mysqld
	3.现在可以使用空密码登录mysql
	直接输入mysql登录
	4.使用数据库mysql
	use mysql
	5.修改user表的authentication_string字段
	update user set authentication_string=password('xxx') where User='root';
	6.修改 /etc/my.cnf 文件
	将语句删除或注释 #skip-grant-tables
	7.重启mysqld服务
	8.测试新密码登录mysql
```

```shell
#美团面试题
问题：
	统计ip访问情况，要求分析nginx访问日志access.log，找出访问页面次数在前两位的ip
解：
	cat access.log | awk -F " " '{print $1}' | sort | uniq -c | sort -nr | head -2

问题：
	使用tcpdump监听本机，将来自ip 192.168.200.1，tcp端口为22的数据，保存输出到tcpdump.log，用作将来数据分析
解：
	tcpdump -i ens33 host 192.168.200.1 and port 22 >> /opt/interview_questions/tcpdump.log
```

```shell
#头条面试题
问题：
	常用的Nginx有哪些，用来做什么？
解：
	rewrite模块：实现重写功能
	access模块：来源控制
	ssl模块：安全加密
	ngx_http_gzip_module：网络传输压缩模块
	ngx_http_proxy_module：模块实现代理
	ngx_http_upstream_module：模块实现定义后端服务器列表
	ngx_cache_purge：实现缓存清除功能
	...
```

```shell
#腾讯面试题
问题：
	如果你是系统管理员，在进行linux系统权限划分时，应考虑哪些因素？
解：
	一.首先阐述linux权限的主要对象
	文件的权限的含义
	目录的权限的含义
		-r：目录内文件列表的查看权限，ls
		-w：可以在目录内新增，删除，复制，剪切文件，touch,rm,cp,mv
		-x：可以进入该目录，cd
	权限如何修改
		chmod [augo] [+-=] [rwx] filename
		chmod 644 filename
		-R：权限递归选项
	二.根据自己实际经验谈考虑因素
		1.注意权限分离，比如：工作中，linux系统权限和数据库权限不要在同一个部门
		2.权限最小原则，即在满足使用的前提下最少优先
		3.减少使用root账户，尽量使用普通用户+sudo提权进行日常操作
		4.重要的系统文件，比如/etc/passwd etc/shadow /etc/fstab /etc/sudoers 等，建议使用chattr(change attribute)进行锁定，需要操作时打开
		5.使用SUID,SGID,Sticky 设置特殊权限
		6.可以利用工具，比如chkrootkit/rootkit hunter检测rootkit脚本，rootkit是入侵者使用工具，在不察绝的情况下建立入侵系统 wget ftp://ftp.pangeia.com.br/pub/seg/pac/chkrootkit.tar.gz
		7.利用工具Tripwire检测文件系统的完整性
```

```shell
#权限操作思考题
1.用户tom对目录/home/test有读写执行权限，/home/test/hello是只读文件，问tom对hello文件能读吗？能修改吗？能删除吗？
答：yny
2.用户tom对目录/home/test有读写权限，/home/test/hello是只读文件，问tom对hello文件能读吗？能修改吗？能删除吗？
答：nnn
3.用户tom对目录/home/test有执行权限，/home/test/hello是只读文件，问tom对hello文件能读吗？能修改吗？能删除吗？
答：ynn
4.用户tom对目录/home/test有写执行权限，/home/test/hello是只读文件，问tom对hello文件能读吗？能修改吗？能删除吗？
答：yny
```

```shell
#腾讯面试题
问题：
	说明Centos7启动流程，并说明与Centos6相同和不同的地方
解： 
	略
```

```shell
#百度面试题
问题：
	列举Linux高级命令，至少六个
解：
	netstat //网络状态监控
	top //系统运行状态
	lsblk //查看硬盘分区
	df -lh //查看磁盘存储状态
	mkfs //格式化分区
	ps -aux //查看进程 
	systemctl //系统服务管理
	firewall-cmd //防火墙管理
	chkconfig //查看服务启动状态
	chattr //给重要文件加锁
```

```shell
#瓜子面试题
问题：
	linux 查看内存，io读写，磁盘存储，端口占用，进程查看命令是什么?
解：
	top
	iotop
	df -lh
	netstat -tunlp
	ps -aux
```

```shell
#美团面试题
问题：
	使用linux命令计算t2.txt第二列的和并输出
	t2.txt
	张三 40
	李四 50
	王五 60
解：
	cat t2.txt | awk -F " " '{sum+=$2} END {print sum}'
```

```shell
#百度面试题
问题：
	shell脚本里如何检查一个文件是否存在，并给出提示
解：
	if [ -f $1 ]
	then
        echo "文件存在"
	else
        echo "文件不存在"
	fi
问题：
	用shell写一个脚本，对文本t2.txt中无序的一列数字排序，并将总和输出
	4
	3
	2
	7
	5
	8
	5
	1
	3
	10
解：
	sort -n t3.txt | awk '{sum+=$0;print$0} END {print "总和="sum}'
```

```shell
#金山面试题
问题：
	请用指令写出查找当前文件夹/home下所有文件内容中包含字符"cat"的文件名称
解：
	grep -r "cat" /home | cut -d : -f 1
问题：
	请写出统计/home目录下所有文件的个数和所有文件总行数的指令
解：
	find /home/jerry -name "*.*" | wc -l
	find /home/jerry -name "*.*" | xargs wc -l
```

```shell
#滴滴面试题
问题：
	列出你了解的web服务器负载架构
解：
	Nginx
	Haproxy
	Keepalived
	LVS
问题：
	每天晚上10点30分，打包站点目录/var/spool/mail备份到/home目录下，每次备份按时间生成不同的备份包，比如按照年月日时分秒
解：
	1.编写备份脚本
	#!/bin/bash
	filename=`date +%Y-%m-%d_%H%M%S`
	cd /var/spool
	tar -zcf /home/${filename}.tar.gz mail/
	2.添加执行权限
	chmod u+x backup_mail.sh
	3.编辑crond
	crontab -e
	30 20 * * * /root/backup_mail.sh
```

```shell
#瓜子面试题
问题：
	如何优化Linux系统
解：
	一.对linux的架构的优化和原则分析
		1.架构优化 略
		2.原则分析
			网络
			磁盘io
			文件
			安全性
			防火墙
			内存
	二.对linux系统本身的优化-规则
		原则：具体场景具体分析
		1.不用root，使用sudo提升权限
		2.定时自动更新服务时间，使用
		3.配置yum源，指向国内镜像(清华，163)
		4.配置合理的防火墙策略，打开必要的端口，关闭不必要的端口
		5.打开最大文件数(调整文件描述的数量)
			vim /etc/profile 
			ulimit -SHn 65535
		6.配置合理的监控策略
		7.配置合理的系统重要文件的备份策略
		8.对安装的软件进行优化，比如nginx，apache
		9.内核参数进行优化 /etc/sysctl.conf
		10.锁定一些重要的系统文件
			chattr /etc/passwd 
			chattr /etc/shadow
			chattr /inittab
		11.禁用不必要的服务
			setup
			ntsysv
```

