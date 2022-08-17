# Linux 笔记

## 网络连接的三种方法

- 桥接模式，虚拟系统可以和外部系统进行通讯，但是容易造成IP冲突
- NAT模式，网络地址转换模式，虚拟系统可以和外部系统进行通讯，不会造成IP冲突
- 主机模式，独立的系统，不与外部发生联系

## 虚拟机克隆

- 方式一 
  - 直接拷贝一份安装好的虚拟机文件
- 方式二
  - 使用VMware的克隆操作
  - 克隆时需要先关闭Linux系统

## 虚拟机快照

- 利用VMware快照管理功能，可以快速将系统恢复到之前正常运行的状态

## 虚拟机的迁移和删除

- 在VMware安装的虚拟机的本质是文件，想要对虚拟机进行迁移或者删除操作，只需要对相对应的虚拟机文件进行操作

## 安装VMtools

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

## 使用 vmtools 设置共享文件夹

- 步骤

  - 打开虚拟机设置中的选项，设置共享文件的路径和总是启用即可

  - centos 下共享文件夹路径为 /mnt/hgfs

- 注意事项和细节说明
  - 在实际开发中，windows 和 linux 不在同一台机器上，需要通过远程方式实现文件的上传下载

Linux 目录结构

- linux 的文件系统采用层级式树状目录结构，在此结构的最上层是根目录 **/** ，然后在此目录下创建其他目录
- 在linux中，一切皆文件
  - 在linux中，硬件会被映射为一个个的文件进行管理